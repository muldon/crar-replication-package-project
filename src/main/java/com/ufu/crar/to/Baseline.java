package com.ufu.crar.to;

import com.ufu.crar.config.CrarParameters;
import com.ufu.crar.to.Tags.TagEnum;
import com.ufu.crar.util.SearcherParams;

public class Baseline {

	public String name;
	public String language;
	public String dataSet;
	
	public Integer tagId;
	
	public double classFreqWeight;  
	public double tfIdfWeight; 	   
	public double methodFreqWeight;
	public double semWeight;
	
	public Integer bm25SmallLimit;
	public Integer bm25BigLimit;
	
	public Boolean onlyTrm;
	public Boolean isCrokage;

	private Integer vectorsTypeId;
	/*
	 * Crokage's extension params
	 */
	public Boolean reduceThreads;
	public int trmLimit1Threads;
	public int topRelevantThreadsLimit1;
	public int topRelevantThreadsLimit2;
	public int numberOfRoundsInReducingThreads;  //0 for no reducing (BM25 return), 1 for reducing to topRelevantThreadsLimitRound1, 2 for reducing to topRelevantThreadsLimitRound2
	public int trmLimitAnswers;   
	public String evaluationOf; //" Threads" | "Answers"
		
	//Antonyms for: NN (Nouns), VB(Verbs), All, None (do not use this feature) 
	//https://stackoverflow.com/questions/1833252/java-stanford-nlp-part-of-speech-labels
	public String antonymsFor; //"None" | "All" | "VB", "NN" 

	//Antonyms Filter: threads, answers, threads&answers 
	public String antonymsFilter;
	
	//threads round1
	public double threadRound1TitleAsymmetricSimWeight;
	public double threadRound1BodyAsymmetricSimWeight;
	public double threadRound1TfIdfCosineSimScoreWeight; 
	public double threadRound1BM25ScoreWeight;
	public double threadRound1QueryTitleCoverageScoreWeight;
	public double threadRound1QueryCodeCoverageScoreWeight;
	public double threadRound1JaccardScoreWeight;
	public double threadRound1Sent2VecWeight;
	
	
	public double threadRound1NGrams2ScoreWeight;
	public double threadRound1NGrams3ScoreWeight;
	public double threadRound1NGrams4ScoreWeight;
	
	//threads round2
	public double threadRound2TitleAsymmetricSimWeight;
	public double threadRound2BodyAsymmetricSimWeight;
	public double threadRound2TfIdfCosineSimScoreWeight;
	public double threadRound2AnswerCountScoreWeight;
	public double threadRound2UpVotesScoreScoreWeight;
	public double threadRound2TotalUpvotesScoreWeight;
	public double threadRound2BM25ScoreWeight;
	public double threadRound2QueryTitleCoverageScoreWeight;
	public double threadRound2QueryCodeCoverageScoreWeight;
	public double threadRound2JaccardScoreWeight;
	public double threadRound2Sent2VecWeight;
	
	
	public double threadRound2NGrams2ScoreWeight;
	public double threadRound2NGrams3ScoreWeight;
	public double threadRound2NGrams4ScoreWeight;
	
	//thread filters
	public double threadFilterTopTitleAsymmetricSim;
	public double threadFilterTopBodyAsymmetricSim;
	public double threadFilterTopBM25Sim;
	public double threadFilterTopTfIdfSim;
	public double threadFilterTopThreadTotalUpScoreNorm;
	public double threadFilterTopThreadAnswerCountNorm;
	public double threadFilterTopThreadUpVotes;
	
	public boolean useThreadFilterQueryCoverageScoreMinThreshold;
	public double threadFilterQueryCoverageScoreMinThresholdValue;
	
	//answers
	public double answersThreadScoreWeight;
	public double answersAsymmScoreWeight;
	public double answersTfIdfScoreWeight;
	public double answersMethodFreqScoreWeight;
	public double answersBM25ScoreWeight;
	public double answersJaccardScoreWeight;
	public double answersQueryCoverageScoreWeight;
	public double answersCodeCoverageScoreWeight;
	public double answersAPIClassScoreWeight;
	
	public SearcherParams searchParam;
	public String weightingScheme;
	public Boolean useSocialFactors;
	
	public Boolean isWeightsTraining;

