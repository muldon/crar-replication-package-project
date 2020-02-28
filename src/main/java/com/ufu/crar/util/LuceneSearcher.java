package com.ufu.crar.util;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;
import com.ufu.crar.to.Bucket;
import com.ufu.crar.to.ThreadContent;

@Component
public class LuceneSearcher {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Analyzer standardAnalyzer;
	
	private Directory index;
	
	public static Integer indexedListSize;

	private ScoreDoc[] hits;

	private IndexSearcher searcher;

	private IndexReader reader;
	
	private IndexWriter w;

	private Integer parseErrosNum;

	private Map<Integer, Bucket> answersCache;
	
	private IndexWriterConfig config;
	
	private Set<Integer> bigSetAnswersIds;
	
	private QueryParser parser;
	 
	public LuceneSearcher() throws Exception {
		initializeConfigs();
	}
	
	@PostConstruct
	public void initializeConfigs() throws Exception {
		parseErrosNum = 0;
		standardAnalyzer = new StandardAnalyzer();
		//bigSetAnswersIds = new LinkedHashSet<>();
		// 1. create the index
		index = new RAMDirectory();
		//answersCache = new HashMap<>();
		config = new IndexWriterConfig(standardAnalyzer);
		//default
		//config.setSimilarity(new BM25Similarity(0.05f, 0.03f));
		//config.setSimilarity(new BM25Similarity(1.2f, 0.75f));
		//config.setSimilarity(new LMJelinekMercerSimilarity(lambda));
		//config.setSimilarity(new LMDirichletSimilarity());
		parser = new QueryParser("content", standardAnalyzer);
		//System.out.println("Initializing lucene index, config and parser");
	}
	
	
	
	
	/*
	public void buildSearchManagerLemma(Map<Integer, Bucket> allAnswersWithUpvotesAndCodeBucketsMap, SearcherParams searcherParams) throws Exception {
		logger.info("LuceneSearcher.buildSearchManagerLemma: searcherParams="+searcherParams+". Indexing all upvoted scored aswers with code: "+allAnswersWithUpvotesAndCodeBucketsMap.size());
		long initTime2 = System.currentTimeMillis();
	
		config.setSimilarity(searcherParams.getSimilarity());
		
		w = new IndexWriter(index, config);
		
		Set<Integer> bucketsIds = allAnswersWithUpvotesAndCodeBucketsMap.keySet();
		
		for (Integer id: bucketsIds) {
			Bucket answerBucket = allAnswersWithUpvotesAndCodeBucketsMap.get(id);
			String finalContent = answerBucket.getParentProcessedTitleLemma()+" "+answerBucket.getParentProcessedBodyLemma()+" "+answerBucket.getProcessedBodyLemma();
			addDocument(w, finalContent, id);
		}
		
		w.close();
		reader = DirectoryReader.open(index);
		searcher = new IndexSearcher(reader);
		
		searcher.setSimilarity(searcherParams.getSimilarity());
		
		CrarUtils.reportElapsedTime(initTime2,"LuceneSearcher.buildSearchManagerLemma");
	}*/
	
	
	public void buildSearchManager(Map<Integer, Bucket> allAnswersWithUpvotesAndCodeBucketsMap, SearcherParams searcherParams) throws Exception {
		logger.info("LuceneSearcher.buildSearchManager: searcherParams="+searcherParams+". Indexing all upvoted scored aswers with code: "+allAnswersWithUpvotesAndCodeBucketsMap.size());
		long initTime2 = System.currentTimeMillis();
		config.setSimilarity(searcherParams.getSimilarity());
		
		w = new IndexWriter(index, config);
		
		Set<Integer> bucketsIds = allAnswersWithUpvotesAndCodeBucketsMap.keySet();
		
		for (Integer id: bucketsIds) {
			Bucket answerBucket = allAnswersWithUpvotesAndCodeBucketsMap.get(id);
			String finalContent = answerBucket.getParentProcessedTitle()+" "+answerBucket.getParentProcessedBody()+" "+answerBucket.getParentProcessedCode()+" "+answerBucket.getProcessedBody()+ " "+answerBucket.getProcessedCode();
			addDocument(w, finalContent, id);
			
		}
		
		w.close();
		reader = DirectoryReader.open(index);
		searcher = new IndexSearcher(reader);
		
		searcher.setSimilarity(searcherParams.getSimilarity());
		
		CrarUtils.reportElapsedTime(initTime2,"LuceneSearcher.buildSearchManager");
	}
	
