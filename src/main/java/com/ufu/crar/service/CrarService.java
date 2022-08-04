package com.ufu.crar.service;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.ufu.crar.to.ApiAnswers;
import com.ufu.crar.to.Bucket;
import com.ufu.crar.to.MetricResult;
import com.ufu.crar.to.Pair;
import com.ufu.crar.to.Post;
import com.ufu.crar.to.PostApis;
import com.ufu.crar.to.Query;
import com.ufu.crar.to.ResultEvaluation;
import com.ufu.crar.to.SoContentWordVector;
import com.ufu.crar.to.ThreadContent;
import com.ufu.crar.to.Tags.TagEnum;
import com.ufu.crar.util.AbstractService;
import com.ufu.crar.util.CrokageComposer;
import com.ufu.crar.util.CrarUtils;


@Service
@Transactional
public class CrarService extends AbstractService{
		
	
	
	public CrarService() {
		
	}

	@Transactional(readOnly = true)
	public List<Post> getAnswersWithCode(String startDate,Integer tagId) {
		return genericRepository.getAnswersWithCode(startDate,tagId);
		
	}
	
	@Transactional(readOnly = true)
	public List<Post> getUpvotedPostsWithCode(String startDate, Integer tagId) {
		return genericRepository.getUpvotedPostsWithCode(startDate,tagId);
	}

	@Transactional(readOnly = true)
	public List<Bucket> getBucketsByIds(Set<Integer> postsListIds) {
		return genericRepository.getBucketsByIds(postsListIds);
	}
	
	@Transactional(readOnly = true)
	public List<Bucket> getUpvotedQuestionsWithAnswersForTag(Integer tagId, boolean useLemma) {
		return genericRepository.getUpvotedQuestionsWithAnswersForTag(tagId, useLemma);
	}

	@Transactional(readOnly = true)
	public List<Integer> findAllQuestionsIds() {
		return postsRepository.findAllQuestionsIds();
	}

	@Transactional(readOnly = true)
	public List<Post> findPostsById(Collection<Integer> somePosts) {
		return genericRepository.getPostsByIds(somePosts);
	}

	@Transactional(readOnly = true)
	public List<Integer> findAllPostsIds() {
		return postsRepository.findAllPostsIds();
	}

	@Transactional(readOnly = true)
	public Map<Integer, String> getQuestionsIdsTitles() {
		return genericRepository.getQuestionsIdsTitles();
	}

	@Transactional(readOnly = true)
	public Map<Integer, Integer> getAnswersIdsParentIds() {
		return genericRepository.getAnswersIdsParentIds();
	}

	@Transactional(readOnly = true)
	public List<Post> findUpVotedAnswersWithCodeByQuestionId(Integer id) {
		return postsRepository.findUpVotedAnswersWithCodeByQuestionId(id);
	}

	
	

	public void saveMetricResult(MetricResult metricResult) {
		metricResult.setClassFreqWeight(CrokageComposer.getApiWeight());
		metricResult.setMethodFreqWeight(CrokageComposer.getMethodFreqWeight());
		metricResult.setFastTextWeight(CrokageComposer.getFastTextWeight());
		metricResult.setSent2VecWeight(CrokageComposer.getSent2VecWeight());
		metricResult.setBm25weight(CrokageComposer.getBm25Weight());
		metricResult.setTfIdfCosSimWeight(CrokageComposer.getTfIdfWeight());
		//metricResult.setUpWeight(CrokageComposer.getUpWeight());
		//metricResult.setRepWeight(CrokageComposer.getRepWeight());
		metricResultRepository.save(metricResult);
	}

	@Transactional(readOnly = true)
	public Integer getMostUpvotedAnswerForQuestionId(Integer questionId) {
		List<Integer> ids = postsRepository.getMostUpvotedAnswerForQuestionId(questionId);
		return ids.isEmpty() ? null: ids.get(0);
	}
	
	@Transactional(readOnly = true)
	public Bucket getMostUpvotedAnswerForQuestionId2(Integer questionId) {
		List<Post> buckets = postsRepository.getMostUpvotedAnswerForQuestionId2(questionId);
		Post post= buckets.isEmpty() ? null: buckets.get(0);
		Bucket bucket=null;
		if(post!=null) {
			bucket = new Bucket(post.getId(),post.getProcessedBody());
		}else {
			System.out.println("Bucket null in getMostUpvotedAnswerForQuestionId2 for question:"+questionId);
		}
		return bucket;
	}

	@Transactional(readOnly = true)
	public Post getMostUpvotedAnswerForQuestion(Integer questionId,Integer answerId) {
		List<Post> questions = postsRepository.getMostUpvotedAnswerForQuestion(questionId,answerId);
		return questions.isEmpty() ? null: questions.get(0);
	}