	/*public Baseline(String name, String language, Integer tagId, Boolean reduceThreads,
			int trmLimit1Threads, int topRelevantThreadsLimitRound1, int topRelevantThreadsLimitRound2,
			int numberOfRoundsInReducingThreads, int trmLimitAnswers, String evaluationOf, String antonymsFor,
			String antonymsFilter, double threadRound1TitleAsymmetricSimWeight,
			double threadRound1BodyAsymmetricSimWeight, double threadRound1TfIdfCosineSimScoreWeight,
			double threadRound1BM25ScoreWeight, double threadRound1QueryTitleCoverageScoreWeight,
			double threadRound1QueryCodeCoverageScoreWeight, double threadRound1NGrams2ScoreWeight,
			double threadRound1NGrams3ScoreWeight, double threadRound1NGrams4ScoreWeight,
			double threadRound2TitleAsymmetricSimWeight, double threadRound2BodyAsymmetricSimWeight,
			double threadRound2TfIdfCosineSimScoreWeight, double threadRound2AnswerCountScoreWeight,
			double threadRound2UpVotesScoreScoreWeight, double threadRound2TotalUpvotesScoreWeight,
			double threadRound2BM25ScoreWeight, double threadRound2QueryTitleCoverageScoreWeight,
			double threadRound2QueryCodeCoverageScoreWeight, double threadRound2NGrams2ScoreWeight,
			double threadRound2NGrams3ScoreWeight, double threadRound2NGrams4ScoreWeight,
			double threadFilterTopTitleAsymmetricSim, double threadFilterTopBodyAsymmetricSim,
			double threadFilterTopBM25Sim, double threadFilterTopTfIdfSim, double threadFilterTopThreadTotalUpScoreNorm,
			double threadFilterTopThreadAnswerCountNorm, double threadFilterTopThreadUpVotes,
			double answersThreadScoreWeight, double answersAsymmScoreWeight, double answersTfIdfScoreWeight,
			double answersMethodFreqScoreWeight, double answersBM25ScoreWeight, double answersJaccardScoreWeight,
			double answersQueryCoverageScoreWeight, double answersCodeCoverageScoreWeight, boolean useThreadFilterQueryCoverageScoreMinThreshold,
			double threadFilterQueryCoverageScoreThreshold, double threadRound1JaccardScoreWeight) {
		super();
		this.name = name;
		this.language = language;
		this.tagId = tagId;
		this.reduceThreads = reduceThreads;
		this.trmLimit1Threads = trmLimit1Threads;
		this.topRelevantThreadsLimitRound1 = topRelevantThreadsLimitRound1;
		this.topRelevantThreadsLimitRound2 = topRelevantThreadsLimitRound2;
		this.numberOfRoundsInReducingThreads = numberOfRoundsInReducingThreads;
		this.trmLimitAnswers = trmLimitAnswers;
		this.evaluationOf = evaluationOf; //" Threads" | "Answers"
		this.antonymsFor = antonymsFor;  //"None" | "All" | "VB", "NN" 
		this.antonymsFilter = antonymsFilter; //threads, answers
		this.threadRound1TitleAsymmetricSimWeight = threadRound1TitleAsymmetricSimWeight;
		this.threadRound1BodyAsymmetricSimWeight = threadRound1BodyAsymmetricSimWeight;
		this.threadRound1TfIdfCosineSimScoreWeight = threadRound1TfIdfCosineSimScoreWeight;
		this.threadRound1BM25ScoreWeight = threadRound1BM25ScoreWeight;
		this.threadRound1QueryTitleCoverageScoreWeight = threadRound1QueryTitleCoverageScoreWeight;
		this.threadRound1QueryCodeCoverageScoreWeight = threadRound1QueryCodeCoverageScoreWeight;
		this.threadRound1NGrams2ScoreWeight = threadRound1NGrams2ScoreWeight;
		this.threadRound1NGrams3ScoreWeight = threadRound1NGrams3ScoreWeight;
		this.threadRound1NGrams4ScoreWeight = threadRound1NGrams4ScoreWeight;
		this.threadRound2TitleAsymmetricSimWeight = threadRound2TitleAsymmetricSimWeight;
		this.threadRound2BodyAsymmetricSimWeight = threadRound2BodyAsymmetricSimWeight;
		this.threadRound2TfIdfCosineSimScoreWeight = threadRound2TfIdfCosineSimScoreWeight;
		this.threadRound2AnswerCountScoreWeight = threadRound2AnswerCountScoreWeight;
		this.threadRound2UpVotesScoreScoreWeight = threadRound2UpVotesScoreScoreWeight;
		this.threadRound2TotalUpvotesScoreWeight = threadRound2TotalUpvotesScoreWeight;
		this.threadRound2BM25ScoreWeight = threadRound2BM25ScoreWeight;
		this.threadRound2QueryTitleCoverageScoreWeight = threadRound2QueryTitleCoverageScoreWeight;
		this.threadRound2QueryCodeCoverageScoreWeight = threadRound2QueryCodeCoverageScoreWeight;
		this.threadRound2NGrams2ScoreWeight = threadRound2NGrams2ScoreWeight;
		this.threadRound2NGrams3ScoreWeight = threadRound2NGrams3ScoreWeight;
		this.threadRound2NGrams4ScoreWeight = threadRound2NGrams4ScoreWeight;
		this.threadFilterTopTitleAsymmetricSim = threadFilterTopTitleAsymmetricSim;
		this.threadFilterTopBodyAsymmetricSim = threadFilterTopBodyAsymmetricSim;
		this.threadFilterTopBM25Sim = threadFilterTopBM25Sim;
		this.threadFilterTopTfIdfSim = threadFilterTopTfIdfSim;
		this.threadFilterTopThreadTotalUpScoreNorm = threadFilterTopThreadTotalUpScoreNorm;
		this.threadFilterTopThreadAnswerCountNorm = threadFilterTopThreadAnswerCountNorm;
		this.threadFilterTopThreadUpVotes = threadFilterTopThreadUpVotes;
		this.answersThreadScoreWeight = answersThreadScoreWeight;
		this.answersAsymmScoreWeight = answersAsymmScoreWeight;
		this.answersTfIdfScoreWeight = answersTfIdfScoreWeight;
		this.answersMethodFreqScoreWeight = answersMethodFreqScoreWeight;
		this.answersBM25ScoreWeight = answersBM25ScoreWeight;
		this.answersJaccardScoreWeight = answersJaccardScoreWeight;
		this.answersQueryCoverageScoreWeight = answersQueryCoverageScoreWeight;
		this.answersCodeCoverageScoreWeight = answersCodeCoverageScoreWeight;
		this.threadFilterQueryCoverageScoreMinThresholdValue = threadFilterQueryCoverageScoreThreshold;
		this.useThreadFilterQueryCoverageScoreMinThreshold = useThreadFilterQueryCoverageScoreMinThreshold;
		
		this.searchParam = CrarParameters.getSearchParamForTagId(tagId);
		this.weightingScheme = CrarParameters.getWeightingSchemeForTagId(tagId);
		this.threadRound1JaccardScoreWeight = threadRound1JaccardScoreWeight;
	}
	
	*/
	


