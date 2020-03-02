package com.ufu.crar;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.ufu.crar.service.CrarService;
import com.ufu.crar.tfidf.Document;
import com.ufu.crar.tfidf.TFIDFCalculator;
import com.ufu.crar.tfidf.ngram.NgramTfIdf;
import com.ufu.crar.to.Baseline;
import com.ufu.crar.to.Bucket;
import com.ufu.crar.to.ContentType.ContentTypeEnum;
import com.ufu.crar.to.MetricResult;
import com.ufu.crar.to.Pair;
import com.ufu.crar.to.Post;
import com.ufu.crar.to.PostRestTransfer;
import com.ufu.crar.to.SoContentWordVector;
import com.ufu.crar.to.Tags.TagEnum;
import com.ufu.crar.to.ThreadContent;
import com.ufu.crar.to.UserEvaluation;
import com.ufu.crar.util.CrarUtils;
import com.ufu.crar.util.CrokageComposer;
import com.ufu.crar.util.LuceneSearcher;
import com.ufu.crar.util.SearcherParams;

public class AppAuxSolutionBuilder {

	@Autowired
	public CrarService crarService;

	@Autowired
	public CrarUtils crarUtils;

	
	@Autowired
	protected TFIDFCalculator tfidfCalculator;
	
	@Autowired
	protected CrokageComposer crokageComposer;
	
	@Autowired
	protected ApplicationContext appContext;
	
	/*@Autowired
	protected LuceneSearcher luceneSearcher;*/
	
	//app variables
	
	
	
	/*@Value("${TMP_DIR}")
	public String tmpDir;*/
	
	@Value("${CRAR_HOME}")
	public String CRAR_HOME;
	
	@Value("${luceneSearchAsClient}")
	public Boolean luceneSearchAsClient;
	
	/*@Value("${TMP_DIR}")
	public String TMP_DIR;*/
	
	@Value("${BIKER_ANSWERS_SUMMARIES_TEST}")
	public String BIKER_ANSWERS_SUMMARIES_TEST;
	
	
	@Value("${RECOMMENDED_ANSWERS_QUERIES_CACHE}")
	public String RECOMMENDED_ANSWERS_QUERIES_CACHE;
	
	@Value("${CRAR_HOME_DATA_FOLDER}")
	public String CRAR_HOME_DATA_FOLDER;
		
	@Value("${RACK_OUTPUT_QUERIES_FILE}")
	public String RACK_OUTPUT_QUERIES_FILE;
	
	
	
	@Value("${NLP2API_OUTPUT_QUERIES_FILE}")
	public String NLP2API_OUTPUT_QUERIES_FILE;
	
	@Value("${GROUND_TRUTH_THREADS_FOR_QUERIES}")
	public String GROUND_TRUTH_THREADS_FOR_QUERIES;
	
	@Value("${GROUND_TRUTH_ANSWERS_FOR_QUERIES}")
	public String GROUND_TRUTH_ANSWERS_FOR_QUERIES;
	
	
	
	@Value("${action}")
	public String action;
	
	@Value("${serverUrlGetCandidadeBuckets}")
	public String serverUrlGetCandidadeBuckets;
	
	@Value("${serverUrlGetCandidadeThreads}")
	public String serverUrlGetCandidadeThreads;
	
	@Value("${serverUrlGetAllBuckets}")
	public String serverUrlGetAllBuckets;
	
	@Value("${serverUrlGetAllThreads}")
	public String serverUrlGetAllThreads;
	
	public String subAction;

	public String obs;

	public Integer numberOfAPIClasses;
	
	public Integer[] topkArr;
	
	public Integer limitQueries;
	
	public Boolean onlyAnswersWithClasses;
		
	public String dataSet;
	
	public Integer cutoff;
	
	
	
	
	/*@Value("${topSimilarContentsAsymRelevanceNumber}")
	public Integer topSimilarContentsAsymRelevanceNumber;*/
	
	
	
	@Value("${saveQueryMetric}")
	public Boolean saveQueryMetric;
	
	
	
		
	protected long initTime;
	protected long endTime;
	protected Set<String> extractedAPIs;
	protected List<String> queries;
	protected List<String> processedQueries;
	protected Map<Integer,Set<String>> rackQueriesApisMap;
	//protected Map<String,Set<String>> rackReformulatedQueriesApis;  // because output from rack is reformulated. This map contains the real output.
	protected Map<Integer,Set<String>> bikerQueriesApisClassesMap;
	protected Map<Integer,Set<String>> bikerQueriesApisClassesAndMethodsMap;
	protected Map<Integer,Set<String>> nlp2ApiQueriesApisMap;
	public Map<String,Set<Integer>> bigMapApisAnswersIds;
	protected Map<String,Set<Integer>> filteredSortedMapAnswersIds;
	protected Map<String,Set<Integer>> groundTruthSelectedQueriesAnswersIdsMap;
	protected Map<String, Set<Integer>> groundTruthSelectedQueriesQuestionsIdsMap;
	protected Map<String,Set<Integer>> queriesAndSOIdsWithoutClassesMap;
	protected Map<String,double[]> soContentWordVectorsMap;
	protected Map<String,Double> soIDFVocabularyMap;
	protected Map<String,SoContentWordVector> wordVectorsMap;
	protected Map<Integer, Map<String,SoContentWordVector>> wordVectorsMapByTag;
	protected Map<String,Double> queryIDFVectorsMap;
	protected Map<Integer,String> allQuestionsIdsTitlesMap;
	protected Map<Integer,String> allThreadsIdsContentsMap;
	protected Map<Integer,String> allAnswersIdsContentsParentContentsMap;
	protected Map<Integer,Integer> allAnswersWithUpvotesIdsParentIdsMap;
	protected Map<Integer,Integer> topAnswersWithUpvotesIdsParentIdsMap;
	protected Map<String,List<Bucket>> candidateBucketsMap;
	
	protected Map<String,List<Bucket>> candidateBucketsJavaMap;
	protected Map<String,List<Bucket>> candidateBucketsPhpMap;
	protected Map<String,List<Bucket>> candidateBucketsPythonMap;
	
	
	protected Map<Integer,Bucket> allBucketsWithUpvotesMap;
	
	protected Map<Integer,Bucket> allBucketsWithUpvotesMapJava;
	protected Map<Integer,Bucket> allBucketsWithUpvotesMapPython;
	protected Map<Integer,Bucket> allBucketsWithUpvotesMapPhp;
	
	
	protected Map<Integer,Map<Integer,Bucket>> allBucketsWithUpvotesMapByTag;
	protected Map<Integer,Map<Integer,ThreadContent>> allValidThreadsMapByTag;
	//protected Map<Integer,Bucket> allQuestionsWithUpvotesAndAnswersMap;
	protected Map<Integer,Bucket> allThreadsForAnswersWithUpvotesAndCodeBucketsMap;
	protected Set<Integer> topClassesRelevantAnswersIds;
	protected Set<Integer> topApiScoredAnswersIds;
	protected Map<Integer,String> threadsForUpvotedAnswersWithCodeIdsTitlesMap;
	public List<String> wordsAndVectorsLines;
	//protected Map<Integer,Double> questionsIdsScores;
	//protected Map<Integer,Double> bucketsIdsBigSetScores;
	protected Map<Integer,Double> bucketsIdsSmallSetScores;
	protected Map<Integer,Double> bucketsIdsScores;
	protected Set<String> allWordsSetForBuckets;
	protected List<String> bikerTopMethods;
	protected Set<String> bikerTopClasses;
	protected Map<String,Integer> methodsCounterMap;
	protected Map<Integer,Integer> topicsCounterMap;
	protected Map<String,Integer> classesCounterMap;
	protected Map<Integer,Double> topAnswerParentPairsAnswerIdScoreMap; 
	
	protected Map<String,Set<Integer>> filteredAnswersWithApisIdsMap;
	protected Map<String,Set<Integer>> luceneTopIdsMap;
	protected Map<String,Set<Integer>> topThreadsAnswersIdsMap;
	protected Map<String,Set<Integer>> topAsymIdsMap;
	protected Map<String,Set<Integer>> topTFIDFAnswersIdsMap;
	protected Map<String,Set<Integer>> topMergeIdsMap1;
	protected Map<String,Set<Integer>> topMergeIdsMap2;
	protected Map<String,Set<Integer>> topAnswersIdsMap;
	protected Map<Integer,Set<String>> upvotedPostsIdsWithCodeApisMap;
	protected Map<Integer, Set<String>> recommendedApis;
	
	
	protected ArrayList<Document> documents;
	//public Integer contentTypeTFIDF;
	//public Integer contentTypeSemanticSim; 
	public Integer luceneMoreThreadsNumber; 
	public Integer numberOfPostsInfoToMatchAsymmetricSimRelevance;
	protected String currentQuery;
	protected Set<Bucket> candidateBuckets;
	protected Map<Integer,Float> bm25ScoreAnswerIdMap;
	protected List<Integer> allPostsByTagIds;
	protected Integer tagId;
	protected float k;
	protected float b;
	protected LuceneSearcher luceneSearcher;
	
	protected LuceneSearcher luceneSearcherThreadsJava;
	protected LuceneSearcher luceneSearcherThreadsPython;
	protected LuceneSearcher luceneSearcherThreadsPhp;
	
	protected LuceneSearcher luceneSearcherAnswersJava;
	protected LuceneSearcher luceneSearcherAnswersPython;
	protected LuceneSearcher luceneSearcherAnswersPhp;
	
	protected SearcherParams searchParam;
	protected Set<String> topClasses;
	protected Joiner.MapJoiner mapJoiner;
	protected Client client;
	protected Gson gson;
	protected WebResource webResourceGetCandidadeBuckets;
	protected WebResource webResourceGetCandidadeThreads;
	protected WebResource webResourceGetAllBuckets;
	protected WebResource webResourceGetAllThreads;
	protected boolean useLemma;
	
	public void initializeVariables() throws Exception {
		subAction = subAction !=null ? subAction.toLowerCase().trim(): null;
		dataSet = dataSet !=null ? dataSet.toLowerCase().trim(): null;
		soIDFVocabularyMap = new LinkedHashMap<>();
		wordsAndVectorsLines = new ArrayList<>();
		soContentWordVectorsMap = new LinkedHashMap<>();
		allQuestionsIdsTitlesMap = new LinkedHashMap<>();
		allThreadsIdsContentsMap = new LinkedHashMap<>();
		allAnswersIdsContentsParentContentsMap= new LinkedHashMap<>();
		//questionsIdsScores = new LinkedHashMap<>();
		//bucketsIdsBigSetScores = new LinkedHashMap<>();
		bucketsIdsSmallSetScores= new LinkedHashMap<>();
		bucketsIdsScores = new LinkedHashMap<>();
		candidateBucketsMap= new LinkedHashMap<>();
		
		candidateBucketsJavaMap= new LinkedHashMap<>();
		candidateBucketsPhpMap= new LinkedHashMap<>();
		candidateBucketsPythonMap= new LinkedHashMap<>();
		
		wordVectorsMap = new LinkedHashMap<>();
		allWordsSetForBuckets = new LinkedHashSet<>();
		methodsCounterMap = new LinkedHashMap<>();
		topicsCounterMap =  new LinkedHashMap<>();
		classesCounterMap = new LinkedHashMap<>();
		topAnswersWithUpvotesIdsParentIdsMap = new LinkedHashMap<>(); 
		topAnswerParentPairsAnswerIdScoreMap = new LinkedHashMap<>(); 
		groundTruthSelectedQueriesAnswersIdsMap = new LinkedHashMap<>();
		groundTruthSelectedQueriesQuestionsIdsMap = new LinkedHashMap<>();
		allBucketsWithUpvotesMap = new LinkedHashMap<>(); 
		allBucketsWithUpvotesMapByTag= new LinkedHashMap<>();
		allValidThreadsMapByTag = new LinkedHashMap<>();
		
		allBucketsWithUpvotesMapJava = new LinkedHashMap<>();
		allBucketsWithUpvotesMapPython = new LinkedHashMap<>();
		allBucketsWithUpvotesMapPhp = new LinkedHashMap<>();
		
		//allQuestionsWithUpvotesAndAnswersMap= new LinkedHashMap<>();
		allThreadsForAnswersWithUpvotesAndCodeBucketsMap = new LinkedHashMap<>();
		topClassesRelevantAnswersIds = new LinkedHashSet<>();
		rackQueriesApisMap = new LinkedHashMap<>();
		bikerQueriesApisClassesMap = new LinkedHashMap<>();
		bikerQueriesApisClassesAndMethodsMap = new LinkedHashMap<>();
		nlp2ApiQueriesApisMap = new LinkedHashMap<>();
		filteredAnswersWithApisIdsMap = new LinkedHashMap<>();
		topThreadsAnswersIdsMap = new LinkedHashMap<>();
		topAsymIdsMap = new LinkedHashMap<>();
		topTFIDFAnswersIdsMap = new LinkedHashMap<>();
		topMergeIdsMap1 = new LinkedHashMap<>();
		topMergeIdsMap2 = new LinkedHashMap<>();
		topAnswersIdsMap = new LinkedHashMap<>();
		luceneTopIdsMap = new LinkedHashMap<>();
		bm25ScoreAnswerIdMap = new LinkedHashMap<>();
		recommendedApis = new LinkedHashMap<>();
		allPostsByTagIds = new ArrayList<>();
		documents = new ArrayList<>();
		threadsForUpvotedAnswersWithCodeIdsTitlesMap = new LinkedHashMap<>();
		upvotedPostsIdsWithCodeApisMap = new LinkedHashMap<>();
		processedQueries = new ArrayList<>();
		candidateBuckets = new LinkedHashSet<>();
		k=0f;
		b=0f;
		bigMapApisAnswersIds = new LinkedHashMap<>();
		filteredSortedMapAnswersIds= new LinkedHashMap<>();
		queries = new ArrayList<>();
		queriesAndSOIdsWithoutClassesMap  = new LinkedHashMap<>();
		luceneSearcher=new LuceneSearcher();
		luceneSearcherThreadsJava = new LuceneSearcher();
		luceneSearcherThreadsPython = new LuceneSearcher();
		luceneSearcherThreadsPhp = new LuceneSearcher();
		
		luceneSearcherAnswersJava = new LuceneSearcher();
		luceneSearcherAnswersPython = new LuceneSearcher();
		luceneSearcherAnswersPhp = new LuceneSearcher();
		
		
		topClasses = new LinkedHashSet<>();
		mapJoiner = Joiner.on(",").withKeyValueSeparator("=");
		wordVectorsMapByTag = new LinkedHashMap<>();
		client = Client.create();
		
		gson = new GsonBuilder()
				  .excludeFieldsWithoutExposeAnnotation()
				  .create();
		useLemma=false;
		webResourceGetCandidadeBuckets = client.resource(serverUrlGetCandidadeBuckets);
		webResourceGetCandidadeThreads = client.resource(serverUrlGetCandidadeThreads);
		webResourceGetAllBuckets = client.resource(serverUrlGetAllThreads);
		webResourceGetAllThreads = client.resource(serverUrlGetAllThreads);
		
		//best found parameters
		numberOfAPIClasses=30;
		//contentTypeTFIDF=3;
		//contentTypeSemanticSim= 2;
		cutoff=5;
		topkArr = new Integer[]{10,50,100};
		onlyAnswersWithClasses=false;
		obs="running baseline";
		subAction="nlp2api|rack|biker|generatetableforapproaches|evaluatecover";
		dataSet="java-test-57";
	}
	