	public void buildSearchManager(Set<Bucket> candidateBuckets, SearcherParams searcherParams) throws Exception {
		//long initTime2 = System.currentTimeMillis();
		config.setSimilarity(searcherParams.getSimilarity());
		
		w = new IndexWriter(index, config);
		
		for (Bucket answerBucket: candidateBuckets) {
			String finalContent = answerBucket.getParentProcessedTitle()+" "+answerBucket.getParentProcessedBody()+" "+answerBucket.getParentProcessedCode()+" "+answerBucket.getProcessedBody()+ " "+answerBucket.getProcessedCode();
			addDocument(w, finalContent, answerBucket.getId());
		}
		
		w.close();
		reader = DirectoryReader.open(index);
		searcher = new IndexSearcher(reader);
		
		searcher.setSimilarity(searcherParams.getSimilarity());
		
		//CrarUtils.reportElapsedTime(initTime2,"LuceneSearcher.buildSearchManagerLemma");
	}
	
	public void buildSearchManagerForThreads(Map<Integer, ThreadContent> validThreadsMap, SearcherParams searcherParams) throws IOException {
		logger.info("LuceneSearcher.buildSearchManagerForThreads: searcherParams="+searcherParams+". Indexing all valid threads: "+validThreadsMap.size());
		long initTime2 = System.currentTimeMillis();
		config.setSimilarity(searcherParams.getSimilarity());
		
		w = new IndexWriter(index, config);
		
		Set<Integer> threadsIds = validThreadsMap.keySet();
		
		for (Integer id: threadsIds) {
			ThreadContent threadContent = validThreadsMap.get(id);
			String finalContent = threadContent.getTitle()+" "+threadContent.getQuestionBody()+" "+threadContent.getAnswersBody()+ " "+threadContent.getCode();
			addDocument(w, finalContent, id);
		}
		
		w.close();
		reader = DirectoryReader.open(index);
		searcher = new IndexSearcher(reader);
		searcher.setSimilarity(searcherParams.getSimilarity());
		
		CrarUtils.reportElapsedTime(initTime2,"LuceneSearcher.buildSearchManagerForThreads");
		
	}
	
	

	public Set<Integer> search(String query, Integer trmLimit1, Integer trmLimit2, Integer trmLimit3, Map<Integer, Float> idScoreMap) throws Exception {
		Set<Integer> limitSet1 = new LinkedHashSet<>();
		Set<Integer> limitSet2 = new LinkedHashSet<>();
		Set<Integer> limitSet3 = new LinkedHashSet<>();
		Set<Integer> limitSetAll = new LinkedHashSet<>();
		
		idScoreMap.clear();
		Query myquery = parser.parse(query);
		
		TopDocs docs1 = null;
		TopDocs docs2 = null;
		TopDocs docs3 = null;
		
		docs1 = searcher.search(myquery, trmLimit1);
		getDocs(limitSet1,docs1,idScoreMap);
		limitSetAll.addAll(limitSet1);
		
		if(!limitSet1.isEmpty()) {
			System.out.println("First list 25: "+Iterables.get(limitSet1, trmLimit1-1));
		}
		
		if(trmLimit2!=null) {
			docs2 = searcher.search(myquery, trmLimit2);
			getDocs(limitSet2,docs2,idScoreMap);
			limitSetAll.addAll(limitSet2);
			if(!limitSet2.isEmpty()) {
				System.out.println("First list 25: "+Iterables.get(limitSet1, trmLimit1-1));
			}
		}
		if(trmLimit3!=null) {
			docs3 = searcher.search(myquery, trmLimit3);
			getDocs(limitSet3,docs3,idScoreMap);
			limitSetAll.addAll(limitSet3);
			if(!limitSet3.isEmpty()) {
				System.out.println("First list 25: "+Iterables.get(limitSet1, trmLimit1-1));
			}
		}
		
		limitSet1=null;
		limitSet2=null;
		limitSet3=null;
		myquery=null;
		docs1=null;
		docs2=null;
		docs3=null;
		hits=null;		
		return limitSetAll;		
		
	}
	