	public Baseline(String name, TagEnum language, boolean isCrokage, String antonymsFor,
			String antonymsFilter, boolean useThreadFilterQueryCoverageScoreMinThreshold, int numberOfRoundsInReducingThreads) {
		super();
		this.name = name;
		this.dataSet = language.getDescricao(); 
		this.tagId = language.getId();
		this.antonymsFor = antonymsFor;
		this.antonymsFilter = antonymsFilter;
		this.reduceThreads=true;
		this.useThreadFilterQueryCoverageScoreMinThreshold = useThreadFilterQueryCoverageScoreMinThreshold;
		this.searchParam = CrarParameters.getSearchParamForTagId(tagId);
		this.weightingScheme = CrarParameters.getWeightingSchemeForTagId(tagId);
		this.numberOfRoundsInReducingThreads = numberOfRoundsInReducingThreads;
		if(numberOfRoundsInReducingThreads==2) {
			this.useSocialFactors=true;
		}else {
			this.useSocialFactors=false;
		}
		this.isCrokage=isCrokage;
		
		if(tagId.equals(TagEnum.Java.getId())) {
			this.language=TagEnum.Java.getDescricao();
			
			this.trmLimit1Threads              = CrarParameters.trmLimit1ThreadsJava;
			this.topRelevantThreadsLimit1 	   = CrarParameters.topRelevantThreadsLimit1Java;
			this.topRelevantThreadsLimit2 	   = CrarParameters.topRelevantThreadsLimit2Java;
			this.trmLimitAnswers               = CrarParameters.trmLimitAnswersJava;
			
			this.threadRound1JaccardScoreWeight        = CrarParameters.threadRound1JaccardScoreWeightJava;
			this.threadRound1TitleAsymmetricSimWeight  = CrarParameters.threadRound1TitleAsymmetricSimWeightJava;
			this.threadRound1BodyAsymmetricSimWeight   = CrarParameters.threadRound1QuestionBodyAnswersBodyAsymmetricSimWeightJava;
			this.threadRound1TfIdfCosineSimScoreWeight = CrarParameters.threadRound1TfIdfCosineSimScoreWeightJava;
			this.threadRound1BM25ScoreWeight           = CrarParameters.threadRound1BM25ScoreWeightJava;
			this.threadRound1Sent2VecWeight            = CrarParameters.threadRound1Sent2VecWeightJava;
			
			this.threadRound2TitleAsymmetricSimWeight   = CrarParameters.threadRound2TitleAsymmetricSimWeightJava;
			this.threadRound2JaccardScoreWeight         = CrarParameters.threadRound2JaccardScoreWeightJava;
			this.threadRound2BodyAsymmetricSimWeight    = CrarParameters.threadRound2BodyAsymmetricSimWeightJava;
			this.threadRound2TfIdfCosineSimScoreWeight  = CrarParameters.threadRound2TfIdfCosineSimScoreWeightJava;
			this.threadRound2AnswerCountScoreWeight     = CrarParameters.threadRound2AnswerCountScoreWeightJava;
			this.threadRound2UpVotesScoreScoreWeight    = CrarParameters.threadRound2UpVotesScoreScoreWeightJava;
			this.threadRound2TotalUpvotesScoreWeight    = CrarParameters.threadRound2TotalUpvotesScoreWeightJava;
			this.threadRound2Sent2VecWeight             = CrarParameters.threadRound2Sent2VecWeightJava;
						
			this.answersMethodFreqScoreWeight   = CrarParameters.answersMethodFreqScoreWeightJava;
			this.answersThreadScoreWeight       = CrarParameters.answersThreadScoreWeightJava;
			this.answersTfIdfScoreWeight        = CrarParameters.answersTfIdfScoreWeightJava;
			this.answersAsymmScoreWeight        = CrarParameters.answersAsymmScoreWeightJava;
			
		}else { //python
			this.language=TagEnum.Python.getDescricao();
			
			this.trmLimit1Threads              = CrarParameters.trmLimit1ThreadsPython;
			this.topRelevantThreadsLimit1 = CrarParameters.topRelevantThreadsLimit1Python;
			this.topRelevantThreadsLimit2 = CrarParameters.topRelevantThreadsLimit2Python;
			this.trmLimitAnswers               = CrarParameters.trmLimitAnswersPython;
			
			this.threadRound1JaccardScoreWeight        = CrarParameters.threadRound1JaccardScoreWeightPython;
			this.threadRound1TitleAsymmetricSimWeight  = CrarParameters.threadRound1TitleAsymmetricSimWeightPython;
			this.threadRound1BodyAsymmetricSimWeight   = CrarParameters.threadRound1BodyAsymmetricSimWeightPython;
			this.threadRound1TfIdfCosineSimScoreWeight = CrarParameters.threadRound1TfIdfCosineSimScoreWeightPython;
			this.threadRound1BM25ScoreWeight           = CrarParameters.threadRound1BM25ScoreWeightPython;
			this.threadRound1Sent2VecWeight            = CrarParameters.threadRound1Sent2VecWeightPython;
			
			this.threadRound2TitleAsymmetricSimWeight   = CrarParameters.threadRound2TitleAsymmetricSimWeightPython;
			this.threadRound2JaccardScoreWeight         = CrarParameters.threadRound2JaccardScoreWeightPython;
			this.threadRound2BodyAsymmetricSimWeight    = CrarParameters.threadRound2BodyAsymmetricSimWeightPython;
			this.threadRound2TfIdfCosineSimScoreWeight  = CrarParameters.threadRound2TfIdfCosineSimScoreWeightPython;
			this.threadRound2AnswerCountScoreWeight     = CrarParameters.threadRound2AnswerCountScoreWeightPython;
			this.threadRound2UpVotesScoreScoreWeight    = CrarParameters.threadRound2UpVotesScoreScoreWeightPython;
			this.threadRound2TotalUpvotesScoreWeight    = CrarParameters.threadRound2TotalUpvotesScoreWeightPython;
			this.threadRound2Sent2VecWeight             = CrarParameters.threadRound1Sent2VecWeightPython;
						
			this.answersMethodFreqScoreWeight   = CrarParameters.answersMethodFreqScoreWeightPython;
			this.answersThreadScoreWeight       = CrarParameters.answersThreadScoreWeightPython;
			this.answersTfIdfScoreWeight        = CrarParameters.answersTfIdfScoreWeightPython;
			this.answersAsymmScoreWeight        = CrarParameters.answersAsymmScoreWeightPython;
		}
		
		
		if(isCrokage) {
			this.reduceThreads=false;
			this.answersAsymmScoreWeight      = 1.00;
			this.answersMethodFreqScoreWeight = 1.00;
			this.answersTfIdfScoreWeight 	  = 0.5;
			this.answersAPIClassScoreWeight   = 0.25;
			
			if(tagId.equals(TagEnum.Java.getId())) {
				this.trmLimitAnswers=100;
			}else { //python
				this.trmLimitAnswers=60;
			}
		}
		
		if (name.equals("BM25+CNN")) {
			this.threadRound1JaccardScoreWeight        = 0;
			this.threadRound1TitleAsymmetricSimWeight  =0;
			this.threadRound1BodyAsymmetricSimWeight   = 0;
			this.threadRound1TfIdfCosineSimScoreWeight = 0;
			this.threadRound1BM25ScoreWeight           = 0;
			this.threadRound1Sent2VecWeight            = 1;
			
			this.threadRound2TitleAsymmetricSimWeight   = 0;
			this.threadRound2JaccardScoreWeight         = 0;
			this.threadRound2BodyAsymmetricSimWeight    = 0;
			this.threadRound2TfIdfCosineSimScoreWeight  = 0;
			this.threadRound2AnswerCountScoreWeight     = 0;
			this.threadRound2UpVotesScoreScoreWeight    = 0;
			this.threadRound2TotalUpvotesScoreWeight    = 0;
			this.threadRound2Sent2VecWeight             = 1;
		}
		
		if (name.equals("SENT2VEC")) {
			this.threadRound1JaccardScoreWeight        = 0;
			this.threadRound1TitleAsymmetricSimWeight  = 0;
			this.threadRound1BodyAsymmetricSimWeight   = 0;
			this.threadRound1TfIdfCosineSimScoreWeight = 0;
			this.threadRound1BM25ScoreWeight           = 0;
			this.threadRound1Sent2VecWeight            = 1;
			
			this.threadRound2TitleAsymmetricSimWeight   = 0;
			this.threadRound2JaccardScoreWeight         = 0;
			this.threadRound2BodyAsymmetricSimWeight    = 0;
			this.threadRound2TfIdfCosineSimScoreWeight  = 0;
			this.threadRound2AnswerCountScoreWeight     = 0;
			this.threadRound2UpVotesScoreScoreWeight    = 0;
			this.threadRound2TotalUpvotesScoreWeight    = 0;
			this.threadRound2Sent2VecWeight             = 1;
		}
		
		if (name.equals("ASSIMETRIC")) {
			this.threadRound1JaccardScoreWeight        = 0;
			this.threadRound1TitleAsymmetricSimWeight  = 1;
			this.threadRound1BodyAsymmetricSimWeight   = 0;
			this.threadRound1TfIdfCosineSimScoreWeight = 0;
			this.threadRound1BM25ScoreWeight           = 0;
			this.threadRound1Sent2VecWeight            = 0;
			
			this.threadRound2TitleAsymmetricSimWeight   = 1;
			this.threadRound2JaccardScoreWeight         = 0;
			this.threadRound2BodyAsymmetricSimWeight    = 0;
			this.threadRound2TfIdfCosineSimScoreWeight  = 0;
			this.threadRound2AnswerCountScoreWeight     = 0;
			this.threadRound2UpVotesScoreScoreWeight    = 0;
			this.threadRound2TotalUpvotesScoreWeight    = 0;
			this.threadRound2Sent2VecWeight             = 0;
		}
		
		if (name.equals("BODYASS")) {
			this.threadRound1JaccardScoreWeight        = 0;
			this.threadRound1TitleAsymmetricSimWeight  = 0;
			this.threadRound1BodyAsymmetricSimWeight   = 1;
			this.threadRound1TfIdfCosineSimScoreWeight = 0;
			this.threadRound1BM25ScoreWeight           = 0;
			this.threadRound1Sent2VecWeight            = 0;
			
			this.threadRound2TitleAsymmetricSimWeight   = 0;
			this.threadRound2JaccardScoreWeight         = 0;
			this.threadRound2BodyAsymmetricSimWeight    = 1;
			this.threadRound2TfIdfCosineSimScoreWeight  = 0;
			this.threadRound2AnswerCountScoreWeight     = 0;
			this.threadRound2UpVotesScoreScoreWeight    = 0;
			this.threadRound2TotalUpvotesScoreWeight    = 0;
			this.threadRound2Sent2VecWeight             = 0;
		}
		
		if (name.equals("TFIDFCOS")) {
			this.threadRound1JaccardScoreWeight        = 0;
			this.threadRound1TitleAsymmetricSimWeight  = 0;
			this.threadRound1BodyAsymmetricSimWeight   = 0;
			this.threadRound1TfIdfCosineSimScoreWeight = 1;
			this.threadRound1BM25ScoreWeight           = 0;
			this.threadRound1Sent2VecWeight            = 0;
			
			this.threadRound2TitleAsymmetricSimWeight   = 0;
			this.threadRound2JaccardScoreWeight         = 0;
			this.threadRound2BodyAsymmetricSimWeight    = 0;
			this.threadRound2TfIdfCosineSimScoreWeight  = 1;
			this.threadRound2AnswerCountScoreWeight     = 0;
			this.threadRound2UpVotesScoreScoreWeight    = 0;
			this.threadRound2TotalUpvotesScoreWeight    = 0;
			this.threadRound2Sent2VecWeight             = 0;
		}
		
		if (name.equals("JACCARD")) {
			this.threadRound1JaccardScoreWeight        = 1;
			this.threadRound1TitleAsymmetricSimWeight  = 0;
			this.threadRound1BodyAsymmetricSimWeight   = 0;
			this.threadRound1TfIdfCosineSimScoreWeight = 0;
			this.threadRound1BM25ScoreWeight           = 0;
			this.threadRound1Sent2VecWeight            = 0;
			
			this.threadRound2TitleAsymmetricSimWeight   = 0;
			this.threadRound2JaccardScoreWeight         = 1;
			this.threadRound2BodyAsymmetricSimWeight    = 0;
			this.threadRound2TfIdfCosineSimScoreWeight  = 0;
			this.threadRound2AnswerCountScoreWeight     = 0;
			this.threadRound2UpVotesScoreScoreWeight    = 0;
			this.threadRound2TotalUpvotesScoreWeight    = 0;
			this.threadRound2Sent2VecWeight             = 0;
		}
		
		if (name.equals("BM25")) {
			this.threadRound1JaccardScoreWeight        = 0;
			this.threadRound1TitleAsymmetricSimWeight  = 0;
			this.threadRound1BodyAsymmetricSimWeight   = 0;
			this.threadRound1TfIdfCosineSimScoreWeight = 0;
			this.threadRound1BM25ScoreWeight           = 1;
			this.threadRound1Sent2VecWeight            = 0;
			
			this.threadRound2TitleAsymmetricSimWeight   = 0;
			this.threadRound2JaccardScoreWeight         = 0;
			this.threadRound2BodyAsymmetricSimWeight    = 0;
			this.threadRound2TfIdfCosineSimScoreWeight  = 0;
			this.threadRound2AnswerCountScoreWeight     = 0;
			this.threadRound2UpVotesScoreScoreWeight    = 0;
			this.threadRound2TotalUpvotesScoreWeight    = 0;
			this.threadRound2Sent2VecWeight             = 0;
		}
		
		
	}