	@Transactional(readOnly = true)
	public List<Bucket> getUpvotedAnswersIdsContentsAndParentContents(Integer tagId) {
		return genericRepository.getUpvotedAnswersIdsContentsAndParentContents(tagId);
	}
	
	@Transactional(readOnly = true)
	public List<Bucket> getUpvotedAnswersIdsContentsAndParentContentsDycrokage(Integer tagId) {
		return genericRepository.getUpvotedAnswersIdsContentsAndParentContentsDycrokage(tagId);
	}

	/*public Map<Integer,String> getThreadsIdsTitlesForUpvotedAnswersWithCode() {
		return genericRepository.getThreadsIdsTitlesForUpvotedAnswersWithCode();
	}
*/
	

	@Transactional(readOnly = true)
	public Set<Bucket> findClosedDuplicatedNonMasters() throws Exception {
		
		logger.info("findClosedDuplicatedNonMasters... ");
		Set<Bucket> rawClosedDuplicatedNonMasters = genericRepository.findClosedDuplicatedNonMasters();
		logger.info("rawClosedDuplicatedNonMasters size: "+rawClosedDuplicatedNonMasters.size());
		return rawClosedDuplicatedNonMasters;
	}
	
	@Transactional(readOnly = true)
	public Map<Integer, Set<Integer>> getAllPostLinks() {
		return genericRepository.getAllPostLinks();
	}

	@Transactional(readOnly = true)
	public Set<Bucket> getAllUpvotedScoredBuckets(int postType) {
		Set<Bucket> buckets = genericRepository.getAllUpvotedScoredBuckets(postType); 
		return buckets;
	}

	/*@Transactional(readOnly = true)
	public List<Bucket> getBucketsLemmaByParentsIds(Set<Integer> questionsIds) {
		return genericRepository.getBucketsLemmaByParentsIds(questionsIds);
	}*/

	
	public void saveTopics(Map<Integer, String> postsTopics) {
		Set<Integer> ids = postsTopics.keySet();
				
		for(Integer postId: ids) {
			String topic = postsTopics.get(postId);
			genericRepository.saveTopic(postId,topic);
		}
			
	}
	
	public void saveHotTopics(Set<Bucket> bucketsAndTopics) {
		for(Bucket bucket: bucketsAndTopics) {
			genericRepository.saveHotTopic(bucket.getId(),bucket.getHotTopics());
		}
	}
	
	public void saveTopScoredAnswers(Set<Bucket> allUpvotedScoredQuestions) {
		for(Bucket bucket: allUpvotedScoredQuestions) {
			genericRepository.saveTopScoredAnswers(bucket.getId(),bucket.getTopScoredAnswers());
		}
	}
	

	@Transactional(readOnly = true)
	public Set<Bucket> getAllBucketsAndTopics() {
		return genericRepository.getAllBucketsAndTopics();
	}

	@Transactional(readOnly = true)
	public Set<Bucket> getAllQuestionsLemma() {
		return genericRepository.getAllQuestionsLemma();
	}

	

	public void removePossibleDuplicateFromBodies() {
		Set<Bucket> buckets = genericRepository.getPossibleDupesInBodies();
		for(Bucket bucket: buckets) {
			if(bucket.getProcessedBody().startsWith("possible duplicates")) {
				String newBody= bucket.getProcessedBody().replace("possible duplicates ", " ");
				bucket.setProcessedBody(newBody);
				String newBodyLemma = bucket.getProcessedBodyLemma().replace("possible duplicates ", " ");
				bucket.setProcessedBodyLemma(newBodyLemma);
				
				genericRepository.updateBodies(bucket);
			}if(bucket.getProcessedBody().startsWith("possible duplicate")) {
				String newBody= bucket.getProcessedBody().replace("possible duplicate ", " ");
				bucket.setProcessedBody(newBody);
				String newBodyLemma = bucket.getProcessedBodyLemma().replace("possible duplicate ", " ");
				bucket.setProcessedBodyLemma(newBodyLemma);
				
				genericRepository.updateBodies(bucket);
			}
		}
		
	}

	@Transactional(readOnly = true)
	public Bucket getBucketById(int id, boolean useLemma) {
		return genericRepository.getBucketById(id,useLemma);
	}

	@Transactional(readOnly = true)
	public Set<Pair> getDuplicatedPairs() {
		return genericRepository.getDuplicatedPairs();
	}

	@Transactional(readOnly = true)
	public List<Post> getAnswersWithCodeForQuestion(Integer questionId) {
		return genericRepository.getAnswersWithCodeForQuestion(questionId);
	}

	@Transactional(readOnly = true)
	public List<Integer> findAllPostsIdsByTagId(Integer tagId) {
		return postsRepository.findAllPostsIdsByTagId(tagId);
	}

	
	public void saveSOContentWordAndVectors(List<SoContentWordVector> soContentWordVectors) {
		for(SoContentWordVector soContentWordVector: soContentWordVectors) {
			soContentWordVectorsRepository.save(soContentWordVector);
		}
	}
	