	public Set<Integer> search(String query, Integer trmLimit1, Map<Integer, Float> idScoreMap) throws Exception {
		Set<Integer> limitSet = new LinkedHashSet<>();
		
		idScoreMap.clear();
		Query myquery = parser.parse(query);
		
		TopDocs docs = searcher.search(myquery, trmLimit1);
		
		ScoreDoc[] hits = docs.scoreDocs;
		
		for (int i = 0; i < hits.length; i++) {
			ScoreDoc item = hits[i];
			Document doc = searcher.doc(item.doc);
			idScoreMap.put(Integer.valueOf(doc.get("id")), item.score);
			limitSet.add(Integer.valueOf(doc.get("id")));
		}
		
		myquery=null;
		docs=null;
		hits=null;		
		return limitSet;		
		
	}
	
	private void getDocs(Set<Integer> limitSet, TopDocs docs, Map<Integer, Float> idScoreMap) throws IOException {
		ScoreDoc[] hits = docs.scoreDocs;
		
		for (int i = 0; i < hits.length; i++) {
			ScoreDoc item = hits[i];
			Document doc = searcher.doc(item.doc);
			idScoreMap.put(Integer.valueOf(doc.get("id")), item.score);
			limitSet.add(Integer.valueOf(doc.get("id")));
		}
		
		hits=null;
	}

	/*public Set<Integer> searchDupes(String query, Integer smallSetLimit, Bucket nonMaster) throws Exception {
		Set<Integer> smallSetAnswersIds = new LinkedHashSet<>();
		QueryParser parser = new QueryParser("content", standardAnalyzer);
		Query myquery = parser.parse(query);
		TopDocs docs = searcher.search(myquery, smallSetLimit);
				
		ScoreDoc[] hits = docs.scoreDocs;
		if(hits.length<smallSetLimit) {
			smallSetLimit=hits.length;
		}
			
		for (int i = 0; i < smallSetLimit; i++) {
			ScoreDoc item = hits[i];
			Document doc = searcher.doc(item.doc);
			smallSetAnswersIds.add(Integer.valueOf(doc.get("id")));
		}
		
		for (int i = smallSetLimit; i < bigSetLimit; i++) {
			ScoreDoc item = hits[i];
			Document doc = searcher.doc(item.doc);
			bm25ScoreAnswerIdMap.put(Integer.valueOf(doc.get("id")), item.score);
			bigSetAnswersIds.add(Integer.valueOf(doc.get("id")));
		}
				
		return smallSetAnswersIds;	
	}*/

	/*
	 * public Integer search(String querystr, Integer
	 * maxConsideredSeachNumberForClassifier) throws Exception { Query q = new
	 * QueryParser("content", analyzer).parse(querystr); TopDocs docs =
	 * searcher.search(q, maxConsideredSeachNumberForClassifier); hits =
	 * docs.scoreDocs; q = null; return hits.length; }
	 */

	public Integer getQuestion(int numTest) throws Exception {
		int docId = hits[numTest].doc;
		Document d = searcher.doc(docId);

		Integer id = Integer.valueOf(d.get("id"));

		return id;
	}
	/*
	 * public Question getQuestion(int numTest) throws Exception { int docId =
	 * hits[numTest].doc; Document d = searcher.doc(docId);
	 * 
	 * Integer id = Integer.valueOf(d.get("id")); String title = d.get("title");
	 * String body = d.get("body"); String tags = d.get("tags"); Question
	 * question = new Question(id,title,body,tags);
	 * 
	 * return question; }
	 */