	public Baseline(String name, String language, String dataSet, Integer tagId, double classFreqWeight, double tfIdfWeight, double methodFreqWeight, double semWeight, Integer bm25SmallLimit) {
		super();
		this.name = name;
		this.language = language;
		this.dataSet = dataSet;
		this.tagId = tagId;
		this.classFreqWeight = classFreqWeight;
		this.tfIdfWeight = tfIdfWeight;
		this.methodFreqWeight = methodFreqWeight;
		this.semWeight = semWeight;
		this.bm25SmallLimit = bm25SmallLimit;
		this.isCrokage = false;
		
		/*if(semWeight==0d && methodFreqWeight==0d && tfIdfWeight==0 && classFreqWeight==0d) {
			this.onlyTrm=true;
		}else {
			this.onlyTrm=false;
		}*/
		
	}
	
	
	/*public Baseline(String name, String dataSet, Integer tagId, Integer bm25SmallLimit, Integer bm25BigLimit) {
		super();
		this.name = name;
		this.dataSet = dataSet;
		this.tagId = tagId;
		this.bm25SmallLimit = bm25SmallLimit;
		this.bm25BigLimit = bm25BigLimit;
	}*/









	public Boolean getOnlyTrm() {
		return onlyTrm;
	}









	public void setOnlyTrm(Boolean onlyTrm) {
		this.onlyTrm = onlyTrm;
	}



