	protected void configureTagIdByDataSet() {
		if (dataSet.contains("php")) {
			tagId= TagEnum.PHP.getId();
		}
		if (dataSet.contains("conala") || dataSet.contains("python")) {
			tagId=TagEnum.Python.getId();
		}else { 
			tagId= TagEnum.Java.getId();
		}
		System.out.println("Setting tagId: "+tagId);
	}

	
	protected int isApiFound_K(List<String> rapis, ArrayList<String> gapis) {
		// check if correct API is found
		int found = 0;
		outer: for (int i = 0; i < rapis.size(); i++) {
			String api = rapis.get(i);
			for (String gapi : gapis) {
				//if (gapi.endsWith(api) || api.endsWith(gapi)) {
				if (gapi.equals(api)) {
					//System.out.println("Gold:"+gapi+"\t"+"Result:"+api);
					found = 1;
					break outer;
				}
			}
		}
		return found;
	}
	
	protected int isFound_K(ArrayList<Integer> recommendedIds, ArrayList<Integer> goldSetIds) {
		int found = 0;
		outer: for (int i = 0; i < recommendedIds.size(); i++) {
			Integer id = recommendedIds.get(i);
			if(goldSetIds.contains(id)) {
				found = 1;
				break outer;
			}
		}
		return found;
	}

	
	protected double getRecallK(List<String> rapis, ArrayList<String> gapis, int K) {
		// getting recall at K
		K = rapis.size() < K ? rapis.size() : K;
		double found = 0;
		for (int index = 0; index < K; index++) {
			String api = rapis.get(index);
			if (isApiFound(api, gapis)) {
				found++;
			}
		}
		return found / gapis.size();
	}
	
	
	protected boolean isApiFound(String api, List<String> gapis) {
		// check if the API can be found
		for (String gapi : gapis) {
			//if (gapi.endsWith(api) || api.endsWith(gapi)) {
			if (gapi.equals(api)) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean isFound(Integer id, List<Integer> goldSetIds) {
		return goldSetIds.contains(id);
	}
	
	
	protected double getRRank(List<String> rapis, ArrayList<String> gapis) {
		double rrank = 0;
		for (int i = 0; i < rapis.size(); i++) {
			String api = rapis.get(i);
			if (isApiFound(api, gapis)) {
				rrank = 1.0 / (i + 1);
				break;
			}
		}
		return rrank;
	}
	
	protected double getRRankV2(List<Integer> recommendedIds, ArrayList<Integer> goldSetIds) {
		double rrank = 0;
		for (int i = 0; i < recommendedIds.size(); i++) {
			Integer id = recommendedIds.get(i);
			if (goldSetIds.contains(id)) {
				rrank = 1.0 / (i + 1);
				break;
			}
		}
		return rrank;
	}
	
	
	
	protected double getAvgPrecisionK(List<String> rapis, ArrayList<String> gapis) {
		double linePrec = 0;
		double found = 0;
		for (int index = 0; index < rapis.size(); index++) {
			String api = rapis.get(index);
			if (isApiFound(api, gapis)) {
				found++;
				linePrec += (found / (index + 1));
			}
		}
		if (found == 0)
			return 0;

		return linePrec / found;
	}
	
	protected double getAvgPrecisionKV2(List<Integer> recommendedIds, List<Integer> goldSetIds) {
		double linePrec = 0;
		double found = 0;
		for (int index = 0; index < recommendedIds.size(); index++) {
			Integer id = recommendedIds.get(index);
			if (goldSetIds.contains(id)) {
				found++;
				linePrec += (found / (index + 1));
			}
		}
		if (found == 0)
			return 0;

		return linePrec / found;
	}
	
	/*
	 * the lower, the better. It is the effort to find the relevant document for the query.   
	 */
	public double getQueryEffectiveness(List<Integer> recommendedIds, List<Integer> goldSetIds) {
		int pos = 0;
		for(Integer recommendedId: recommendedIds) {
			pos++;
			if(goldSetIds.contains(recommendedId)) {
				break;
			}
			
		}
		if(pos==0) { //not found
			pos=recommendedIds.size(); //set max value
		}
		return pos;
	}
	
	
	

	protected double getRecallK(List<String> rapis, ArrayList<String> gapis) {
		// getting recall at K
		double found = 0;
		for (int index = 0; index < rapis.size(); index++) {
			String api = rapis.get(index);
			if (isApiFound(api, gapis)) {
				found++;
			}
		}
		return found / gapis.size();
	}
	
	protected double getRecallKV2(List<Integer> recommendedIds, ArrayList<Integer> goldSetIds) {
		// getting recall at K
		double found = 0;
		for (int index = 0; index < recommendedIds.size(); index++) {
			Integer id = recommendedIds.get(index);
			if (goldSetIds.contains(id)) {
				found++;
			}/*else {
				if(currentQuery.contains("How can I insert an element in array at a given position?")) {
					System.out.println("not found: "+id);
				}
				
			}*/
		}
		return found / goldSetIds.size();
	}
	
	
	protected double getRecallKV3(List<Integer> recommendedIds, ArrayList<Integer> goldSetIds) {
		// getting recall at K
		double found = 0;
		for (Integer id: goldSetIds) {
			if (recommendedIds.contains(id)) {
				found++;
			}/*else {
				if(currentQuery.contains("How can I insert an element in array at a given position?")) {
					System.out.println("not found: "+id);
				}
			}*/
		}
		return found / goldSetIds.size();
	}




	



	public String assembleContentsByThreads(Set<Integer> parentIds, List<Bucket> answersBucketsWithCode) {
		int count=0;
		List<Post> parents = crarService.findPostsById(new ArrayList<Integer>(parentIds));
		System.out.println("number of threads(parents): "+parents.size());
		StringBuilder stringBuilder = new StringBuilder();
		for(Post parent: parents) {
			count++;
			if(count%10000==0) {
				System.out.println(count+" threads processed...");
			}
			stringBuilder.append("\n");
			stringBuilder.append(parent.getId()+" ");
			if(!StringUtils.isBlank(parent.getProcessedTitle())) {
				stringBuilder.append(parent.getProcessedTitle());
			}
			if(!StringUtils.isBlank(parent.getProcessedBody())) {
				stringBuilder.append(" ");
				stringBuilder.append(parent.getProcessedBody());
			}
			if(!StringUtils.isBlank(parent.getProcessedCode())) {
				stringBuilder.append(" ");
				stringBuilder.append(parent.getProcessedCode());
			}
			
			List<Bucket> answersBucketsWithCodeForParent = answersBucketsWithCode.stream()
					.filter(e-> e.getParentId().equals(parent.getId()))
					.collect(Collectors.toList());
			
			for(Bucket answer: answersBucketsWithCodeForParent) {
				if(!StringUtils.isBlank(answer.getProcessedBody())) {
					stringBuilder.append(" ");
					stringBuilder.append(answer.getProcessedBody());
				}
				if(!StringUtils.isBlank(answer.getProcessedCode())) {
					stringBuilder.append(" ");
					stringBuilder.append(answer.getProcessedCode());
				}
			}
		}
		return stringBuilder.toString();
	}

    /*
	protected void loadUpvotedAnswersIdsWithCodeContentsAndParentContents() {
		long initTime2 = System.currentTimeMillis();
		List<Bucket> buckets = crarService.getUpvotedAnswersIdsContentsAndParentContents();
		
		Set<Integer> acceptedAnswersIds = buckets.stream()
		  .filter(b -> b.getAcceptedAnswerId()!=null)
		  .map(e -> e.getAcceptedAnswerId())
		  .collect(Collectors.toSet());
		
		List<Bucket> acceptedAnswersBuckets = crarService.getBucketsByIds(acceptedAnswersIds);
		
		Map<Integer,String> acceptedAnswersBucketsMap = new LinkedHashMap<>();
		for(Bucket acceptedAnswersBucket:acceptedAnswersBuckets) {
			acceptedAnswersBucketsMap.put(acceptedAnswersBucket.getId(), acceptedAnswersBucket.getAcceptedAnswerBody());
		}
		
		Set<Integer> parentsIdsOfPostsWhoseThreadDoesNotHaveAcceptedAnswers = buckets.stream()
				  .filter(b -> b.getAcceptedAnswerId()==null)
				  .map(e -> e.getParentId())
				  .collect(Collectors.toSet());
		
		Map<Integer,Bucket> mostUpVotedBucketsMap = new LinkedHashMap<>();
		for(Integer parentId: parentsIdsOfPostsWhoseThreadDoesNotHaveAcceptedAnswers) {
			mostUpVotedBucketsMap.put(parentId,crarService.getMostUpvotedAnswerForQuestionId2(parentId));
		}
		
				
		for(Bucket bucket:buckets) {
			if(bucket.getAcceptedAnswerId()!=null && !bucket.getId().equals(bucket.getAcceptedAnswerId())) {
				bucket.setAcceptedAnswerBody(acceptedAnswersBucketsMap.get(bucket.getAcceptedAnswerId()));
			}else if(bucket.getAcceptedAnswerId()==null ) {
				Bucket mostUpvoted = mostUpVotedBucketsMap.get(bucket.getParentId());
				if(!mostUpvoted.getId().equals(bucket.getId())) {
					bucket.setAcceptedAnswerBody(mostUpvoted.getProcessedBody());
				}
			}
				
			allBucketsWithUpvotesMap.put(bucket.getId(), bucket);
		}
		crarUtils.reportElapsedTime(initTime2,"getUpvotedAnswersIdsContentsAndParentContents");
		buckets=null;
		acceptedAnswersBuckets=null;
		acceptedAnswersBucketsMap=null;
		mostUpVotedBucketsMap=null;
	}*/
	
	
	protected void loadUpvotedAnswersIdsWithCodeContentsAndParentContents2() {
		long initTime2 = System.currentTimeMillis();
		List<Bucket> buckets = crarService.getUpvotedAnswersIdsContentsAndParentContents(tagId);
		String threadContent=null;
		for(Bucket bucket:buckets) {
			threadContent = allThreadsIdsContentsMap.get(bucket.getParentId());
			bucket.setThreadContent(threadContent);
			//allThreadsForAnswersWithUpvotesAndCodeBucketsMap.put(bucket.getId(), bucket);
			
			allBucketsWithUpvotesMap.put(bucket.getId(), bucket);
		}
		crarUtils.reportElapsedTime(initTime2,"getUpvotedAnswersIdsContentsAndParentContents");
		buckets=null;
		
	}
	
	protected void loadUpvotedAnswersIdsWithCodeContentsAndParentContents() {
		long initTime2 = System.currentTimeMillis();
		List<Bucket> buckets = crarService.getUpvotedAnswersIdsContentsAndParentContents(tagId);
		//CrarUtils.generateHotTopicsMap(buckets);
		for(Bucket bucket:buckets) {
			allBucketsWithUpvotesMap.put(bucket.getId(), bucket);
		}
		crarUtils.reportElapsedTime(initTime2,"loading all SO Q&A pairs for tag: "+tagId);
		buckets=null;
		
	}
	
	
	
	/*protected Set<Integer> getAllThreadsForAnswersWithUpvotesAndCodeBucketsMap() {
		Set<Integer> keys = allBucketsWithUpvotesMap.keySet();
		Set<Integer> threadsIds = new LinkedHashSet<>();
		for(Integer key: keys) {
			threadsIds.add(allBucketsWithUpvotesMap.get(key).getParentId());
		}
		return threadsIds;	
	}*/


	/*
	protected void loadThreadsForUpvotedAnswersWithCodeIdsTitlesMap() {
		threadsForUpvotedAnswersWithCodeIdsTitlesMap = crarService.getThreadsIdsTitlesForUpvotedAnswersWithCode();
		
	}
*/
	
	

	
	public void loadGroundTruthSelectedQueries() throws IOException {
		if(groundTruthSelectedQueriesAnswersIdsMap.isEmpty()) {
			System.out.println("Loading ground truth for dataset = "+dataSet);
			groundTruthSelectedQueriesAnswersIdsMap.putAll(CrarUtils.readQueriesAndIds(GROUND_TRUTH_ANSWERS_FOR_QUERIES+"-"+dataSet+".txt"));
			//groundTruthSelectedQueriesQuestionsIdsMap.putAll(CrarUtils.readFileToMap(GROUND_TRUTH_THREADS_FOR_QUERIES+"-"+dataSet+".txt"));
			queries = new ArrayList<String>(groundTruthSelectedQueriesAnswersIdsMap.keySet());
			System.out.println("Queries read. Size: "+queries.size()+ " - first: "+queries.get(0));
			//System.out.println("Queries read. Size: "+queries.size());
			
		}
	}


	public List<String> loadGroundTruthSelectedQueries(String languageDataSet, Map<String, Set<Integer>> groundTruthMap) throws IOException {
			System.out.println("Loading ground truth for dataset = "+languageDataSet);
			Map<String, Set<Integer>> dataMap = CrarUtils.readQueriesAndIds(GROUND_TRUTH_ANSWERS_FOR_QUERIES+"-"+languageDataSet+".txt");
			System.out.println("Loaded. First query = "+new ArrayList<String>(dataMap.keySet()).get(0));
			groundTruthMap.putAll(dataMap);
			
			//groundTruthSelectedQueriesQuestionsIdsMap.putAll(CrarUtils.readFileToMap(GROUND_TRUTH_THREADS_FOR_QUERIES+"-"+dataSet+".txt"));
			//queries = groundTruthMap.keySet().stream().collect(Collectors.toList());
			/*for(String key: new TreeSet<String>(groundTruthMap.keySet())) {
				  queries.add(key);
			}*/
			
			queries = new ArrayList<String>(groundTruthMap.keySet());
			//System.out.println("All "+languageDataSet+" queries size: "+queries.size());
			System.out.println("Queries read. Size: "+queries.size());
			return queries;
	
	}
	
	
	protected void loadGroundTruthSelectedQueriesForQuestions(String languageDataSet, Map<String, Set<Integer>> groundTruthMap) throws IOException {
		//System.out.println("Loading ground truth for dataset = "+languageDataSet);
		Map<String, Set<Integer>> dataMap = CrarUtils.readQueriesAndIds(GROUND_TRUTH_THREADS_FOR_QUERIES+"-"+languageDataSet+".txt");
		//System.out.println("Loaded. First query = "+new ArrayList<String>(dataMap.keySet()).get(0));
		groundTruthMap.putAll(dataMap);
		queries = new ArrayList<String>(groundTruthMap.keySet());
		System.out.println("Ground truth read. Size: "+groundTruthMap.size());
	}
	

	protected void loadGroundTruthSelectedQueriesForQuestionsOld(String languageDataSet, Map<String, Set<Integer>> groundTruthMap) throws IOException {
		System.out.println("Loading ground truth for dataset = "+languageDataSet);
		Map<String, Set<Integer>> dataMap = CrarUtils.readQueriesAndIds(GROUND_TRUTH_ANSWERS_FOR_QUERIES+"-"+languageDataSet+".txt");
		System.out.println("Loaded. First query = "+new ArrayList<String>(dataMap.keySet()).get(0));
		
		for(String query: dataMap.keySet()) {
			Set<Integer> answersIds = dataMap.get(query);
			//Set<Integer> parentIds = new LinkedHashSet<>();
			//List<Post> answers = crarService.findPostsById(new ArrayList<>(answersIds));
			
			Set<Integer> parentIds = crarService.findParentIdsByPostIds(answersIds);
			
			/*Set<Integer> parentIds = answers.stream()
				.map(e -> e.getParentId())
				.collect(Collectors.toCollection(LinkedHashSet::new));*/
			/*for(Post answer: answers) {
				Post parent = crarService.findPostById(answer.getParentId());
				parentIds.add(parent.getId());
			}*/
			groundTruthMap.put(query, parentIds); 
			
		}
		
	}





	protected List<String> getBikerTopMethods(Integer key) {
		if(bikerQueriesApisClassesAndMethodsMap==null) {
			return null;
		}
		List<String> topMethods = new ArrayList<>();
		Set<String> classesAndMethods = bikerQueriesApisClassesAndMethodsMap.get(key);
		for(String classAndMethod: classesAndMethods) {
			String parts[] = classAndMethod.split("\\."); 
			topMethods.add(parts[1]);
		}
		//System.out.println("Top methods from biker: "+topMethods);
		return topMethods;
	}



/*
	protected void addVectorsToSoContentWordVectorsMap(Set<Integer> answersWithTopFrequentAPIsIds) throws Exception {
		Set<String> contents = new LinkedHashSet<>();
		String content;
		for(Integer answerId:answersWithTopFrequentAPIsIds) {
			content = allAnswersIdsContentsParentContentsMap.get(answerId);
			contents.add(content);
		}
		crarUtils.readVectorsFromSOMapForWords(soContentWordVectorsMap,contents,wordsAndVectorsLines);
		
		contents=null;
				
	}*/
	
	
	/*private void readSoContentWordVectorsForAllWordsOld() throws IOException {
		readSOContentWordAndVectorsLines();
		
		long initTime = System.currentTimeMillis();
		System.out.println("Parsing vectors...");
		CrarUtils.getVectorsFromLines(wordsAndVectorsLines,soContentWordVectorsMap);
		wordsAndVectorsLines=null;
		CrarUtils.reportElapsedTime(initTime,"parsing vectors");
		
	}*/

	
	

	protected void loadUpvotedPostsWithCodeApisToDb() throws Exception {
		crarService.loadPostsApisMap(upvotedPostsIdsWithCodeApisMap);
		
		crarService.loadUpvotedPostsWithCodeApisToDb(upvotedPostsIdsWithCodeApisMap);
		
	}
	
/*
	protected void loadAPIIndexToDataBase() throws IOException {
		loadInvertedIndexFileOld();
		
		crarService.loadAPIIndexToDataBase(bigMapApisAnswersIds);
	}*/


	

	/*
	 * Set -Xss200m in VM parameters or java.lang.StackOverflowError: null is thrown 
	 */
	protected void populateApiAnswersTable() throws IOException {
		//get posts containing code. Date parameter is optional for tests purpose.
		//String startDate = "2016-01-01"; 
		String startDate = null;
		long initTime = System.currentTimeMillis();
		System.out.println("Processing posts to generate inverted index file...");
		List<Post> answersWithPreCode =  crarService.getAnswersWithCode(startDate,1);
		
		String postsWithoutAPICalls = "";
		int postsWithoutAPICallsCounter=0;
		
		Map<String,Set<Integer>> bigMapApisAnswersIds = new LinkedHashMap<>();
		int i=1;
		for(Post answer:answersWithPreCode) {
			
			/*if(answer.getId().equals(50662268)) {
				System.out.println();
			}*/
			
			if(i%100000==0) {
				System.out.println("Processing post "+i);
			}
			
			//extract apis from answer. For each api, add the api in a map, together with the other references for that post
			Set<String> codeSet=null;
			try {
				codeSet = crarUtils.extractClassesFromCode(answer.getBody());
			} catch(StackOverflowError e){
				System.out.println("Error in post "+answer.getId()+ " - disconsidering...");
				continue;
	        } catch (Exception e) {
				System.out.println("Exception here: "+answer);
				e.printStackTrace();
			}
			
			//ArrayList<String> codeClasses = new ArrayList(codeSet);
			if(codeSet.isEmpty()) {
				i++;
				postsWithoutAPICallsCounter++;
				if(postsWithoutAPICallsCounter%10==0) {
					postsWithoutAPICalls+="\n";
				}
				postsWithoutAPICalls+=answer.getId()+",";
				continue;
			}
			
			for(String api: codeSet) {
				if(bigMapApisAnswersIds.get(api)==null){
					LinkedHashSet<Integer> idsSet = new LinkedHashSet<>();
					idsSet.add(answer.getId());
					bigMapApisAnswersIds.put(api, idsSet);
				
				}else {
					/*if(api.equals("MainActivity")) {
						System.out.println();
					}*/
					Set<Integer> currentIds = bigMapApisAnswersIds.get(api);
					currentIds.add(answer.getId());
				}
				
			}
			i++;
		}
		
		
		crarService.populateApiAnswersTable(bigMapApisAnswersIds);
		
		
		
		/*
		System.out.println("Done processing posts to generate inverted index file.");
		System.out.println("Number of posts containing API calls: "+bigMapApisAnswersIds.size()+ ". Now printing files...");
		CrarUtils.printBigMapIntoFile(bigMapApisAnswersIds,BIG_MAP_INVERTED_INDEX_APIS_FILE_PATH);
		System.out.println("Done printing files.");*/
		crarUtils.reportElapsedTime(initTime,"populateApiAnswersTable");
	}





	protected List<String> getQueriesFromFile(String fileName) throws IOException {
		List<String> queries = new ArrayList<>();
		// reading output from generated file
		List<String> queriesAndApis = Files.readAllLines(Paths.get(fileName), Charsets.UTF_8);
		Iterator<String> it = queriesAndApis.iterator();
		
		while(it.hasNext()) {
			String query = "";
			if(it.hasNext()) {
				query = it.next();
				queries.add(query);
				if(it.hasNext()) {
					it.next(); //APIs are in even lines
				}
			}
			
		}
		
		
		return queries;
	}
	
	
	protected void reportAnswers(Set<Integer> topKRelevantAnswersIds, Map<Integer, Double> topSimilarAnswers) {
		List<Integer> listIds = new ArrayList<>(topKRelevantAnswersIds);
		int listSize = listIds.size();
		int k = listSize > 10? 10:listSize;
		listIds=listIds.subList(0, k);
		System.out.println("Top "+k+" similar answers to query");
		
		for(Integer id:listIds) {
			System.out.println(id.toString()+ " -score:"+topSimilarAnswers.get(id));
			
		}
		
	}


	
	/*
	 * Extended version of getQueriesAndApisFromFile where the generated queries can be equal
	 */
	protected void getQueriesAndApisFromFileMayContainDupes(Map<Integer, Set<String>> queriesApis, String fileName) throws IOException {
		if(queriesApis==null) {
			queriesApis = new LinkedHashMap<>();
		}
		
		// reading output from generated file
		List<String> queriesAndApis = Files.readAllLines(Paths.get(fileName), Charsets.UTF_8);
		if(limitQueries!=null) {
			queriesAndApis = queriesAndApis.subList(0, 2*limitQueries);
		}
		Iterator<String> it = queriesAndApis.iterator();
		int key = 1;
		while(it.hasNext()) {
			String query = "";
			String queryApis = "";
			if(it.hasNext()) {
				query = it.next();
				if(it.hasNext()) {
					queryApis = it.next(); //APIs are in even lines
				}
			}
			
			queryApis = queryApis.replaceAll("\\s+"," ");
			
			List<String> rankedApis = Arrays.asList(queryApis.split(" ")).stream().map(String::trim).collect(Collectors.toList());
			int k = numberOfAPIClasses;
			if (rankedApis.size() < k) {
				//logger.warn("The number of retrieved APIs for query is lower than the number of rack classes set as parameter. Ajusting the parameter to -->" + rankedApis.size() + " apis");
				k = rankedApis.size();
			}
			
			Set<String> rankedApisSet = new LinkedHashSet<String>(rankedApis);
			CrarUtils.setLimit(rankedApisSet, k);
			queriesApis.put(key, rankedApisSet);
			//System.out.println("discovered classes for query: "+query+ " ->  " + queriesApis.get(key));
			key++;
		}
			
	}



	
	protected List<UserEvaluation> loadExcelGroundTruthQuestionsAndLikerts() throws IOException {
		String file1 = "questionsLot1and2withoutWeightsAdjuster.xlsx";
		Integer likertsResearcher1AfterAgreementColumn = 3;
		Integer likertsResearcher2AfterAgreementColumn = 4;
		
		List<UserEvaluation> evaluationsList = new ArrayList<>();
		crarUtils.readXlsxToEvaluationList(evaluationsList,file1,likertsResearcher1AfterAgreementColumn,likertsResearcher2AfterAgreementColumn,null);
		//generateMetricsForEvaluations(evaluationsList);
		return evaluationsList;
	}

	

	



	public List<String> readInputQueries() throws Exception {
		//long initTime = System.currentTimeMillis();
		String fileName = "";
		
		fileName = GROUND_TRUTH_ANSWERS_FOR_QUERIES+"-"+dataSet+".txt";
					
		Map<String,Set<Integer>> queriesAndSOIdsMap = CrarUtils.readQueriesAndIds(fileName);
		List<String> queries = new ArrayList<String>(queriesAndSOIdsMap.keySet());
		return queries;
	
	}

	/*protected void getPropertyValueFromLocalFile() {
		Properties prop = new Properties();
		InputStream input = null;
		useProxy = false;
		try {

			input = new FileInputStream(pathFileEnvFlag);

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			String useProxyStr = prop.getProperty("useProxy");
			if (!StringUtils.isBlank(useProxyStr)) {
				useProxy = new Boolean(useProxyStr);
			}
			bikerHome = prop.getProperty("BIKER_HOME");
			virutalPythonEnv= prop.getProperty("virutalPythonEnv");
			environment = prop.getProperty("environment");
			
			String msg = "\nEnvironment property (useProxy): ";
			if (useProxy) {
				msg += "with proxy";
			} else {
				msg += "no proxy";
			}

			System.out.println(msg+"\nnEnvironment: "+environment);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}*/
	

	
	protected Map<Integer, Set<String>> getGoldSetByEvaluations(Map<Integer, Set<String>> goldSetMap, List<UserEvaluation> evaluationsWithBothUsersScales,String fileName) throws FileNotFoundException {
		//long initTime = System.currentTimeMillis();
		if(goldSetMap==null) {
			goldSetMap = new LinkedHashMap<>();
		}
		Map<String,Set<String>> goldSetQueriesApis = new LinkedHashMap<>();
		int assessed = 0;
		String query = null;
		Integer postId=null;
		
		try {
			
			for(UserEvaluation evaluation:evaluationsWithBothUsersScales){
				int likert1 = evaluation.getLikertScaleUser1();
				int likert2 = evaluation.getLikertScaleUser2();
				query = evaluation.getQuery();
				int diff = Math.abs(likert1 - likert2);
				if(diff>1) {
					continue;
				}
				assessed++;
				
				double meanFull = (likert1+likert2)/(double)2;
				double meanLikert=0d;
				meanLikert = CrarUtils.round(meanFull,2);
				
				if(meanLikert>=4) { 
					
					Post post = crarService.findPostById(evaluation.getPostId());
					postId=post.getId();
					Set<String> codeSet = crarUtils.extractClassesFromCode(post.getBody());
					if(codeSet.isEmpty()) {
						continue;
					}
					
					if(goldSetQueriesApis.get(query)==null){
						goldSetQueriesApis.put(query, codeSet);
					
					}else {
						Set<String> currentClasses = goldSetQueriesApis.get(query);
						currentClasses.addAll(codeSet);
					}
					
				}
			}
			
			System.out.println("\nTotal evaluations: "+evaluationsWithBothUsersScales.size() + "\nConsidered with likert difference <=1: "+assessed);
			
				
		} catch (Exception e) {
			System.out.println("Error in postId: "+postId);
			e.printStackTrace();
		} 
		
		//crarUtils.reportElapsedTime(initTime,"generateMetricsForEvaluations");
		
		//transform from query
		Set<String> keySet = goldSetQueriesApis.keySet();
		
		int keyNumber =1;
		for(String keyQuery: keySet) {
			Set<String> apis = goldSetQueriesApis.get(keyQuery);
			goldSetMap.put(keyNumber, apis);
			keyNumber++;
		}
		
		
		//update input queries to be used by approaches
		try (PrintWriter out = new PrintWriter(fileName)) {
		    for(String query2: keySet) {
		    	out.println(query2);
		    }
			
		}
		
		
		return goldSetMap;
	}
	

	
	
	protected Map<Integer, Set<String>> getRecommendedApis() {
		Map<Integer, Set<String>> recommendedApis = new LinkedHashMap<>();
		//are the same for all
		//Set<String> queriesSet = new LinkedHashSet<>(queries);  
		Set<Integer> keys = null; 
		String approaches = subAction;
		
		int numApproaches = 0;
		if(!MapUtils.isEmpty(rackQueriesApisMap)) {
			numApproaches++;
			keys = rackQueriesApisMap.keySet();
		}
		if(!MapUtils.isEmpty(bikerQueriesApisClassesMap)) {
			numApproaches++;
			keys = bikerQueriesApisClassesMap.keySet();
		}
		if(!MapUtils.isEmpty(nlp2ApiQueriesApisMap)) {
			numApproaches++;
			keys = nlp2ApiQueriesApisMap.keySet();
		}
				
		if(numApproaches>1){ 
			approaches = subAction.replace("|generatetableforapproaches", "");
			String apisArrOrder[] = approaches.split("\\|");
			//List<String> queriesList = new ArrayList<>(queriesSet);
			String bikerApi = null;
			String nlp2ApiApi = null;
			String rackApi = null;
			
			
			outer: for(Integer keyNum: keys) {
				Set<String> recommendedApisSet = new LinkedHashSet<>();
				Set<String> bikerApisSet = null;
				Set<String> rackApisSet = null;
				Set<String> nlpApisSet = null;
				
				Iterator<String> bikerIt = null;
				Iterator<String> rackIt = null;
				Iterator<String> nlpIt = null;
				
				
				if(!MapUtils.isEmpty(rackQueriesApisMap)) {
					rackApisSet = rackQueriesApisMap.get(keyNum);
					rackIt = rackApisSet.iterator();
				}
				if(!MapUtils.isEmpty(bikerQueriesApisClassesMap)) {
					bikerApisSet = bikerQueriesApisClassesMap.get(keyNum);
					bikerIt = bikerApisSet.iterator();
				}
				if(!MapUtils.isEmpty(nlp2ApiQueriesApisMap)) {
					nlpApisSet = nlp2ApiQueriesApisMap.get(keyNum);
					nlpIt = nlpApisSet.iterator();
				}
				
				bikerApi = "";
				nlp2ApiApi = "";
				rackApi = "";
				
				
				int i=1;
				String chosenApi = null;
				//int key =1;
				while(true) { 
					//collect the ith API from each approach and merge
					chosenApi = null;
					boolean stop = true;
					
					if(bikerIt!=null && bikerIt.hasNext()) {
						bikerApi = bikerIt.next();
						stop = false;
					}
					
					if(nlpIt!=null && nlpIt.hasNext()) {
						nlp2ApiApi= nlpIt.next();
						stop = false;
					}
					if(rackIt!=null && rackIt.hasNext()) {
						rackApi = rackIt.next();
						stop = false;
					}
					
					if(stop) { //recommenders were not able to produce enough apis
						break;
					}
					
					//uses the order of the apis to merge the recommendation
					if(numApproaches==3) {
						if(bikerApi.equals(nlp2ApiApi) || bikerApi.equals(rackApi)) { //if 2 are equal, choose it, else one of each until numberOfAPIClasses
							chosenApi = bikerApi;
							recommendedApisSet.add(chosenApi);
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
							i++;
							
						} else if(nlp2ApiApi.equals(rackApi)) {
							chosenApi = nlp2ApiApi;
							recommendedApisSet.add(chosenApi);
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
							i++;
						} else {  //all different, follows the order
							
							
						}
						
					} 
					//two approaches or three approaches: rack+biker(+nlp), rack+nlp(+biker), biker+rack(+nlp), biker+nlp(+rack), nlp+rack(+biker), nlp+biker(+rack)
					if(apisArrOrder[0].equals("rack") && apisArrOrder[1].equals("biker")) {
						
						recommendedApisSet.add(rackApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						recommendedApisSet.add(bikerApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
							
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(nlp2ApiApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
					}else if(apisArrOrder[0].equals("rack") && apisArrOrder[1].equals("nlp2api")) {
						
						recommendedApisSet.add(rackApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						recommendedApisSet.add(nlp2ApiApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(bikerApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
					}else if(apisArrOrder[0].equals("biker") && apisArrOrder[1].equals("rack")) {
						
						recommendedApisSet.add(bikerApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						recommendedApisSet.add(rackApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						//nlp
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(nlp2ApiApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
						
					}else if(apisArrOrder[0].equals("biker") && apisArrOrder[1].equals("nlp2api")) {
						
						recommendedApisSet.add(bikerApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						recommendedApisSet.add(nlp2ApiApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(rackApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
					}else if(apisArrOrder[0].equals("nlp2api") && apisArrOrder[1].equals("rack")) {
						
						recommendedApisSet.add(nlp2ApiApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						recommendedApisSet.add(rackApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(bikerApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
					}else if(apisArrOrder[0].equals("nlp2api") && apisArrOrder[1].equals("biker")) {
						recommendedApisSet.add(nlp2ApiApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						recommendedApisSet.add(bikerApi);
						i++;
						if(recommendedApisSet.size()==numberOfAPIClasses) {
							break;
						}
						
						
						if(apisArrOrder.length>2) {
							recommendedApisSet.add(rackApi);
							i++;
							if(recommendedApisSet.size()==numberOfAPIClasses) {
								break;
							}
						}
						
					}
			
				}
				recommendedApis.put(keyNum, recommendedApisSet);
				//key++;
				
			}
			
		}else if(!MapUtils.isEmpty(rackQueriesApisMap)) {
			recommendedApis.putAll(rackQueriesApisMap);
		}else if(!MapUtils.isEmpty(bikerQueriesApisClassesMap)){
			recommendedApis.putAll(bikerQueriesApisClassesMap);
		}else {
			recommendedApis.putAll(nlp2ApiQueriesApisMap);
		}
		//crarUtils.reportElapsedTime(initTime,"getRecommendedApis");
		return recommendedApis;
		
	}


	protected Map<String, Set<Integer>> processBingResult(List<String> allLinesCrokage) {
		String[] parts;
		String query="";
		String questionId;
		Set<Integer> questionsIds = new LinkedHashSet<>();
		Map<String,Set<Integer>> queriesAndSOIdsMap = new LinkedHashMap<>();
		
		for(String line: allLinesCrokage) {
			if(line.trim().startsWith("\"originalQuery\":")) {
				if(!questionsIds.isEmpty()) {
					queriesAndSOIdsMap.put(query, questionsIds);
					questionsIds = new LinkedHashSet<>();
				}
				
				parts = line.trim().split("\"originalQuery\": \"");
				parts = parts[1].split(" site:stackoverflow.com\"");
				query = parts[0].replace("java ", "");
				
			}
			if(line.trim().startsWith("\"url\": \"https://stackoverflow.com/questions/")) {
				parts = line.trim().split("\"url\": \"https://stackoverflow.com/questions/");
				parts = parts[1].split("/");
				questionId = parts[0];
				if(crarUtils.isNumeric(questionId)) {
					questionsIds.add(new Integer(questionId));
				}
				
			}
			
		}
		return queriesAndSOIdsMap;
	}





	
	

	protected void analyzeGroundTruthClasses() throws Exception {
		
		//String suffix = "-training-58.txt";
		//String suffix = "-test-57.txt";
		
		//Map<String,Set<Integer>> allQueriesAndSOIdsMapTraining  = crarUtils.readQueriesAndIds(CRAR_HOME+"/data/groundTruthAnswersForQueries-java-training-58.txt");
		Map<String,Set<Integer>> allQueriesAndSOIdsMapTest  = crarUtils.readQueriesAndIds(CRAR_HOME+"/data/groundTruthAnswersForQueries-java-test-57.txt");
		
		Map<String,Set<Integer>> allQueriesAndSOIdsMap = new LinkedHashMap<>();
		//allQueriesAndSOIdsMap.putAll(allQueriesAndSOIdsMapTraining);
		allQueriesAndSOIdsMap.putAll(allQueriesAndSOIdsMapTest);
		//allQueriesAndSOIdsMap.put
		//Map<String,Set<Integer>> queriesAndSOIdsContainingClassesMap  = new LinkedHashMap<>();
		//Map<String,Set<Integer>> queriesAndSOIdsWithoutClassesMap  = new LinkedHashMap<>();
		
		Set<String> queries = allQueriesAndSOIdsMap.keySet();
		
		int withoutClass=0;
		
		for(String query: queries) {  //for each query 
			
			Set<Integer> allAnswersIds = allQueriesAndSOIdsMap.get(query);
			
			List<Post> answers = crarService.findPostsById(new ArrayList<Integer>(allAnswersIds));
			
			for(Post answer:answers) {
				/*if(answer.getId().equals(24969273)) {
					System.out.println();
				}*/
				if(answer.getScore()<1) {
					//System.out.println("Answer without score: "+answer.getId());
				}
				
				try {
					//verify if post contain APIs
					Set<String> classes = crarUtils.extractClassesFromProcessedCode(answer.getCode());
					if(classes.isEmpty()) {
						//System.out.println("Answer without class: "+answer.getId());
						withoutClass++;
						
						Set<Integer> ids = queriesAndSOIdsWithoutClassesMap.get(query);
						if(ids==null) {
							ids = new LinkedHashSet<Integer>();
						}
						ids.add(answer.getId());
						queriesAndSOIdsWithoutClassesMap.put(query, ids);
					}
					
				} catch (Throwable e2) {
					System.out.println("Error trying to extract classes from post: "+answer.getId());
				}
				
			
			}
			
		}
		System.out.println("Without classes: "+withoutClass);
		System.out.println("Queries without classes: "+queriesAndSOIdsWithoutClassesMap);
		
		
	}
	
	
	






	
	
	
	
	public String prepareQueryForGoogleCrawler(String query, String tag) {
		String completeQuery = "";
		
		Boolean containTagToken = Pattern.compile(".*\\b"+tag+"\\b.*").matcher(query.toLowerCase()).find();
		
		if(!containTagToken) {
			completeQuery += tag+" ";
		}
		//filter for SO posts is executed in another method: executeGoogleSearch
		completeQuery += query;
		
		return completeQuery;
	}
	
	public String prepareQueryForBing(String query) {
		String completeQuery = "";
		
		Boolean containJavaToken = Pattern.compile(".*\\bjava\\b.*").matcher(query.toLowerCase()).find();
		
		if(!containJavaToken) {
			completeQuery += "java ";
		}
		
		completeQuery += query + " site:stackoverflow.com";
		
		return completeQuery;
	}


	
	
	/*protected Set<Integer> executeStackExchangeSearch(String query, Integer numberOfGoogleResults2) {
		 StackExchangeApiQueryFactory queryFactory = StackExchangeApiQueryFactory.newInstance("gJMxDoCH9XhURvF7a3F4Kg((",StackExchangeSite.STACK_OVERFLOW);  
         Paging paging = new Paging(1, 20);  
         String filterName = "default";  
         List<String> tagged = new ArrayList<String>();  
         tagged.add("java");  
         List<String> nottagged = new ArrayList<String>();  
         PagedList<Question> questions = queryFactory  
                   .newAdvanceSearchApiQuery()
                   .withFilter(filterName)
                   .withSort(QuestionSortOrder.MOST_RELEVANT)
                   .withQuery(query)
                   .withMinAnswers(1)  
                   .withMinViews(100)
                   .withTags(tagged).list();  
         
         Set<Integer> soQuestionsIds = new LinkedHashSet<>();
		
         int i=0;
         for(Question question:questions) {
        	System.out.println(question.getQuestionId()+ " - "+question.getTitle());
        	soQuestionsIds.add(((Long)(question.getQuestionId())).intValue());
        	i++;
        	if(i==20) {
        		break;
        	}
         }
		return soQuestionsIds;
	}
*/




	protected void checkConditions() throws Exception {
		
		boolean exists1 = true;
		
		
		/*File file2 = new File(NLP2API_OUTPUT_QUERIES_FILE);
		boolean exists2 = file2.exists();
		if(!exists1) {
			throw new Exception("File "+file2.getAbsolutePath()+ " must exist. Has it been provided ? ");
		}*/
	
		
	}







	/*
	protected void loadAllAnswersIdsContentsAndParentContentsMap() {
		//load threads whose questions has score > 0 
		List<Bucket> buckets = crarService.getUpvotedAnswersIdsContentsAndParentContents(tagId);
		System.out.println("buckets recuperados: "+buckets.size());
		int count=0;
		for(Bucket bucket: buckets) {
			count++;
			if(count%10000==0) {
				System.out.println(count+" buckets processed...");
			}
			String content = loadBucketContent(bucket,5);
			allAnswersIdsContentsParentContentsMap.put(bucket.getId(), content);
			
		}
		
	}*/

	protected String loadThreadContent(ThreadContent bucket, Integer numberOfPostsInfoToMatch) {
		StringBuffer stringBuffer = new StringBuffer();
		
		if(numberOfPostsInfoToMatch.equals(ContentTypeEnum.title.getId())) {
			stringBuffer.append(bucket.getTitle());
		
		}else if(numberOfPostsInfoToMatch.equals(ContentTypeEnum.title_questionBody_body_code.getId())) {
			stringBuffer.append(bucket.getTitle());
			stringBuffer.append(" ");
			stringBuffer.append(bucket.getQuestionBody());
			stringBuffer.append(" ");
			stringBuffer.append(bucket.getAnswersBody());
			stringBuffer.append(" ");
			stringBuffer.append(bucket.getCode());
		
		}else if(numberOfPostsInfoToMatch.equals(ContentTypeEnum.question_body_answers_body.getId())) {
			stringBuffer.append(bucket.getQuestionBody());
			stringBuffer.append(" ");
			stringBuffer.append(bucket.getAnswersBody());
		
		}else if(numberOfPostsInfoToMatch.equals(ContentTypeEnum.code.getId())) {
			stringBuffer.append(bucket.getCode());
		}
		
		
		return stringBuffer.toString();
	}

	protected String loadBucketContent(Bucket bucket,Integer numberOfPostsInfoToMatch) {
		StringBuffer stringBuffer = new StringBuffer();
		
		if(numberOfPostsInfoToMatch.equals(ContentTypeEnum.body.getId())) {
			if(useLemma) {
				stringBuffer.append(bucket.getProcessedBodyLemma());
			}else {
				stringBuffer.append(bucket.getProcessedBody());
			}
		}else if(numberOfPostsInfoToMatch.equals(ContentTypeEnum.title_questionBody_body.getId())) {
			if(useLemma) {
				stringBuffer.append(bucket.getParentProcessedTitleLemma());
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getParentProcessedBodyLemma());
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedBodyLemma());
			}else {
				stringBuffer.append(bucket.getParentProcessedTitle());
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getParentProcessedBody());
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedBody());
			}
		
		}else if(numberOfPostsInfoToMatch.equals(ContentTypeEnum.title_questionBody_body_code.getId())) {
			if(useLemma) {
				stringBuffer.append(bucket.getParentProcessedTitleLemma());
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getParentProcessedBodyLemma());
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedBodyLemma());
			}else {
				stringBuffer.append(bucket.getParentProcessedTitle());
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getParentProcessedBody());
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedBody());
			}
			stringBuffer.append(" ");
			stringBuffer.append(bucket.getProcessedCode());
		
		}else if(numberOfPostsInfoToMatch.equals(ContentTypeEnum.body_code.getId())) {
			if(useLemma) {
				stringBuffer.append(bucket.getProcessedBodyLemma());
			}else {
				stringBuffer.append(bucket.getProcessedBody());
			}
			stringBuffer.append(" ");
			stringBuffer.append(bucket.getProcessedCode());
			
			
		}else if(numberOfPostsInfoToMatch.equals(ContentTypeEnum.title_body.getId())) {
			if(useLemma) {
				stringBuffer.append(bucket.getParentProcessedTitleLemma());
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedBodyLemma());
			}else {
				stringBuffer.append(bucket.getParentProcessedTitle());
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedBody());
			}
			
		}else if(numberOfPostsInfoToMatch.equals(ContentTypeEnum.code.getId())) {
			stringBuffer.append(bucket.getProcessedCode());
		}
		
		
/*		
		switch(numberOfPostsInfoToMatch) {
		
				
			case 33:
				if(!StringUtils.isBlank(bucket.getParentProcessedTitleLemma())) {
					stringBuffer.append(bucket.getParentProcessedTitleLemma());
				}
				if(!StringUtils.isBlank(bucket.getParentProcessedBodyLemma())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getParentProcessedBodyLemma());
				}
				if(!StringUtils.isBlank(bucket.getProcessedBodyLemma())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getProcessedBodyLemma());
				}
				break;		
			
				
			case 22:
				if(!StringUtils.isBlank(bucket.getParentProcessedTitleLemma())) {
					stringBuffer.append(bucket.getParentProcessedTitleLemma());
				}
				if(!StringUtils.isBlank(bucket.getProcessedBodyLemma())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getProcessedBodyLemma());
				}
				break;		
			
			case 2:
				if(!StringUtils.isBlank(bucket.getParentProcessedTitle())) {
					stringBuffer.append(bucket.getParentProcessedTitle());
				}
				if(!StringUtils.isBlank(bucket.getProcessedBody())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getProcessedBody());
				}
				break;	
				
			case 1:
				stringBuffer.append(bucket.getParentProcessedTitleLemma());
				break;	
			
			case 3:
				if(!StringUtils.isBlank(bucket.getParentProcessedTitle())) {
					stringBuffer.append(bucket.getParentProcessedTitle());
				}
				if(!StringUtils.isBlank(bucket.getParentProcessedBody())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getParentProcessedBody());
				}
				if(!StringUtils.isBlank(bucket.getProcessedBody())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getProcessedBody());
				}
				break;
				
			
			
			case 4: 
				if(!StringUtils.isBlank(bucket.getParentProcessedTitle())) {
					stringBuffer.append(bucket.getParentProcessedTitle());
				}
				if(!StringUtils.isBlank(bucket.getParentProcessedBody())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getParentProcessedBody());
				}
				if(!StringUtils.isBlank(bucket.getProcessedBody())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getProcessedBody());
				}
				if(!StringUtils.isBlank(bucket.getProcessedCode())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getProcessedCode());
				}
				break;
				
			case 5:
				if(!StringUtils.isBlank(bucket.getParentProcessedTitle())) {
					stringBuffer.append(bucket.getParentProcessedTitle());
				}
				if(!StringUtils.isBlank(bucket.getParentProcessedBody())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getParentProcessedBody());
				}
				if(!StringUtils.isBlank(bucket.getProcessedBody())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getProcessedBody());
				}
				if(!StringUtils.isBlank(bucket.getProcessedCode())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getProcessedCode());
				}
				if(!StringUtils.isBlank(bucket.getParentProcessedCode())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getParentProcessedCode());
				}
				break;
			
			case 6:
				if(!StringUtils.isBlank(bucket.getParentProcessedTitle())) {
					stringBuffer.append(bucket.getParentProcessedTitle());
				}
				if(!StringUtils.isBlank(bucket.getParentProcessedBody())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getParentProcessedBody());
				}
				if(!StringUtils.isBlank(bucket.getProcessedBody())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getProcessedBody());
				}
				if(!StringUtils.isBlank(bucket.getParentProcessedCode())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getParentProcessedCode());
				}
				break;
				
			case 7:
				if(!StringUtils.isBlank(bucket.getParentProcessedBody())) {
					stringBuffer.append(bucket.getParentProcessedBody());
				}
				if(!StringUtils.isBlank(bucket.getParentProcessedCode())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getParentProcessedCode());
				}
				break;
				
			case 8: 
				if(!StringUtils.isBlank(bucket.getParentProcessedTitle())) {
					stringBuffer.append(bucket.getParentProcessedTitle());
				}
				if(!StringUtils.isBlank(bucket.getProcessedBody())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getProcessedBody());
				}
				if(!StringUtils.isBlank(bucket.getProcessedCode())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getProcessedCode());
				}
				break;
			
			case 9:
				stringBuffer.append(bucket.getProcessedTitleLemma());
				break;
				
			case 10:
				stringBuffer.append(bucket.getProcessedTitleLemma());
				stringBuffer.append(" "+bucket.getProcessedBodyLemma());
				break;	
				
			case 11:
				stringBuffer.append(bucket.getProcessedBodyLemma());
				break;
			
				
				
			case 13:
				stringBuffer.append(bucket.getProcessedTitle());
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedBody());
				break;	
				
			case 14:
				stringBuffer.append(bucket.getProcessedTitleLemma());
				stringBuffer.append(" "+bucket.getProcessedBodyLemma());
				if(!StringUtils.isBlank(bucket.getProcessedCode())) {
					stringBuffer.append(" ");
					stringBuffer.append(bucket.getProcessedCode());
				}
				break;
				
			case 15:
				stringBuffer.append(bucket.getProcessedBodyLemma());
				stringBuffer.append(" "+bucket.getProcessedCode());
				break;	
				
		}
		*/
			
	
		return stringBuffer.toString();
	}
	
	/*protected String loadBucketContent(Bucket bucket) {
		StringBuffer stringBuffer = new StringBuffer();
		if(!StringUtils.isBlank(bucket.getParentProcessedTitle())) {
			stringBuffer.append(bucket.getParentProcessedTitle());
		}
		if(!StringUtils.isBlank(bucket.getParentProcessedBody())) {
			stringBuffer.append(" ");
			stringBuffer.append(bucket.getParentProcessedBody());
		}
		if(!StringUtils.isBlank(bucket.getParentProcessedCode())) {
			stringBuffer.append(" ");
			stringBuffer.append(bucket.getParentProcessedCode());
		}
		if(!StringUtils.isBlank(bucket.getProcessedBody())) {
			stringBuffer.append(" ");
			stringBuffer.append(bucket.getProcessedBody());
		}
		if(!StringUtils.isBlank(bucket.getProcessedCode())) {
			stringBuffer.append(" ");
			stringBuffer.append(bucket.getProcessedCode());
		}
		return stringBuffer.toString();
	}*/

	/*
	protected void loadAllQuestionsIdsContentsAndAcceptedOrMostUpvotedAnswerContentsMap() {
		//load threads whose questions has score > 0 
		List<Bucket> buckets = crarService.getQuestionsProcessedTitlesBodiesCodes();
		for(Bucket bucket: buckets) {
			StringBuffer stringBuffer = new StringBuffer();
			if(!StringUtils.isBlank(bucket.getProcessedTitle())) {
				stringBuffer.append(bucket.getProcessedTitle());
			}
			if(!StringUtils.isBlank(bucket.getProcessedBody())) {
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedBody());
			}
			if(!StringUtils.isBlank(bucket.getProcessedCode())) {
				stringBuffer.append(" ");
				stringBuffer.append(bucket.getProcessedCode());
			}
			
			Post answer=null;
			//append content of accepted answer or most upvoted answer
			if(bucket.getAcceptedAnswerId()!=null) {
				answer = crarService.findPostById(bucket.getAcceptedAnswerId());
				if(answer!=null) {
					if(StringUtils.isBlank(answer.getProcessedCode())){
						answer=null;
					}
				}
			}	
			if(answer==null) {	
			    //get answer with most upvotes
				answer = crarService.getMostUpvotedAnswerWithCodeForQuestion(bucket.getId());
			}
			if(answer==null) {
				System.out.println("no answer with code for question: "+bucket.getId());
				continue;
			}
			if(!StringUtils.isBlank(answer.getProcessedBody())) {
				stringBuffer.append(" ");
				stringBuffer.append(answer.getProcessedBody());
			}
			
			if(!StringUtils.isBlank(answer.getProcessedCode())) {
				stringBuffer.append(" ");
				stringBuffer.append(answer.getProcessedCode());
			}
			allAnswersIdsContentsParentContentsMap.put(bucket.getId(), stringBuffer.toString());
			
		}
		
	}*/


	protected void loadAllQuestionsIdsTitles() {
		long initTime = System.currentTimeMillis();
		allQuestionsIdsTitlesMap.putAll(crarService.getQuestionsIdsTitles());
		crarUtils.reportElapsedTime(initTime,"loadAllQuestionsIdsTitles");
	}
	
	
	
	protected int getApisForApproaches() throws Exception {
		System.out.println("loading APIs from the three API recommenders...");
		int numApproaches = 0;
		//long initTime = System.currentTimeMillis();
		
		//load ground truth answers
		/*if(dataSet.equals("selectedqueries-training49") && groundTruthSelectedQueriesAnswersIdsMap.isEmpty()){
			loadGroundTruthSelectedQueries();
		}*/
		
		//subAction is transformed to lowercase
		if(subAction.contains("rack")) {
			extractAPIsFromRACK();
			numApproaches++;
			//System.out.println("Metrics for RACK");
		}
		if(subAction.contains("biker")) {
			extractAPIsFromBIKER();
			numApproaches++;
			//System.out.println("Metrics for BIKER");
		}
		if(subAction.contains("nlp2api")) {
			extractAPIsFromNLP2Api();
			numApproaches++;
			//System.out.println("Metrics for nlp2api");
		}
		//System.out.println("Done loading queries from approaches.");
		//CrarUtils.reportElapsedTime(initTime,"getApisForApproaches");
		return numApproaches;
	}

	protected void extractAPIsFromNLP2Api() throws Exception {
		
		if(queries==null) {
			queries = readInputQueries();
		}
		
		getQueriesAndApisFromFileMayContainDupes(nlp2ApiQueriesApisMap,CRAR_HOME_DATA_FOLDER+dataSet+"-"+NLP2API_OUTPUT_QUERIES_FILE);
		
		
	}
	
	protected void extractAPIsFromRACK() throws Exception {
		if(queries==null) {
			queries = readInputQueries();
		}
		
		getQueriesAndApisFromFileMayContainDupes(rackQueriesApisMap,CRAR_HOME_DATA_FOLDER+dataSet+"-"+RACK_OUTPUT_QUERIES_FILE);
		
	}
	
	
	
	protected void extractAPIsFromBIKER() throws Exception {
		// BIKER
		if(queries==null) {
			queries = readInputQueries();
		}
		
		String outputPath=CRAR_HOME_DATA_FOLDER+"crokageDataSetBikerOutputTestJournal57.txt";
		
		int key = 1;
		// reading output from BIKER
		List<String> queriesWithApis = Files.readAllLines(Paths.get(outputPath), Charsets.UTF_8);
		if(limitQueries!=null) {
			queriesWithApis = queriesWithApis.subList(0, limitQueries);
		}
		for(String generatedLine: queriesWithApis) {
			String parts[] = generatedLine.split("=  ");
			List<String> rankedApis = Arrays.asList(parts[1].split("### ")).stream().map(String::trim).collect(Collectors.toList());
			rankedApis.remove("");
			
			int k = numberOfAPIClasses;
			if (rankedApis.size() < k) {
				k = rankedApis.size();
			}
			
			Set<String> rankedApisSetWithMethods = new LinkedHashSet<String>(rankedApis);
			bikerQueriesApisClassesAndMethodsMap.put(key, rankedApisSetWithMethods);
			
			Set<String> rankedApisSetClassesOnly = new LinkedHashSet<String>();
			for(String api: rankedApisSetWithMethods) {
				rankedApisSetClassesOnly.add(api.split("\\.")[0]);
			}
			
			CrarUtils.setLimit(rankedApisSetClassesOnly,k);
			bikerQueriesApisClassesMap.put(key, rankedApisSetClassesOnly);
		
			key++;
		}
		
	
		
	}






	

	protected Map<String, Set<Integer>> getGroundTruthSelectedQueriesQuestionsIdsMap() {
		//Map<String, Set<Integer>> groundTruthSelectedQueriesQuestionsIdsMap = new LinkedHashMap<>();
		
		for(String query: groundTruthSelectedQueriesAnswersIdsMap.keySet()) {
			//googleRecommendedResults.put(query, googleQueriesAndSOIdsMap.get(query));
			
			Set<Integer> answersIds = groundTruthSelectedQueriesAnswersIdsMap.get(query);
			Set<Integer> parentIds = new LinkedHashSet<>();
			List<Post> answers = crarService.findPostsById(new ArrayList<>(answersIds));
			
			for(Post answer: answers) {
				parentIds.add(answer.getParentId());
			}
			groundTruthSelectedQueriesQuestionsIdsMap.put(query, parentIds); 
			
		}
		return groundTruthSelectedQueriesQuestionsIdsMap;
	}


	public void mergeThreads(Map<String, Set<Integer>> mapMerge,Map<String, Set<Integer>> map1, Map<String, Set<Integer>> map2) {
		mapMerge.putAll(map1);
		Set<String> queries = map2.keySet();
		for(String query: queries) {
			/*Set<Integer> allIds = new LinkedHashSet<>();
			Set<Integer> map1Ids = map1.get(query);
			Set<Integer> map2Ids = map2.get(query);
			if(!CollectionUtils.isEmpty(map1Ids)) {
				mergeIds.addAll(otherIds);
			}
			mapMerge.put(query, mergeIds);*/
			if(map1.containsKey(query)) {
				mapMerge.get(query).addAll(map2.get(query));
			}else {
				mapMerge.put(query,map2.get(query));
			}
		}
		
	}

	
	protected Set<String> getAllWordsForBuckets(List<Bucket> answerBuckets) {
		Set<String> allWords = new LinkedHashSet<>();
		String line;
		String words[];
		for(Bucket bucket:answerBuckets) {
			line = bucket.getProcessedBody()+ " "+bucket.getProcessedCode(); 
			words = line.split("\\s+");
			allWords.addAll(Arrays.asList(words));
		}
		line=null;
		words=null;
		return allWords;
	}


	/*
	private Set<Integer> getAnswersForTFIDF(Set<Integer> candidateAnswersIds, String processedQuery) {
		Set<Bucket> candidateBuckets = new LinkedHashSet<>();
		for(Integer answerId: candidateAnswersIds) {
			candidateBuckets.add(allBucketsWithUpvotesMap.get(answerId));
		}
		
		String comparingContent;
		Map<Integer, Double> answersIdsScores = new LinkedHashMap<>();
		
		documents.clear();
		Document queryDocument = new Document(processedQuery, 0);
		documents.add(queryDocument);
		
		for(Bucket bucket:candidateBuckets) {
			comparingContent = loadBucketContent(bucket,2);
			Document document = new Document(comparingContent, bucket.getId());
			documents.add(document);
			bucket.setDocument(document);
		}
		
		Corpus corpus = new Corpus(documents);
		VectorSpaceModel vectorSpace = new VectorSpaceModel(corpus);
		
		for(Bucket bucket:candidateBuckets) {
			double tfIdfCosineSimScore = vectorSpace.cosineSimilarity(queryDocument, bucket.getDocument());
			bucket.setTfIdfCosineSimScore(tfIdfCosineSimScore);
			answersIdsScores.put(bucket.getId(), tfIdfCosineSimScore);
			
		}
		
		//sort scores in descending order 
		Map<Integer,Double> topSimilarAnswers = answersIdsScores.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		candidateAnswersIds.clear();
		candidateAnswersIds.addAll(topSimilarAnswers.keySet());
		corpus=null;
		vectorSpace=null;
		candidateBuckets=null;
		queryDocument=null;
		answersIdsScores = null;
		return candidateAnswersIds;
	}*/
	


	
	

	protected String getPostContent(Post post) {
		StringBuilder postContent = new StringBuilder();
		if(post.getPostTypeId().equals(1) && !StringUtils.isBlank(post.getProcessedTitle())) { //question
			postContent.append(post.getProcessedTitle());
		}
		/*if(!StringUtils.isBlank(post.getProcessedBodyLemma())) {
			postContent.append(" ");
			postContent.append(post.getProcessedBodyLemma());
		}*/
		/*if(post.getPostTypeId().equals(1) && !StringUtils.isBlank(post.getProcessedTitleLemma())) { //question
			postContent.append(post.getProcessedTitleLemma());
		}*/
		
		
		if(!StringUtils.isBlank(post.getProcessedBody())) {
			postContent.append(" ");
			postContent.append(post.getProcessedBody());
		}
		if(!StringUtils.isBlank(post.getProcessedCode())) {
			postContent.append(" ");
			postContent.append(post.getProcessedCode());
		}
		//String postContentStr = postContent.toString().replaceAll("\\b\\w{1,1}\\b\\s?", "");
		
		return StringUtils.normalizeSpace(postContent.toString());
	}

	/*
	protected Set<Integer> filterAnswersByAPIs(Set<String> topClasses, Set<Integer> relevantAnswersIds) {
		//long initTime = System.currentTimeMillis();
		
		//Set<Integer> answersWithTopFrequentAPIs = new LinkedHashSet<>();
		Set<AnswerParentPair> answerParentThreads = new LinkedHashSet<>(); 
		//List<AnswerParentPair> sortedPairs = new ArrayList<>();
		List<Integer> topNAnswersIds = new ArrayList<>();
		//Set<Integer> topNAnswersIds = new LinkedHashSet<>();
		
		for(String topClass:topClasses) {
			Set<Integer> answersIdsFromBigMap = filteredSortedMapAnswersIds.get(topClass);
			if(answersIdsFromBigMap==null) {
				//System.out.println("Answers not found for topClass: "+topClass);
				continue;
			}	
			for(Integer answerId: answersIdsFromBigMap) {
				//if(relevantAnswersIds.contains(answerId) && allBucketsWithUpvotesMap.containsKey(answerId)) { //filtered by lucene and is upvoted answer
				if(relevantAnswersIds.contains(answerId) && allBucketsWithUpvotesMap.containsKey(answerId)) { //filtered by lucene and is upvoted answer 
					//answersWithTopFrequentAPIs.add(answerId);
					AnswerParentPair answerParentPair = new AnswerParentPair(answerId, allBucketsWithUpvotesMap.get(answerId).getParentId());
					calculateApiScore(answerParentPair,topClasses);
					answerParentThreads.add(answerParentPair);
					topAnswerParentPairsAnswerIdScoreMap.put(answerId, answerParentPair.getApiScore());
				}
			}
		}
		
		if(!productionEnvironment) {
			System.out.println("Number of pairs(answer+parent) containing topClasses: "+answerParentThreads.size());
		}
		
		
		//int topApisScoredPairs = answerParentThreads.size()*topApisScoredPairsPercent/100;
		int topApisScoredPairs = topApisScoredPairsPercent;
		
		//sort by apiScore desc
		topNAnswersIds= answerParentThreads.stream()
				.sorted(Comparator.reverseOrder())
				.limit(topApisScoredPairs)
				.map(x -> x.getAnswerId())
				.collect(Collectors.toList());
		
		sortedPairs= topAnswerParentPairs.stream()
				.sorted(Comparator.comparing(AnswerParentPair::getApiScore).reversed())
				.limit(topApisScoredPairsPercent)
				//.map(x -> x.getAnswerId())
				.collect(Collectors.toList());
		
		
		Set<Integer> topNAnswersIdsSet = new LinkedHashSet<>(topNAnswersIds);
		//topApiScoredAnswersIds.addAll(topNAnswersIdsSet);
		
		for(Integer topAnswerId: topNAnswersIdsSet) {
			topAnswersWithUpvotesIdsParentIdsMap.put(topAnswerId, allAnswersWithUpvotesIdsParentIdsMap.get(topAnswerId));
		}
		
		
		topNAnswersIds=null;
		answerParentThreads=null;
		//CrarUtils.reportElapsedTime(initTime,"getAnswersForTopFrequentAPIs");
		
		return topNAnswersIdsSet;
	}
*/
	

/*
	private void checkPartialCoverage(int topk, Map<Integer, Set<Integer>> searchSpaceBucketsTopicsResults, Map<Integer, Set<Integer>> allPostsLinks) {
		Integer hitsMaxCrokage=0, hits10000Crokage=0, hits1000Crokage=0,hits500Crokage=0, hits100Crokage=0, hits50Crokage=0, hits20Crokage = 0, hits10Crokage = 0, hits5Crokage=0,hits1Crokage=0 ,hitOrderCrokage = 0;
		CrarUtils.reduceSetV2(recommendedResults, topk);
		int accuracy_sum = 0;
		double rrank_sum = 0;
		
		Set<Integer> nonMastersIds = recommendedResults.keySet();
	    int count=0;
	    for(Integer nonMasterId: nonMastersIds) {
	    	count++;
	    	Set<Integer> canditateMastersIds = recommendedResults.get(nonMasterId);
	    	hitOrderCrokage = getDuplicateOrder(nonMasterId, canditateMastersIds,allPostsLinks);
	    	
	    	double rrank = 0;
	    	
	    	if(hitOrderCrokage!=null) { //is found among top k
	    		accuracy_sum++;
	    		
	    		rrank = 1/(double)hitOrderCrokage;
	    		
	    		rrank_sum = rrank_sum + rrank;
	    	}
		}
	    

    	double hit_k= CrarUtils.round((double) accuracy_sum / nonMastersIds.size(),3);
		double mrr = CrarUtils.round(rrank_sum / nonMastersIds.size(),3);
		
		if(metricResult!=null) {
			metricResult.setHitK(hit_k);
			metricResult.setMrrK(mrr);
			
		}
		
		
		System.out.println("Results for: "+approach+" "
				+" - Hit@" + metricResult.getTopk() + ": " + hit_k
				+" - MRR@" + metricResult.getTopk() + ": " + mrr
				+ " - for top "+metricResult.getTopk());
		
	}*/
	

	protected void reportCommonMethods() {
		Map<String,Integer> topMethodsCounterMap = methodsCounterMap.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		System.out.println("Top methods: ");
		int i=0;
		for(String method:topMethodsCounterMap.keySet()) {
			System.out.println(method+" :"+topMethodsCounterMap.get(method));
			i++;
			if(i==10) {
				break;
			}
		}
		
		methodsCounterMap.clear();
		methodsCounterMap.putAll(topMethodsCounterMap);
		
	}

	/*
	protected Set<Integer> getTopKRelevantQuestionBuckets(Set<Bucket> candidateBuckets, Bucket nonMaster, int contentTypeTFIDF, Map<Integer, Bucket> allUpvotedScoredAnswersMap) {
		String comparingContent;
		String nonMasterContent = loadBucketContent(nonMaster,contentTypeTFIDF);
		bucketsIdsScores.clear();
		double maxSimPair=0;
		double maxApiScore=0;
		double maxTfIdfScore=0;
		//double maxBm25Score=0;
		//methodsCounterMap.clear();
		//classesCounterMap.clear();
		documents.clear();
		Document queryDocument = new Document(nonMasterContent, 0);
		documents.add(queryDocument);
		
		for(Bucket bucket:candidateBuckets) {
			comparingContent = loadBucketContent(bucket,contentTypeTFIDF);
			
			if(!CollectionUtils.isEmpty(bucket.getTopScoredAnswersSet())) {
				for(Integer childId: bucket.getTopScoredAnswersSet()) {
					Bucket child = allUpvotedScoredAnswersMap.get(childId);					
					comparingContent+= " "+loadBucketContent(child,11); //getProcessedBodyLemma
				}
			}
			
			Document document = new Document(comparingContent, bucket.getId());
			documents.add(document);
			bucket.setDocument(document);
		}

		Corpus corpus = new Corpus(documents);
		VectorSpaceModel vectorSpace = new VectorSpaceModel(corpus);
			
		for(Bucket bucket:candidateBuckets) {
			//second ranking
			try {
				
				double cosineSimScore = vectorSpace.cosineSimilarity(queryDocument, bucket.getDocument());
				bucket.setTfIdfCosineSimScore(cosineSimScore);
				if(cosineSimScore>maxTfIdfScore) {
					maxTfIdfScore=cosineSimScore;
				}
				
				double bm25Score = bm25ScoreAnswerIdMap.get(bucket.getId());
				bucket.setBm25Score(bm25Score);
				if(bm25Score>maxBm25Score) {
					maxBm25Score=bm25Score;
				}
				
				
				double simPair= bucket.getSimPair();
				if(simPair==0d) {
					System.out.println("Huston, we have a problem !!");
					throw new RuntimeException("Huston, we have a problem !!");
				}
				
				
				if(simPair>maxSimPair) {
					maxSimPair=simPair;
				}
				
			} catch (Exception e) {
				System.out.println("error here... post: "+bucket.getId());
				throw e;
			}
			
		}
		
		
		
		//normalization and other relevance boosts
		for(Bucket bucket:candidateBuckets) {
			double simPair = bucket.getSimPair();
			simPair = (simPair / maxSimPair); //normalization
			
			double tfIdfScore = bucket.getTfIdfCosineSimScore();
			tfIdfScore = (tfIdfScore / maxTfIdfScore); //normalization
			
			double bm25Score = bucket.getTfIdfCosineSimScore();
			bm25Score = (bm25Score / maxBm25Score); //normalization	
			
			double finalScore = CrokageComposer.calculateQuestionRankingScore(bucket,simPair,tfIdfScore,0);
			bucketsIdsScores.put(bucket.getId(), finalScore);
		}
		
		Map<Integer,Double> topScoredBuckets = bucketsIdsScores.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			       //.limit(topSimilarAnswersNumber)
			       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		corpus=null;
		vectorSpace=null;
		candidateBuckets=null;
		queryDocument=null;
		return topScoredBuckets.keySet();
	}
*/

	
	


	
	
	/*

	private Set<Integer> scoreAnswersByAPIs(Set<String> topClasses, Set<Integer> relevantAnswersIds) {
		Set<AnswerParentPair> answerParentThreads = new LinkedHashSet<>(); 
		List<Integer> topNAnswersIds = new ArrayList<>();
		
		for(String topClass:topClasses) {
			Set<Integer> answersIdsFromBigMap = filteredSortedMapAnswersIds.get(topClass);
			if(answersIdsFromBigMap==null) {
				continue;
			}	
			
			
			for(Integer relevantAnswerId: relevantAnswersIds) {
				if(answersIdsFromBigMap.contains(relevantAnswerId) && allBucketsWithUpvotesMap.containsKey(relevantAnswerId)) { //filtered by lucene and is upvoted answer 
					AnswerParentPair answerParentPair = new AnswerParentPair(relevantAnswerId, allBucketsWithUpvotesMap.get(relevantAnswerId).getParentId());
					CrokageComposer.calculateApiScore(answerParentPair,topClasses,upvotedPostsIdsWithCodeApisMap);
					answerParentThreads.add(answerParentPair);
					topAnswerParentPairsAnswerIdScoreMap.put(relevantAnswerId, answerParentPair.getApiScore());
				}
			}
		}
		
		//System.out.println("Number of pairs(answer+parent) containing topClasses: "+answerParentThreads.size());
		
		
		topNAnswersIds=null;
		answerParentThreads=null;
		
		return relevantAnswersIds;
		
		
	}*/


	protected void flushVariablesForTag() throws Exception {
		allBucketsWithUpvotesMap=null;
		luceneSearcher=null;
		searchParam=null;
		
		allBucketsWithUpvotesMap = new LinkedHashMap<>();
		luceneSearcher=new LuceneSearcher();
		//groundTruthSelectedQueriesAnswersIdsMap.clear();
		queries = null;
		
		//recommendedApis = null;

	}


	protected void reduceBigMapFileToMininumAPIsCount() throws Exception {
		if(filteredSortedMapAnswersIds.isEmpty()) {
			//load the inverted index
			long initTime = System.currentTimeMillis();
			//crarService.loadApiAnswersMap(bigMapApisAnswersIds);
			
			//filter by cutoff and sort in descending order
			filteredSortedMapAnswersIds = bigMapApisAnswersIds.entrySet().stream()
					.filter( e -> e.getValue().size() > cutoff)
			        .sorted( (e1,e2)->  Integer.compare(e2.getValue().size(), e1.getValue().size()))
			        .collect(Collectors.toMap(
			                Map.Entry::getKey,
			                Map.Entry::getValue,
			                (a,b) -> {throw new AssertionError();},
			                LinkedHashMap::new
			        )); 
			
			//remove String
			//filteredSortedMapAnswersIds.remove("String");
			//System.out.println("Size of big map before: "+bigMapApisAnswersIds.size()+ " -after cutoff: "+filteredSortedMapAnswersIds.size());
			//generate reduced map
			//CrarUtils.printMapInfosIntoCVSFile(filteredSortedMapAnswersIds,REDUCED_MAP_INVERTED_INDEX_APIS_FILE_PATH);
			//CrarUtils.reportElapsedTime(initTime,"reduceBigMapFileToMininumAPIsCount");
		}
	}


	protected void resetAPIExtractors() {
		rackQueriesApisMap.clear();
		bikerQueriesApisClassesMap.clear();
		nlp2ApiQueriesApisMap.clear();
	}

	
	protected void loadJavaVariables() throws Exception {
		// load the inverted index (api, postsSet)
		
		if(recommendedApis.isEmpty()) {
			crarService.loadApiAnswersMap(bigMapApisAnswersIds);
	
			// filter by cutoff and sort in descending order
			reduceBigMapFileToMininumAPIsCount();
	
			resetAPIExtractors();
	
			// load apis considering approaches
			getApisForApproaches();
	
			// loadThreadsForUpvotedAnswersWithCodeIdsTitlesMap();
			crarService.loadPostsApisMap(upvotedPostsIdsWithCodeApisMap);
	
			recommendedApis = getRecommendedApis();
			
			//System.out.println("\n\nrecommendedApis: "+mapJoiner.join(recommendedApis));
		}
		
		/*
		 * Set<Integer> keys = recommendedApis.keySet(); if (keys.size() !=
		 * queries.size()) { throw new
		 * Exception("Queries size are different from the ones recommended by the API extractors. Consider extracting them again. "
		 * ); }
		 */

		
	}

	

	
	public String analyzeResults(Map<String, Set<Integer>> recommended,Map<String, Set<Integer>> goldSet, MetricResult metricResult, String approach) {
		int accuracy_k = 0;
		int correct_sum = 0;
		double rrank_sum = 0;
		double precision_sum = 0;
		double preck_sum = 0;
		double recall_sum = 0;
		double fmeasure_sum = 0;
		int count=1;

		Integer topk = metricResult.getTopk();
		String kvalue="";
		if(topk!=null && topk>0) {
			CrarUtils.reduceSetV2(recommended, topk);
		}else if(topk==null) {
			topk=1; //save in top1 
		}
		int maxSize = 0;
		
		
		try {
			
			for (String keyQuery : recommended.keySet()) {
				currentQuery=keyQuery;
				/*if(keyQuery.equals("How do I convert angle from radians to degrees?")) {
					System.out.println();
				}*/
				
				ArrayList<Integer> rapis = new ArrayList<>(recommended.get(keyQuery));
				ArrayList<Integer> gapis = new ArrayList<>(goldSet.get(keyQuery));
			
				if(rapis.size()>maxSize) {
					maxSize=rapis.size();
				}
				
				int hitk = isFound_K(rapis, gapis);
				accuracy_k = accuracy_k + hitk;
				double rrank = 0;
				rrank = getRRankV2(rapis, gapis);
				rrank_sum = rrank_sum + rrank;
				double preck = 0;
				preck = getAvgPrecisionKV2(rapis, gapis);
				preck_sum = preck_sum + preck;
				double recall = 0;
				recall = getRecallKV3(rapis, gapis);
				recall_sum = recall_sum + recall;
				
				rrank = CrarUtils.round(rrank,2);
				preck = CrarUtils.round(preck,2);
				recall= CrarUtils.round(recall,2);
				
				
				if(saveQueryMetric) {
					MetricResult queryMetric = new MetricResult(metricResult.getApproach(), null, null, null, null, null, topk, "tagId="+tagId+" - saveQueryMetric", null,null,metricResult.getTagId());
					queryMetric.setHitK((double)hitk);
					queryMetric.setMrrK(rrank);
					queryMetric.setMapK(preck);
					queryMetric.setMrK(recall);
					queryMetric.setVectorsType(metricResult.getVectorsType());
					queryMetric.setIsQueryMetric(true);
					crarService.saveMetricResult(queryMetric);
				}
				count++;
				
			}

			double hit_k= CrarUtils.round((double) accuracy_k / goldSet.size(),2);
			double mrr = CrarUtils.round(rrank_sum / goldSet.size(),2);
			double map = CrarUtils.round(preck_sum / goldSet.size(),2);
			double mr = CrarUtils.round(recall_sum / goldSet.size(),2);
			
			if(metricResult!=null) {
				metricResult.setHitK(hit_k);
				metricResult.setMrrK(mrr);
				metricResult.setMapK(map);
				metricResult.setMrK(mr);
				
			}
			
			kvalue = maxSize+ "";
			if(approach.contains("topAsymIdsMap") || approach.contains("topAsymIdsMap")) {
				kvalue+="(maxValue)";
			}
			
			System.out.println(approach+" "
					+" - Hit@" + kvalue + ": " + hit_k
					+" - MRR@" + kvalue + ": " + mrr
					+" - MAP@" + kvalue + ": " + map
					+" - MR@" + kvalue + ": " + mr
					+ "");
			
			return approach+" "
					+" - Hit@" + kvalue + ": " + hit_k
					+" - MRR@" + kvalue + ": " + mrr
					+" - MAP@" + kvalue + ": " + map
					+" - MR@" + kvalue + ": " + mr
					+ "\n";
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in analyzeResultsV2 in query: "+currentQuery);
			throw e;
		}
		
	}


	public void analyzeResultsForApiExtractors(Map<Integer, Set<String>> recommendedApis,Map<Integer, Set<String>> goldSetQueriesApis, MetricResult metricResult) {
		int accuracy_k = 0;
		int correct_sum = 0;
		double rrank_sum = 0;
		double precision_sum = 0;
		double preck_sum = 0;
		double recall_sum = 0;
		double fmeasure_sum = 0;
		int count=1;
		
		int k = numberOfAPIClasses;
		
		try {
			
			for (Integer keyQuery : goldSetQueriesApis.keySet()) {
				
				List<String> rapis = new ArrayList<>(recommendedApis.get(keyQuery));
				ArrayList<String> gapis = new ArrayList<>(goldSetQueriesApis.get(keyQuery));
				
				int hitk = isApiFound_K(rapis, gapis);
				accuracy_k = accuracy_k + hitk;
				double rrank = 0;
				rrank = getRRank(rapis, gapis);
				rrank_sum = rrank_sum + rrank;
				double preck = 0;
				preck = getAvgPrecisionK(rapis, gapis);
				preck_sum = preck_sum + preck;
				double recall = 0;
				recall = getRecallK(rapis, gapis);
				recall_sum = recall_sum + recall;
				
				
				count++;
			}

			double hit_k= CrarUtils.round((double) accuracy_k / goldSetQueriesApis.size(),4);
			double mrr = CrarUtils.round(rrank_sum / goldSetQueriesApis.size(),4);
			double map = CrarUtils.round(preck_sum / goldSetQueriesApis.size(),4);
			double mr = CrarUtils.round(recall_sum / goldSetQueriesApis.size(),4);
			
			if(metricResult!=null) {
				if(k==10) {
					metricResult.setHitK10(hit_k);
					metricResult.setMrrK10(mrr);
					metricResult.setMapK10(map);
					metricResult.setMrK10(mr);
				}else {//5
					metricResult.setHitK5(hit_k);
					metricResult.setMrrK5(mrr);
					metricResult.setMapK5(map);
					metricResult.setMrK5(mr);
				}
			}
			
			
				//System.out.println("Results for: "+subAction+" \n"+		"&"+ hit_k + "  &"+mrr+ "  &"+map+ "  &"+mr);
			
			
			/*System.out.println("Results: \n"
					+"\nHit@" + k + ": " + hit_k
					+ "\nMRR@" + k + ": " + mrr
					+ "\nMAP@" + k + ": " + map
					+ "\nMR@" + k + ": " + mr
					+ "");*/
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	


	protected void readSOContentWordAndVectors(Integer tagId) {
		long initTime = System.currentTimeMillis();
		System.out.println("loading word vectors for tag:"+tagId+"...");
		wordVectorsMap.clear();
		crarService.findAllWordVectorsByTagId(tagId,wordVectorsMap);
		CrarUtils.reportElapsedTime(initTime,"loading word vectors");
	}

	
	protected void loadUpvotedAnswersIdsWithCodeContentsAndParentContentsDyCrokage(Integer tagId) {
		long initTime2 = System.currentTimeMillis();
		List<Bucket> buckets = crarService.getUpvotedAnswersIdsContentsAndParentContentsDycrokage(tagId);
		for(Bucket bucket:buckets) {
			allBucketsWithUpvotesMap.put(bucket.getId(), bucket);
		}
		crarUtils.reportElapsedTime(initTime2,"loading all SO Q&A pairs for tag: "+tagId);
		buckets=null;
		
	}

	protected void readSOContentWordAndVectorsForTags(TagEnum[] languages) {
		long initTime = System.currentTimeMillis();
		for(TagEnum language:languages) {
			int tagId = TagEnum.getTagIdByTagEnum(language);
			System.out.println("loading word vectors for tag:"+tagId+"...");
			Map<String, SoContentWordVector> wordVectorsMap = new LinkedHashMap<>();
			System.gc();
			crarService.findAllWordVectorsByTagId(tagId,wordVectorsMap);
			System.gc();
			wordVectorsMapByTag.put(tagId, wordVectorsMap);
			CrarUtils.reportElapsedTime(initTime,"loading word vectors for tag "+tagId);
		}
		CrarUtils.reportElapsedTime(initTime,"loading word vectors for all tags");
	}

	
	protected void loadUpvotedAnswersIdsWithCodeContentsAndParentContentsDyCrokageforTags(List<TagEnum> languagesList) {
		long initTime2 = System.currentTimeMillis();
		for(TagEnum language: languagesList) {
			int tagId = TagEnum.getTagIdByTagEnum(language);
			System.out.println("loading Upvoted Answers for tag:"+tagId+"...");
			Map<Integer, Bucket> allBucketsWithUpvotesMap = new LinkedHashMap<>();
			List<Bucket> buckets = crarService.getUpvotedAnswersIdsContentsAndParentContentsDycrokage(tagId);
			for(Bucket bucket:buckets) {
				allBucketsWithUpvotesMap.put(bucket.getId(), bucket);
			}
			allBucketsWithUpvotesMapByTag.put(tagId, allBucketsWithUpvotesMap);
			CrarUtils.reportElapsedTime(initTime2,"loading Upvoted Answers for tag "+tagId);
		}
		
		
		
	}
	
	

	protected void buildSynonymsFromSO() throws IOException {
		/*
		 * 1- Load all duplicated questions where the master question has upvotes and answers
		 */
		TagEnum languages[] = {TagEnum.Java};
		BufferedWriter bw =null;
		//int limit = 1000;
		int count=0;
		for(TagEnum language: languages) {
			try {
				bw = new BufferedWriter(new FileWriter(CRAR_HOME+"/data/"+language.getDescricao()+".txt"));
				Set<Pair> duplicatedPairsClosedNonMasters =  crarService.getDuplicatedPairsClosedNonMasters(language);  
				for(Pair pair: duplicatedPairsClosedNonMasters) {
					
					String content = pair.getNonMasterProcessedTitle()+" "+pair.getMasterProcessedTitle();
					
					//Set<String> wordsSet = Arrays.stream(content.split(" +")) .collect(Collectors.toCollection(LinkedHashSet::new));
					Set<String> verbs = CrarUtils.getWordsByPOS(content,"VB");
					if(verbs.isEmpty() || verbs.size()==1) {
						continue;
					}
					String verbsList = String.join(" ", verbs);
					bw.write(verbsList+"\n");
					
					
					count++;
					/*if(count==limit) {
						break;
					}*/
					
				}
				
			
			}catch (Exception e) {
				System.out.println(e);
			} finally {
				bw.close();
			}
			
			
		}
		
	}
	
	protected void buildAntonymsFromLists() throws IOException {
		String resourceName;
		List<String> lines;
		InputStream input;
		String[] parts;
		Map<String,Set<String>> synonymsMap = new LinkedHashMap<>();
		Map<String,Set<String>> antonymsMap = new LinkedHashMap<>();
		
		/*
		 * taikuukaits
		 */
		resourceName = "antonyms_taikuukaits.txt";
		input = getClass().getClassLoader().getResourceAsStream(resourceName);
        lines = IOUtils.readLines(input, StandardCharsets.UTF_8);
		
		for(String line:lines) {
			parts = line.split("\\|");
			if(parts.length>1) {
				String word = parts[0];
				Set<String> antonyms = Arrays.stream(parts[1].split(",")).map(String::trim).collect(Collectors.toSet());
				//antonymsMap.put(word, antonyms);
				CrarUtils.appendInMap(word,antonyms,antonymsMap);
			}
		}
		
		//System.out.println(antonymsMap);
		
		/*
		 * maxtruxa
		 */
		resourceName = "antonyms_maxtruxa.txt";
		input = getClass().getClassLoader().getResourceAsStream(resourceName);
        lines = IOUtils.readLines(input, StandardCharsets.UTF_8);
		
		for(String line:lines) {
			parts = line.split("\\|");
			if(parts.length>1) {
				//String[] antonymsList = parts[2].split("`");
				//String[] wordParts = word.split("_");
				
				Set<String> antonyms = Arrays.stream(parts[1].split(",")).map(String::trim).collect(Collectors.toSet());
				if(!antonyms.isEmpty()) {
					CrarUtils.appendInMap(parts[0].trim(),antonyms,antonymsMap);
				}
				
			}
		}
		
		
		/*
		 * mfaruq
		 */
		resourceName = "antonyms_mfaruqui.txt";
		input = getClass().getClassLoader().getResourceAsStream(resourceName);
        lines = IOUtils.readLines(input, StandardCharsets.UTF_8);
		
		for(String line:lines) {
			parts = line.split(" ");
			if(parts.length>1) {
				//String[] antonymsList = parts[2].split("`");
				//String[] wordParts = word.split("_");
				
				Set<String> antonyms = new LinkedHashSet<>();
				for(int i=1 ; i<= parts.length-1; i++) {
					antonyms.add(parts[i].trim());
				}
				
				if(!antonyms.isEmpty()) {
					CrarUtils.appendInMap(parts[0].trim(),antonyms,antonymsMap);
				}
				
			}
		}
		
		
		
		/*
		 * airshipcloud
		 */
		/*resourceName = "antonyms_airshipcloud.txt";
		input = getClass().getClassLoader().getResourceAsStream(resourceName);
        lines = IOUtils.readLines(input, StandardCharsets.UTF_8);
		
		for(String line:lines) {
			parts = line.split(",");
			if(parts.length>1) {
				String word = parts[0].split("_")[0];
				//String[] wordParts = word.split("_");
				
				Set<String> antonyms = new LinkedHashSet<>();
				for(int i=1 ; i<= parts.length-1; i++) {
					antonyms.add(parts[i].trim());
				}
				CrarUtils.appendInMap(word,antonyms,antonymsMap);
			}
		}*/
		
		/*
		 * SuzanaK
		 */
		/*resourceName = "antonyms_SuzanaK.txt";
		input = getClass().getClassLoader().getResourceAsStream(resourceName);
        lines = IOUtils.readLines(input, StandardCharsets.UTF_8);
		
		for(String line:lines) {
			parts = line.split("\t");
			if(parts.length>2) {
				//String[] antonymsList = parts[2].split(",");
				//String[] wordParts = word.split("_");
				
				Set<String> antonyms = Arrays.stream(parts[2].split(",")).map(String::trim).collect(Collectors.toSet());
				if(!antonyms.isEmpty()) {
					CrarUtils.appendInMap(parts[0],antonyms,antonymsMap);
				}
				
			}
		}*/
		
		
		
		//System.out.println(antonymsMap);
		
		crarUtils.writeMapToFile2(antonymsMap,CRAR_HOME+"/src/main/resources/antonyms.txt","|",",");
		
	}
	

	
	protected void generateThreadsCoverage(Set<ThreadContent> candidateThreads, Map<String, Set<Integer>> groundTruthMap,
			String rawQuery, String processedQuery, BufferedWriter bw, Map<Integer, ThreadContent> topThreadsMap, int rounds, 
			Integer tagId, ThreadContent maxFeaturesThread, Baseline baseline) throws IOException {
		
		List<Integer> recommendedIds = candidateThreads.stream().map(e -> e.getId()).collect(Collectors.toCollection(ArrayList::new));
		List<Integer> groundTruthIds = new ArrayList(groundTruthMap.get(rawQuery));
		
		List<Integer> trashList = new ArrayList<>();
		List<Integer> foundList = new ArrayList<>();
		
		bw.write(rawQuery+"\n\n");
		
		for(Integer recommendedId: recommendedIds) {
			if(!groundTruthIds.contains(recommendedId)) {
				trashList.add(recommendedId);
			}else {
				foundList.add(recommendedId);
			}
		}
		
		if(rounds<2) { //social features not calculated
			maxFeaturesThread.setAnswerCountMax(-1d);
			maxFeaturesThread.setThreadTotalUpvotesMax(-1d);
		}
		bw.write("\nFound Threads: ("+foundList.size()+")");
		bw.write(";Pivot;------;------;"+CrarUtils.round(maxFeaturesThread.getThreadTitleAsymmetricSimMax(),2)
		+";------;"+CrarUtils.round(maxFeaturesThread.getThreadBodyAsymmetricSimMax(),2)
		+";------;------;------;------;"+maxFeaturesThread.getTwoGramsScoreMax()
		+";"+maxFeaturesThread.getThreeGramsScoreMax()
		+";"+CrarUtils.round(maxFeaturesThread.getBm25ScoreMax(),2)
		+";------;"+CrarUtils.round(maxFeaturesThread.getTfIdfCosineSimScoreMax(),2)
		+";------;"+CrarUtils.round(maxFeaturesThread.getAnswerCountMax(),2)
		+";------;------;"+CrarUtils.round(maxFeaturesThread.getThreadTotalUpvotesMax(),2)
		
		/*+";------;"+CrarUtils.round(maxFeaturesThread.getThreadAnswerCountScoreNormalized(),2)
		+";------;"+CrarUtils.round(maxFeaturesThread.getThreadUpVotesScore(),2)
		+";------;"+CrarUtils.round(maxFeaturesThread.getThreadTotalUpVotesScore(),2)
		+";------;"+CrarUtils.round(maxFeaturesThread.getThreadTotalUpVotesScoreNormalized(),2)*/
		
		
		+"\n"); //pivot with max values
		for(Integer foundTreadId:foundList) {
			int pos = recommendedIds.indexOf(foundTreadId);
			CrarUtils.writeFeatureRowThreads(topThreadsMap.get(foundTreadId),bw,pos,foundTreadId);
		}
		
		groundTruthIds.removeAll(recommendedIds);
		bw.write("\nMissed Threads: ("+groundTruthIds.size()+")");
		if(!groundTruthIds.isEmpty()) { //remaining ground truth missed
			for(Integer groundTruthId: groundTruthIds) {
				ThreadContent candidateThread = crarService.getThreadById(groundTruthId);
				fillFeaturesForThread(candidateThread, processedQuery, tagId, baseline);
				try {
					CrarUtils.writeFeatureRowThreads(candidateThread,bw,-1,groundTruthId);
				} catch (Exception e2) {
					System.out.println("error here: "+candidateThread);
					System.out.println("");
				}
				
			}
		}
		bw.write("\n\nTrash in round "+rounds+": ("+trashList.size()+")");
		
		if(!trashList.isEmpty()) {
			int k=0;
			for(Integer trashId: trashList) {
				int pos = recommendedIds.indexOf(trashId);
				CrarUtils.writeFeatureRowThreads(topThreadsMap.get(trashId),bw,pos,trashId);
				k++;
				if(k==20) break;
			}
		}
		
		bw.write("\n\n-----;-----;-----;-----;-----;-----;-----;-----;-----;-----;-----;-----;-----;-----;-----;\n\n");
	}


	protected void fillFeaturesForThread(ThreadContent candidateThread, String processedQuery, Integer tagId, Baseline baseline) throws IOException {
		String comparingContent;
		List<Collection<String>> candidateDocumentGrams;
		List<Collection<String>> queryGrams = Lists.newArrayList(NgramTfIdf.ngramDocumentTerms(
        		Lists.newArrayList(2,3,4),
            	Arrays.asList(processedQuery)));
		Bucket queryGramBucket = new Bucket();
		CrarUtils.generateNGrams(queryGramBucket,queryGrams);
		
		 candidateDocumentGrams = Lists.newArrayList(NgramTfIdf.ngramDocumentTerms(
	        		Lists.newArrayList(2,3,4),
	         		Arrays.asList(comparingContent= loadThreadContent(candidateThread,ContentTypeEnum.title_questionBody_body_code.getId()))));
		 CrarUtils.generateNGrams(candidateThread,candidateDocumentGrams);
	     CrarUtils.countCommonNGrams(queryGramBucket,candidateThread);
	     
		
		comparingContent=candidateThread.getTitle();
		
		double threadTitleAsymmetricSim = CrarUtils.round(getAsymmetricSimilarity(processedQuery,comparingContent, tagId,null),2);
		candidateThread.setThreadTitleAsymmetricSimNormalized(threadTitleAsymmetricSim);
		
		comparingContent= loadThreadContent(candidateThread,ContentTypeEnum.question_body_answers_body.getId()); 
		double threadBodyAsymmetricSim = CrarUtils.round(getAsymmetricSimilarity(processedQuery,comparingContent, tagId,null),2);
		candidateThread.setThreadQuestionBodyAnswersBodyAsymmetricSimNormalized(threadBodyAsymmetricSim);
		
		double queryCoverage = CrarUtils.round(CrarUtils.calculateCoverage(processedQuery, loadThreadContent(candidateThread,ContentTypeEnum.title.getId())),2);
		candidateThread.setQueryTitleCoverageScore(queryCoverage);
		
		double codeCoverage = CrarUtils.round(CrarUtils.calculateCoverage(processedQuery, loadThreadContent(candidateThread,ContentTypeEnum.code.getId())),2);
		candidateThread.setQueryCodeCoverageScore(codeCoverage);
		
		double jaccard = CrarUtils.round(CrarUtils.calculateExactJaccard(processedQuery, loadThreadContent(candidateThread,ContentTypeEnum.title.getId())),2);
		candidateThread.setJaccardScore(jaccard);
		
		candidateThread.setTfIdfCosineSimScoreNormalized(0d);
		candidateThread.setBm25ScoreNormalized(0d);
		
		double finalScoreStage1 = CrokageComposer.calculateThreadsRankingScoreRound1(candidateThread, baseline);
		candidateThread.setThreadFinalScore(finalScoreStage1);
		
	}
	
	
	
	protected void fillFeaturesForAnswer(Bucket bucket, String processedQuery, Integer tagId) throws IOException {
		String comparingContent="";
		
		comparingContent = loadBucketContent(bucket,ContentTypeEnum.title_body.getId());
		
		double asymmetricSim = CrarUtils.round(getAsymmetricSimilarity(processedQuery,comparingContent, tagId,null),2);
		bucket.setSimPair(asymmetricSim);
		
		
		double queryCoverage = CrarUtils.round(CrarUtils.calculateCoverage(processedQuery, loadBucketContent(bucket,ContentTypeEnum.body_code.getId())),2);
		bucket.setQueryCoverageScore(queryCoverage);
		
		double codeCoverage = CrarUtils.round(CrarUtils.calculateCoverage(processedQuery, loadBucketContent(bucket,ContentTypeEnum.code.getId())),2);
		bucket.setCodeCoverageScore(codeCoverage);
		
		double jaccard = CrarUtils.round(CrarUtils.calculateExactJaccard(processedQuery, loadBucketContent(bucket,ContentTypeEnum.title_body.getId())),2);
		bucket.setJaccardScore(jaccard);
		
		bucket.setTfIdfCosineSimScoreNormalized(0d);
		bucket.setBm25ScoreNormalized(0d);
		bucket.setThreadSimNormalized(0d);
		bucket.setMethodScoreNormalized(0d);
		
		//double finalScoreStage1 = CrokageComposer.calculateAnswersDynamicRankingScore(bucket);
		bucket.setFinalScore(0d);
		
	}
	

	public Double getAsymmetricSimilarity(String processedQuery,String comparingContent, Integer tagId, Integer vectorsTypeId) {
		double similarity=0d;
		similarity = CrarUtils.getSimPair(comparingContent, processedQuery, wordVectorsMapByTag.get(tagId), vectorsTypeId);
		return similarity;
		
	}
	
	
	public Set<ThreadContent> getCandidadeThreadsClient(String processedQuery, int trmLimit1,Integer tagId) {
		
		String parameters = "{\"tagId\":"+tagId+",\"trmLimit1\":"+trmLimit1+",\"queryText\":\""+processedQuery+"\"}";
		ClientResponse response = webResourceGetCandidadeThreads.type("application/json").post(ClientResponse.class, parameters);
		String output = response.getEntity(String.class);
		PostRestTransfer postRestTransfer = gson.fromJson(output, PostRestTransfer.class);
		response=null;
		parameters=null;
		output=null;
		return postRestTransfer.getThreads();
		
	}
	
	public Set<Bucket> getCandidadeAnswersClient(String processedQuery, int trmLimit1,Integer tagId) {
		
		String parameters = "{\"tagId\":"+tagId+",\"trmLimit1\":"+trmLimit1+",\"queryText\":\""+processedQuery+"\"}";
		ClientResponse response = webResourceGetCandidadeBuckets.type("application/json").post(ClientResponse.class, parameters);
		String output = response.getEntity(String.class);
		PostRestTransfer postRestTransfer = gson.fromJson(output, PostRestTransfer.class);
		response=null;
		parameters=null;
		output=null;
		return postRestTransfer.getBuckets();
		
	}
	
	public Set<ThreadContent> getAllThreadsClient(Integer tagId) {
		String parameters = "{\"tagId\":"+tagId+"}";
		ClientResponse response = webResourceGetAllThreads.type("application/json").post(ClientResponse.class, parameters);
		String output = response.getEntity(String.class);
		PostRestTransfer postRestTransfer = gson.fromJson(output, PostRestTransfer.class);
		response=null;
		parameters=null;
		output=null;
		return postRestTransfer.getThreads();
		
	}
	
	public Set<Bucket> getAllAnswersClient(Integer tagId) {
		String parameters = "{\"tagId\":"+tagId+"}";
		ClientResponse response = webResourceGetAllBuckets.type("application/json").post(ClientResponse.class, parameters);
		String output = response.getEntity(String.class);
		PostRestTransfer postRestTransfer = gson.fromJson(output, PostRestTransfer.class);
		response=null;
		parameters=null;
		output=null;
		return postRestTransfer.getBuckets();
		
	}
	
	
	
}