	public Integer getDocno(int numTest) throws Exception {
		int docId = hits[numTest].doc;
		Document d = searcher.doc(docId);
		return Integer.valueOf(d.get("id"));
	}

	public void finalize() throws Exception {
		reader.close();
	}

	private static void addDocument(IndexWriter w, String postContent, Integer id) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("content", postContent, Field.Store.YES));
		doc.add(new StringField("id", id.toString(), Field.Store.YES));
		w.addDocument(doc);
	}

	
	/*public Bucket getAnswer(Integer id) {
		return answersCache.get(id);
	}*/
	

	public void setSearchSimilarityParams(Float bm25ParameterK, Float bm25ParameterB) {
		searcher.setSimilarity(new BM25Similarity(bm25ParameterK, bm25ParameterB));
		config.setSimilarity(new BM25Similarity(bm25ParameterK, bm25ParameterB));
		logger.info("Setting k: "+bm25ParameterK+ " b: "+bm25ParameterB);
	}


	public Set<Integer> getBigSetAnswersIds() {
		return bigSetAnswersIds;
	}

	


	/*public void buildSearchManager(List<Bucket> allUpvotedScoredQuestions) throws IOException {
		logger.info("LuceneSearcher.buildSearchManager. Indexing all upvoted scored questions: "+allUpvotedScoredQuestions.size());
		long initTime2 = System.currentTimeMillis();
		
		indexedListSize = allUpvotedScoredQuestions.size();
		IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
		IndexWriter w = new IndexWriter(index, config);
		
		for (Bucket bucket: allUpvotedScoredQuestions) {
			String finalContent = bucket.getParentProcessedTitle()+" "+bucket.getParentProcessedBody()+ " "+bucket.getProcessedCode();
			if(bucket.getAcceptedAnswerId()!=null) {
				finalContent+= " "+bucket.getAcceptedOrMostUpvotedAnswerOfParentProcessedBody()+ " "+bucket.getAcceptedOrMostUpvotedAnswerOfParentProcessedCode();
			}
			addDocument(w, finalContent, bucket.getId());
			
		}
		w.close();
		
		reader = DirectoryReader.open(index);
		searcher = new IndexSearcher(reader);
		CrarUtils.reportElapsedTime(initTime2,"LuceneSearcher.buildSearchManager");
		
	}


	public void buildSearchManagerLemma(Set<Bucket> allUpvotedScoredQuestions, Map<Integer, Bucket> allUpvotedScoredAnswersMap) throws IOException {
		logger.info("LuceneSearcher.buildSearchManager. Indexing all upvoted scored questions: "+allUpvotedScoredQuestions.size());
		long initTime2 = System.currentTimeMillis();
		
		indexedListSize = allUpvotedScoredQuestions.size();
		IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
		IndexWriter w = new IndexWriter(index, config);
		
		for (Bucket bucket: allUpvotedScoredQuestions) {
			String finalContent = bucket.getParentProcessedTitleLemma()+" "+bucket.getParentProcessedBodyLemma()+ " "+bucket.getProcessedCode();
			
			if(!CollectionUtils.isEmpty(bucket.getTopScoredAnswersSet())) {
				for(Integer childId: bucket.getTopScoredAnswersSet()) {
					Bucket child = allUpvotedScoredAnswersMap.get(childId);					
					finalContent+=" "+child.getProcessedBodyLemma()+ " "+child.getProcessedCode();
				}
			}
			addDocument(w, finalContent, bucket.getId());
		}
		w.close();
		
		reader = DirectoryReader.open(index);
		searcher = new IndexSearcher(reader);
		CrarUtils.reportElapsedTime(initTime2,"LuceneSearcher.buildSearchManager");
		
	}*/


	


	
	
	

	
}