	@Override
	public String toString() {
		return "Baseline [name=" + name + ", language=" + language + ", dataSet=" + dataSet + ", tagId=" + tagId
				+ ", bm25SmallLimit=" + bm25SmallLimit + ", bm25BigLimit=" + bm25BigLimit + "]";
	}


	public Integer getBm25SmallLimit() {
		return bm25SmallLimit;
	}








	public void setBm25SmallLimit(Integer bm25SmallLimit) {
		this.bm25SmallLimit = bm25SmallLimit;
	}













	public Baseline() {
		super();
	}





	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getDataSet() {
		return dataSet;
	}
	public void setDataSet(String dataSet) {
		this.dataSet = dataSet;
	}
	public Integer getTagId() {
		return tagId;
	}
	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}
	public double getClassFreqWeight() {
		return classFreqWeight;
	}
	public void setClassFreqWeight(double classFreqWeight) {
		this.classFreqWeight = classFreqWeight;
	}
	public double getTfIdfWeight() {
		return tfIdfWeight;
	}
	public void setTfIdfWeight(double tfIdfWeight) {
		this.tfIdfWeight = tfIdfWeight;
	}
	public double getMethodFreqWeight() {
		return methodFreqWeight;
	}
	public void setMethodFreqWeight(double methodFreqWeight) {
		this.methodFreqWeight = methodFreqWeight;
	}
	public double getSemWeight() {
		return semWeight;
	}
	public void setSemWeight(double semWeight) {
		this.semWeight = semWeight;
	}


	public Integer getBm25BigLimit() {
		return bm25BigLimit;
	}


	public void setBm25BigLimit(Integer bm25BigLimit) {
		this.bm25BigLimit = bm25BigLimit;
	}


	public Boolean getReduceThreads() {
		return reduceThreads;
	}


	public void setReduceThreads(Boolean reduceThreads) {
		this.reduceThreads = reduceThreads;
	}


	public int getTrmLimit1Threads() {
		return trmLimit1Threads;
	}


	public void setTrmLimit1Threads(int trmLimit1Threads) {
		this.trmLimit1Threads = trmLimit1Threads;
	}


	public int getTopRelevantThreadsLimit1() {
		return topRelevantThreadsLimit1;
	}


	public void setTopRelevantThreadsLimit1(int topRelevantThreadsLimitRound1) {
		this.topRelevantThreadsLimit1 = topRelevantThreadsLimitRound1;
	}


	public int getTopRelevantThreadsLimit2() {
		return topRelevantThreadsLimit2;
	}


	public void setTopRelevantThreadsLimit2(int topRelevantThreadsLimitRound2) {
		this.topRelevantThreadsLimit2 = topRelevantThreadsLimitRound2;
	}


	public int getNumberOfRoundsInReducingThreads() {
		return numberOfRoundsInReducingThreads;
	}


	public void setNumberOfRoundsInReducingThreads(int numberOfRoundsInReducingThreads) {
		this.numberOfRoundsInReducingThreads = numberOfRoundsInReducingThreads;
	}


	public int getTrmLimitAnswers() {
		return trmLimitAnswers;
	}


	public void setTrmLimitAnswers(int trmLimitAnswers) {
		this.trmLimitAnswers = trmLimitAnswers;
	}


	public String getEvaluationOf() {
		return evaluationOf;
	}


	public void setEvaluationOf(String evaluationOf) {
		this.evaluationOf = evaluationOf;
	}


	public String getAntonymsFor() {
		return antonymsFor;
	}


	public void setAntonymsFor(String antonymsFor) {
		this.antonymsFor = antonymsFor;
	}


	public String getAntonymsFilter() {
		return antonymsFilter;
	}


	public void setAntonymsFilter(String antonymsFilter) {
		this.antonymsFilter = antonymsFilter;
	}


	public double getThreadRound1TitleAsymmetricSimWeight() {
		return threadRound1TitleAsymmetricSimWeight;
	}


	public void setThreadRound1TitleAsymmetricSimWeight(double threadRound1TitleAsymmetricSimWeight) {
		this.threadRound1TitleAsymmetricSimWeight = threadRound1TitleAsymmetricSimWeight;
	}


	public double getThreadRound1BodyAsymmetricSimWeight() {
		return threadRound1BodyAsymmetricSimWeight;
	}


	public void setThreadRound1BodyAsymmetricSimWeight(double threadRound1BodyAsymmetricSimWeight) {
		this.threadRound1BodyAsymmetricSimWeight = threadRound1BodyAsymmetricSimWeight;
	}


	public double getThreadRound1TfIdfCosineSimScoreWeight() {
		return threadRound1TfIdfCosineSimScoreWeight;
	}


	public void setThreadRound1TfIdfCosineSimScoreWeight(double threadRound1TfIdfCosineSimScoreWeight) {
		this.threadRound1TfIdfCosineSimScoreWeight = threadRound1TfIdfCosineSimScoreWeight;
	}


	public double getThreadRound1BM25ScoreWeight() {
		return threadRound1BM25ScoreWeight;
	}


	public void setThreadRound1BM25ScoreWeight(double threadRound1BM25ScoreWeight) {
		this.threadRound1BM25ScoreWeight = threadRound1BM25ScoreWeight;
	}


	public double getThreadRound1QueryTitleCoverageScoreWeight() {
		return threadRound1QueryTitleCoverageScoreWeight;
	}


	public void setThreadRound1QueryTitleCoverageScoreWeight(double threadRound1QueryTitleCoverageScoreWeight) {
		this.threadRound1QueryTitleCoverageScoreWeight = threadRound1QueryTitleCoverageScoreWeight;
	}


	public double getThreadRound1QueryCodeCoverageScoreWeight() {
		return threadRound1QueryCodeCoverageScoreWeight;
	}


	public void setThreadRound1QueryCodeCoverageScoreWeight(double threadRound1QueryCodeCoverageScoreWeight) {
		this.threadRound1QueryCodeCoverageScoreWeight = threadRound1QueryCodeCoverageScoreWeight;
	}


	public double getThreadRound1NGrams2ScoreWeight() {
		return threadRound1NGrams2ScoreWeight;
	}


	public void setThreadRound1NGrams2ScoreWeight(double threadRound1NGrams2ScoreWeight) {
		this.threadRound1NGrams2ScoreWeight = threadRound1NGrams2ScoreWeight;
	}


	public double getThreadRound1NGrams3ScoreWeight() {
		return threadRound1NGrams3ScoreWeight;
	}


	public void setThreadRound1NGrams3ScoreWeight(double threadRound1NGrams3ScoreWeight) {
		this.threadRound1NGrams3ScoreWeight = threadRound1NGrams3ScoreWeight;
	}


	public double getThreadRound1NGrams4ScoreWeight() {
		return threadRound1NGrams4ScoreWeight;
	}


	public void setThreadRound1NGrams4ScoreWeight(double threadRound1NGrams4ScoreWeight) {
		this.threadRound1NGrams4ScoreWeight = threadRound1NGrams4ScoreWeight;
	}


	public double getThreadRound2TitleAsymmetricSimWeight() {
		return threadRound2TitleAsymmetricSimWeight;
	}


	public void setThreadRound2TitleAsymmetricSimWeight(double threadRound2TitleAsymmetricSimWeight) {
		this.threadRound2TitleAsymmetricSimWeight = threadRound2TitleAsymmetricSimWeight;
	}


	public double getThreadRound2BodyAsymmetricSimWeight() {
		return threadRound2BodyAsymmetricSimWeight;
	}


	public void setThreadRound2BodyAsymmetricSimWeight(double threadRound2BodyAsymmetricSimWeight) {
		this.threadRound2BodyAsymmetricSimWeight = threadRound2BodyAsymmetricSimWeight;
	}


	public double getThreadRound2TfIdfCosineSimScoreWeight() {
		return threadRound2TfIdfCosineSimScoreWeight;
	}


	public void setThreadRound2TfIdfCosineSimScoreWeight(double threadRound2TfIdfCosineSimScoreWeight) {
		this.threadRound2TfIdfCosineSimScoreWeight = threadRound2TfIdfCosineSimScoreWeight;
	}


	public double getThreadRound2AnswerCountScoreWeight() {
		return threadRound2AnswerCountScoreWeight;
	}


	public void setThreadRound2AnswerCountScoreWeight(double threadRound2AnswerCountScoreWeight) {
		this.threadRound2AnswerCountScoreWeight = threadRound2AnswerCountScoreWeight;
	}


	public double getThreadRound2UpVotesScoreScoreWeight() {
		return threadRound2UpVotesScoreScoreWeight;
	}


	public void setThreadRound2UpVotesScoreScoreWeight(double threadRound2UpVotesScoreScoreWeight) {
		this.threadRound2UpVotesScoreScoreWeight = threadRound2UpVotesScoreScoreWeight;
	}


	public double getThreadRound2TotalUpvotesScoreWeight() {
		return threadRound2TotalUpvotesScoreWeight;
	}


	public void setThreadRound2TotalUpvotesScoreWeight(double threadRound2TotalUpvotesScoreWeight) {
		this.threadRound2TotalUpvotesScoreWeight = threadRound2TotalUpvotesScoreWeight;
	}


	public double getThreadRound2BM25ScoreWeight() {
		return threadRound2BM25ScoreWeight;
	}


	public void setThreadRound2BM25ScoreWeight(double threadRound2BM25ScoreWeight) {
		this.threadRound2BM25ScoreWeight = threadRound2BM25ScoreWeight;
	}


	public double getThreadRound2QueryTitleCoverageScoreWeight() {
		return threadRound2QueryTitleCoverageScoreWeight;
	}


	public void setThreadRound2QueryTitleCoverageScoreWeight(double threadRound2QueryTitleCoverageScoreWeight) {
		this.threadRound2QueryTitleCoverageScoreWeight = threadRound2QueryTitleCoverageScoreWeight;
	}


	public double getThreadRound2QueryCodeCoverageScoreWeight() {
		return threadRound2QueryCodeCoverageScoreWeight;
	}


	public void setThreadRound2QueryCodeCoverageScoreWeight(double threadRound2QueryCodeCoverageScoreWeight) {
		this.threadRound2QueryCodeCoverageScoreWeight = threadRound2QueryCodeCoverageScoreWeight;
	}


	public double getThreadRound2NGrams2ScoreWeight() {
		return threadRound2NGrams2ScoreWeight;
	}


	public void setThreadRound2NGrams2ScoreWeight(double threadRound2NGrams2ScoreWeight) {
		this.threadRound2NGrams2ScoreWeight = threadRound2NGrams2ScoreWeight;
	}


	public double getThreadRound2NGrams3ScoreWeight() {
		return threadRound2NGrams3ScoreWeight;
	}


	public void setThreadRound2NGrams3ScoreWeight(double threadRound2NGrams3ScoreWeight) {
		this.threadRound2NGrams3ScoreWeight = threadRound2NGrams3ScoreWeight;
	}


	public double getThreadRound2NGrams4ScoreWeight() {
		return threadRound2NGrams4ScoreWeight;
	}


	public void setThreadRound2NGrams4ScoreWeight(double threadRound2NGrams4ScoreWeight) {
		this.threadRound2NGrams4ScoreWeight = threadRound2NGrams4ScoreWeight;
	}


	public double getThreadFilterTopTitleAsymmetricSim() {
		return threadFilterTopTitleAsymmetricSim;
	}


	public void setThreadFilterTopTitleAsymmetricSim(double threadFilterTopTitleAsymmetricSim) {
		this.threadFilterTopTitleAsymmetricSim = threadFilterTopTitleAsymmetricSim;
	}


	public double getThreadFilterTopBodyAsymmetricSim() {
		return threadFilterTopBodyAsymmetricSim;
	}


	public void setThreadFilterTopBodyAsymmetricSim(double threadFilterTopBodyAsymmetricSim) {
		this.threadFilterTopBodyAsymmetricSim = threadFilterTopBodyAsymmetricSim;
	}


	public double getThreadFilterTopBM25Sim() {
		return threadFilterTopBM25Sim;
	}


	public void setThreadFilterTopBM25Sim(double threadFilterTopBM25Sim) {
		this.threadFilterTopBM25Sim = threadFilterTopBM25Sim;
	}


	public double getThreadFilterTopTfIdfSim() {
		return threadFilterTopTfIdfSim;
	}


	public void setThreadFilterTopTfIdfSim(double threadFilterTopTfIdfSim) {
		this.threadFilterTopTfIdfSim = threadFilterTopTfIdfSim;
	}


	public double getThreadFilterTopThreadTotalUpScoreNorm() {
		return threadFilterTopThreadTotalUpScoreNorm;
	}


	public void setThreadFilterTopThreadTotalUpScoreNorm(double threadFilterTopThreadTotalUpScoreNorm) {
		this.threadFilterTopThreadTotalUpScoreNorm = threadFilterTopThreadTotalUpScoreNorm;
	}


	public double getThreadFilterTopThreadAnswerCountNorm() {
		return threadFilterTopThreadAnswerCountNorm;
	}


	public void setThreadFilterTopThreadAnswerCountNorm(double threadFilterTopThreadAnswerCountNorm) {
		this.threadFilterTopThreadAnswerCountNorm = threadFilterTopThreadAnswerCountNorm;
	}


	public double getThreadFilterTopThreadUpVotes() {
		return threadFilterTopThreadUpVotes;
	}


	public void setThreadFilterTopThreadUpVotes(double threadFilterTopThreadUpVotes) {
		this.threadFilterTopThreadUpVotes = threadFilterTopThreadUpVotes;
	}


	public double getAnswersThreadScoreWeight() {
		return answersThreadScoreWeight;
	}


	public void setAnswersThreadScoreWeight(double answersThreadScoreWeight) {
		this.answersThreadScoreWeight = answersThreadScoreWeight;
	}


	public double getAnswersAsymmScoreWeight() {
		return answersAsymmScoreWeight;
	}


	public void setAnswersAsymmScoreWeight(double answersAsymmScoreWeight) {
		this.answersAsymmScoreWeight = answersAsymmScoreWeight;
	}


	public double getAnswersTfIdfScoreWeight() {
		return answersTfIdfScoreWeight;
	}


	public void setAnswersTfIdfScoreWeight(double answersTfIdfScoreWeight) {
		this.answersTfIdfScoreWeight = answersTfIdfScoreWeight;
	}


	public double getAnswersMethodFreqScoreWeight() {
		return answersMethodFreqScoreWeight;
	}


	public void setAnswersMethodFreqScoreWeight(double answersMethodFreqScoreWeight) {
		this.answersMethodFreqScoreWeight = answersMethodFreqScoreWeight;
	}


	public double getAnswersBM25ScoreWeight() {
		return answersBM25ScoreWeight;
	}


	public void setAnswersBM25ScoreWeight(double answersBM25ScoreWeight) {
		this.answersBM25ScoreWeight = answersBM25ScoreWeight;
	}


	public double getAnswersJaccardScoreWeight() {
		return answersJaccardScoreWeight;
	}


	public void setAnswersJaccardScoreWeight(double answersJaccardScoreWeight) {
		this.answersJaccardScoreWeight = answersJaccardScoreWeight;
	}


	public double getAnswersQueryCoverageScoreWeight() {
		return answersQueryCoverageScoreWeight;
	}


	public void setAnswersQueryCoverageScoreWeight(double answersQueryCoverageScoreWeight) {
		this.answersQueryCoverageScoreWeight = answersQueryCoverageScoreWeight;
	}


	public double getAnswersCodeCoverageScoreWeight() {
		return answersCodeCoverageScoreWeight;
	}


	public void setAnswersCodeCoverageScoreWeight(double answersCodeCoverageScoreWeight) {
		this.answersCodeCoverageScoreWeight = answersCodeCoverageScoreWeight;
	}


	public SearcherParams getSearchParam() {
		return searchParam;
	}


	public void setSearchParam(SearcherParams searchParam) {
		this.searchParam = searchParam;
	}


	

	public boolean isUseThreadFilterQueryCoverageScoreMinThreshold() {
		return useThreadFilterQueryCoverageScoreMinThreshold;
	}


	public void setUseThreadFilterQueryCoverageScoreMinThreshold(boolean useThreadFilterQueryCoverageScoreMinThreshold) {
		this.useThreadFilterQueryCoverageScoreMinThreshold = useThreadFilterQueryCoverageScoreMinThreshold;
	}


	public double getThreadFilterQueryCoverageScoreMinThresholdValue() {
		return threadFilterQueryCoverageScoreMinThresholdValue;
	}


	public void setThreadFilterQueryCoverageScoreMinThresholdValue(double threadFilterQueryCoverageScoreMinThresholdValue) {
		this.threadFilterQueryCoverageScoreMinThresholdValue = threadFilterQueryCoverageScoreMinThresholdValue;
	}


	public String getWeightingScheme() {
		return weightingScheme;
	}


	public void setWeightingScheme(String weightingScheme) {
		this.weightingScheme = weightingScheme;
	}


	public double getThreadRound1JaccardScoreWeight() {
		return threadRound1JaccardScoreWeight;
	}


	public void setThreadRound1JaccardScoreWeight(double threadRound1JaccardScoreWeight) {
		this.threadRound1JaccardScoreWeight = threadRound1JaccardScoreWeight;
	}





	public double getThreadRound2JaccardScoreWeight() {
		return threadRound2JaccardScoreWeight;
	}





	public void setThreadRound2JaccardScoreWeight(double threadRound2JaccardScoreWeight) {
		this.threadRound2JaccardScoreWeight = threadRound2JaccardScoreWeight;
	}





	public Boolean getIsCrokage() {
		return isCrokage;
	}





	public void setIsCrokage(Boolean isCrokage) {
		this.isCrokage = isCrokage;
	}





	public Boolean getUseSocialFactors() {
		return useSocialFactors;
	}





	public void setUseSocialFactors(Boolean useSocialFactors) {
		this.useSocialFactors = useSocialFactors;
	}





	public double getAnswersAPIClassScoreWeight() {
		return answersAPIClassScoreWeight;
	}





	public void setAnswersAPIClassScoreWeight(double answersAPIClassScoreWeight) {
		this.answersAPIClassScoreWeight = answersAPIClassScoreWeight;
	}





	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}





	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Baseline other = (Baseline) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}





	public Integer getVectorsTypeId() {
		return vectorsTypeId;
	}





	public void setVectorsTypeId(Integer vectorsTypeId) {
		this.vectorsTypeId = vectorsTypeId;
	}





	public Boolean getIsWeightsTraining() {
		return isWeightsTraining;
	}





	public void setIsWeightsTraining(Boolean isWeightsTraining) {
		this.isWeightsTraining = isWeightsTraining;
	}





	public double getThreadRound1Sent2VecWeight() {
		return threadRound1Sent2VecWeight;
	}





	public void setThreadRound1Sent2VecWeight(double threadRound1Sent2VecWeight) {
		this.threadRound1Sent2VecWeight = threadRound1Sent2VecWeight;
	}





	public double getThreadRound2Sent2VecWeight() {
		return threadRound2Sent2VecWeight;
	}





	public void setThreadRound2Sent2VecWeight(double threadRound2Sent2VecWeight) {
		this.threadRound2Sent2VecWeight = threadRound2Sent2VecWeight;
	} 
	
	
	
	
	
}