	/*public void updateSOContentWordAndVectors(List<SoContentWordVector> soContentWordVectors) {
		for(SoContentWordVector soContentWordVector: soContentWordVectors) {
			soContentWordVectorsRepository.updateSentence2Vec(soContentWordVector.getId(),soContentWordVector.getSent2vecVectors());
		}		
	}*/

	
	public void loadIDFsToDataBase(Integer tagId, String SO_IDF_VOCABULARY) throws Exception {
		Map<String,Double> soIDFVocabularyMap = new LinkedHashMap<>();
		crarUtils.readIDFVocabulary(soIDFVocabularyMap,SO_IDF_VOCABULARY);

		String word;
		
		//get all wordvectors from DB
		List<SoContentWordVector> wordVectors = soContentWordVectorsRepository.findByTagId(tagId);
		for(SoContentWordVector wordVector: wordVectors) {
			word = wordVector.getWord();
			if(StringUtils.isBlank(word)) {
				System.out.println("EEEErrrrrrrr ----- Word is blank for: "+wordVector);
				continue;
			}
			wordVector.setIdf(soIDFVocabularyMap.get(word));
		}
	}

	public void loadAPIIndexToDataBase(Map<String, Set<Integer>> bigMapApisAnswersIds) {
		
		Set<String> words= bigMapApisAnswersIds.keySet();
				
		for(String word: words) {
			
			Set<Integer> ids = bigMapApisAnswersIds.get(word);
			
			String s = Joiner.on(',').join(ids);
			
			ApiAnswers apiAnswers = new ApiAnswers(word,s);
			apiAnswersRepository.save(apiAnswers);
		}
		
	}
	
	
	public void loadUpvotedPostsWithCodeApisToDb(Map<Integer, Set<String>> upvotedPostsIdsWithCodeApisMap) {
		Set<Integer> postId= upvotedPostsIdsWithCodeApisMap.keySet();
		
		for(Integer id: postId) {
			
			Set<String> apis = upvotedPostsIdsWithCodeApisMap.get(id);
			
			String s = Joiner.on(',').join(apis);
			
			PostApis postApis = new PostApis(id,s);
			postApisRepository.save(postApis);
		}
		
	} 
	

	@Transactional(readOnly = true)
	public void findAllWordVectorsByTagId(Integer tagId, Map<String, SoContentWordVector> wordVectorsMap) {
		//return soContentWordVectorsRepository.findByTagId(tagId);
		genericRepository.findAllWordVectorsByTagId(tagId,wordVectorsMap);
	}

	@Transactional(readOnly = true)
	public List<ApiAnswers> loadApiAnswers() {
		return (List<ApiAnswers>)apiAnswersRepository.findAll();
	}

	@Transactional(readOnly = true)
	public List<PostApis> loadAllPostApis() {
		return (List<PostApis>)postApisRepository.findAll();
	}
	
	

	
	public void populateTablePostApis(List<Post> upvotedPostsWithCode) throws IOException {
		
		String apisCommaSeparated;
		int i=0;
		for(Post post:upvotedPostsWithCode) {
			i++;
			if(i%100000==0) {
				System.out.println("Processing post "+i);
			}
			
			//extract apis from answer. For each api, add the api in a map, together with the other references for that post
			Set<String> codeSet=null;
			try {
				codeSet = crarUtils.extractClassesFromCode(post.getBody());
			} catch(StackOverflowError e){
				System.out.println("Error in post "+post.getId()+ " - disconsidering...");
				continue;
	        } catch (Exception e) {
				System.out.println("Exception in post "+post.getId()+ " - disconsidering...");
				continue;
			}
			
			//ArrayList<String> codeClasses = new ArrayList(codeSet);
			if(codeSet.isEmpty()) {
				continue;
			}
			
			apisCommaSeparated = String.join(",", codeSet);
			
			PostApis postApis = new PostApis(post.getId(),apisCommaSeparated);
			postApisRepository.save(postApis);
				
		}
	
		
		
	}

	public void populateApiAnswersTable(Map<String, Set<Integer>> bigMapApisAnswersIds) {
		Set<String> apis = bigMapApisAnswersIds.keySet();
		int i=0;
		for(String api: apis) {
			i++;
			if(i%100000==0) {
				System.out.println("Processing api "+i);
			}
			Set<Integer> ids =  bigMapApisAnswersIds.get(api);
			String s = Joiner.on(',').join(ids);
			ApiAnswers apiAnswers = new ApiAnswers(api,s);
			apiAnswersRepository.save(apiAnswers);
		}
	}

	@Transactional(readOnly = true)
	public void loadApiAnswersMap(Map<String, Set<Integer>> bigMapApisAnswersIds) {
		bigMapApisAnswersIds.clear();
		long initTime = System.currentTimeMillis();
		
		List<ApiAnswers> apiAnswers = loadApiAnswers();
		for(ApiAnswers row: apiAnswers) {
			
			String idsStr = row.getAnswersIds();
			
			Set<Integer> convertedRankList = Stream.of(idsStr.split(","))
					  .map(String::trim)
					  .map(Integer::parseInt)
					  .collect(Collectors.toSet());
			
			bigMapApisAnswersIds.put(row.getApi(), convertedRankList);
		}
		
		CrarUtils.reportElapsedTime(initTime,"loadInvertedIndexFile");
	}

	
	public void loadPostsApisMap(Map<Integer, Set<String>> upvotedPostsIdsWithCodeApisMap) {
		upvotedPostsIdsWithCodeApisMap.clear();
		
		long initTime = System.currentTimeMillis();
		
		List<PostApis> postApis = loadAllPostApis();
		
		CrarUtils.reportElapsedTime(initTime,"loadAllPostApis in db... now parsing...");
		initTime = System.currentTimeMillis();
		
		for(PostApis row: postApis) {
			
			String apisStr = row.getApis();
			
			Set<String> apiList = Stream.of(apisStr.split(","))
					  .map(String::trim)
					  .collect(Collectors.toSet());
			
			upvotedPostsIdsWithCodeApisMap.put(row.getPostId(), apiList);
		}
		
		CrarUtils.reportElapsedTime(initTime,"parsing apis");
		
	}

	@Transactional(readOnly = true)
	public List<Bucket> getUpvotedAnswersForQuestion(Integer id, boolean useLemma) {
		return genericRepository.getUpvotedAnswersForQuestion(id,useLemma);
	}

	public void saveThread(ThreadContent threadContent) {
		threadContentRepository.save(threadContent);
		
	}

	@Transactional(readOnly = true)
	public List<ThreadContent> getThreadsByTagId(int tagId) {
		return threadContentRepository.findByTagIdOrderById(tagId);
	}

	@Transactional(readOnly = true)
	public Set<Integer> findParentIdsByPostIds(Set<Integer> answersIds) {
		return genericRepository.findParentIdsByPostIds(answersIds);
	}

	@Transactional(readOnly = true)
	public Set<Bucket> getUpvotedAnswersWithCodeForThreadsIds(Set<Integer> thredsIds, boolean useLemma) {
		return genericRepository.getUpvotedAnswersWithCodeForThreadsIds(thredsIds,useLemma);
	}

	@Transactional(readOnly = true)
	public ThreadContent getThreadById(Integer threadId) {
		Optional<ThreadContent> threadContent = threadContentRepository.findById(threadId);
		return threadContent.get();
	}

	@Transactional(readOnly = true)
	public Set<Pair> getDuplicatedPairsClosedNonMasters(TagEnum tag) {
		return genericRepository.getDuplicatedPairsClosedNonMasters(tag);
	}

	@Transactional(readOnly = true)
	public List<Query> loadAllQueries(int idLimit) {
		return (List<Query>)queryRepository.findWithIdLowerThan(idLimit);
	}

	@Transactional(readOnly = true)
	public Set<String> loadAllWordsOfVocabulary(int tagid) {
		return soContentWordVectorsRepository.loadAllWordsOfVocabulary(tagid);
	}

	@Transactional(readOnly = true)
	public List<ResultEvaluation> findAllEvaluations(int idLimit) {
		return (List<ResultEvaluation>)resultEvaluationRepository.findWithQueryIdLowerThan(idLimit);
	}

	@Transactional(readOnly = true)
	public ResultEvaluation findLastEvaluation(String validEvaluatedNotDupeQuery) {
		List<ResultEvaluation> evaluations = resultEvaluationRepository.findLastEvaluation(validEvaluatedNotDupeQuery);
		return evaluations.isEmpty()?null:evaluations.get(0);
	}

	@Transactional(readOnly = true)
	public Post getMostUpvotedAnswerWithCodeForQuestion(int questionId) {
		List<Post> posts = postsRepository.getMostUpvotedAnswerWithCodeForQuestion(questionId); 
		return posts.isEmpty()?null:posts.get(0);
	}

	public void updateSentenceVectorsPostsTable(Integer id, String vectorsContent) {
		postsRepository.updateSentenceVectors(id,vectorsContent);
		
	}

	public void updateSentenceVectorsThreadsContentsTable(Integer id, String vectorsContent) {
		threadContentRepository.updateSentenceVectors(id,vectorsContent);
		
	}

	@Transactional(readOnly = true)
	public List<ThreadContent> getThreadContentsByIds(Set<Integer> questionsIds) {
		return genericRepository.getThreadContentsByIds(questionsIds);
	}

	


	
	
	
		
	
}
