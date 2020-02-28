package com.ufu.crar.config;

import org.apache.lucene.search.similarities.BM25Similarity;

import com.ufu.crar.to.Tags.TagEnum;
import com.ufu.crar.util.SearcherParams;

public class CrarParameters {

	//public static Integer[] topkArr 				  = new Integer[]{10,20,25,30,50,75,100,250,500};
	//public static Integer[] topkArr 				  = new Integer[]{1,5,10,20};
	public static Integer[] topkArr 				  = new Integer[]{10};
	public static Boolean generateFeaturesFile 		  = false;
	public static String evaluationOf                 = "Answers";  //" Threads" | "Answers"
	public static Integer roundsOnlyTRM				  = 0;
	public static Integer roundsWithoutSocialFactors  = 1;
	public static Integer roundsWithSocialFactors  	  = 2;
	
	//Java
	public static int trmLimit1ThreadsJava		         	 	 = 500;
	public static int topRelevantThreadsLimit1Java      	 	 = 250;
	public static int topRelevantThreadsLimit2Java      		 = 100;
	public static int trmLimitAnswersJava			  		 	 = 150; //150
	
	public static double threadRound1TitleAsymmetricSimWeightJava   				= 0.5d; 
	public static double threadRound1QuestionBodyAnswersBodyAsymmetricSimWeightJava = 0.50d;
	public static double threadRound1Sent2VecWeightJava		 	    				= 0.50d;
	public static double threadRound1TfIdfCosineSimScoreWeightJava  				= 0.50d; 
	public static double threadRound1BM25ScoreWeightJava            				= 0.0d;
	public static double threadRound1JaccardScoreWeightJava	 	    				= 0.0d;
	
	
	
	public static double threadRound2TitleAsymmetricSimWeightJava   = 0.5d;
	public static double threadRound2BodyAsymmetricSimWeightJava    = 0.5d;
	public static double threadRound2TfIdfCosineSimScoreWeightJava  = 0.5d;
	public static double threadRound2AnswerCountScoreWeightJava     = 0.5d;
	public static double threadRound2UpVotesScoreScoreWeightJava    = 0.5d;
	public static double threadRound2TotalUpvotesScoreWeightJava	= 0.5d;
	public static double threadRound2Sent2VecWeightJava		 		= 0.5d;
	public static double threadRound2JaccardScoreWeightJava 		= 0d;	
	
	public static double answersMethodFreqScoreWeightJava    	  = 0.75d;
	public static double answersThreadScoreWeightJava    	      = 0.75d;
	public static double answersTfIdfScoreWeightJava    	      = 0.5d;
	public static double answersAsymmScoreWeightJava    	      = 1.0d;
	
	
	//Python
	public static int trmLimit1ThreadsPython 	   			  = 200;
	public static int topRelevantThreadsLimit1Python		  = 100;
	public static int topRelevantThreadsLimit2Python     	  = 50;
	public static int trmLimitAnswersPython			  		  = 60;
	
	public static double threadRound1TitleAsymmetricSimWeightPython   = 0.75d;
	public static double threadRound1JaccardScoreWeightPython 		  = 0d;
	public static double threadRound1BodyAsymmetricSimWeightPython    = 0d;
	public static double threadRound1TfIdfCosineSimScoreWeightPython  = 0d; 
	public static double threadRound1BM25ScoreWeightPython            = 0d;
	public static double threadRound1Sent2VecWeightPython		 	  = 0d;
	
	public static double threadRound2TitleAsymmetricSimWeightPython   = 0.75d;
	public static double threadRound2JaccardScoreWeightPython 		  = 1.0d;
	public static double threadRound2BodyAsymmetricSimWeightPython    = 0.0d;
	public static double threadRound2TfIdfCosineSimScoreWeightPython  = 0.0d;
	public static double threadRound2AnswerCountScoreWeightPython     = 0.5d;
	public static double threadRound2UpVotesScoreScoreWeightPython    = 0.5d;
	public static double threadRound2TotalUpvotesScoreWeightPython	  = 0.5d;
	public static double threadRound2Sent2VecWeightPython		      = 0d;
	
	
	public static double answersMethodFreqScoreWeightPython   		= 0.25d;
	public static double answersThreadScoreWeightPython    	        = 1.0d;
	public static double answersTfIdfScoreWeightPython    	        = 0.25d;
	public static double answersAsymmScoreWeightPython 			    = 0.25d;
	
	//public static Boolean reduceThreads 			  = false;
	//public static Boolean reduceThreads 			  = true;
	//public static int numberOfRoundsInReducingThreads = 2;  //0 for no reducing (BM25 return), 1 for reducing to topRelevantThreadsLimitRound1, 2 for reducing to topRelevantThreadsLimitRound2
	
	//public static int trmLimitAnswers 			  = 100;
	//public static String evaluationOf 			  = "Threads";
	//public static String evaluationOf 				  = "Answers";
	//public static SearcherParams searchParam 		  = new SearcherParams("BM25Similarity Java", new BM25Similarity(1.2f,0.9f));
	
	
	
	//Antonyms for: NN (Nouns), VB(Verbs), All, NO (do not use this feature) 
	//https://stackoverflow.com/questions/1833252/java-stanford-nlp-part-of-speech-labels
	//public static String antonymsFor 		 		  = "All";
	//public static String antonymsFor 				  = "None";
	//public static String antonymsFor 			  	  = "VB";
	//public static String antonymsFor 			      = "NN";
	
	//Antonyms Filter: threads, answers 
	//public static String antonymsFilter 			  = "threads";
	//public static String antonymsFilter 			  = "answers";
	
	
	//public static Boolean generateFeaturesFile = true;
	
	
	//threads round1
/*	public static double threadRound1TitleAsymmetricSimWeight       = 0.5d;
	public static double threadRound1BodyAsymmetricSimWeight	   	= 0.5d;
	public static double threadRound1TfIdfCosineSimScoreWeight      = 0.5d; 
	public static double threadRound1BM25ScoreWeight                = 0.5d;
	public static double threadRound1QueryTitleCoverageScoreWeight;
	public static double threadRound1QueryCodeCoverageScoreWeight;
	
	
	public static double threadRound1NGrams2ScoreWeight;
	public static double threadRound1NGrams3ScoreWeight;
	public static double threadRound1NGrams4ScoreWeight;*/
	
	//threads round2
	/*public static double threadRound2TitleAsymmetricSimWeight       = 0.75d;
	public static double threadRound2BodyAsymmetricSimWeight        = 0.5d;
	public static double threadRound2TfIdfCosineSimScoreWeight      = 0.5d;
	public static double threadRound2AnswerCountScoreWeight         = 0.5d;
	public static double threadRound2UpVotesScoreScoreWeight        = 0.5d;
	public static double threadRound2TotalUpvotesScoreWeight		= 0.5d;
	public static double threadRound2BM25ScoreWeight;
	public static double threadRound2QueryTitleCoverageScoreWeight;
	public static double threadRound2QueryCodeCoverageScoreWeight;
	
	
	public static double threadRound2NGrams2ScoreWeight;
	public static double threadRound2NGrams3ScoreWeight;
	public static double threadRound2NGrams4ScoreWeight;*/
	
	//thread filters
	/*public static double threadFilterTopTitleAsymmetricSim;
	public static double threadFilterTopBodyAsymmetricSim;
	public static double threadFilterTopBM25Sim;
	public static double threadFilterTopTfIdfSim;
	public static double threadFilterTopThreadTotalUpScoreNorm;
	public static double threadFilterTopThreadAnswerCountNorm;
	public static double threadFilterTopThreadUpVotes;*/
	
	//answers
	/*public static double answersThreadScoreWeight           = 0.75d;
	public static double answersAsymmScoreWeight			= 1.0d;
	public static double answersTfIdfScoreWeight			= 0.5d;
	public static double answersMethodFreqScoreWeight       = 0.75d;
	public static double answersBM25ScoreWeight;
	public static double answersJaccardScoreWeight;
	public static double answersQueryCoverageScoreWeight;
	public static double answersCodeCoverageScoreWeight;*/
	
	
	
	
	/*
	 * Filters
	 */
	//filter out limit in %
	//CrokageComposer.setThreadFilterTopTitleAsymmetricSim(0.5);
	//CrokageComposer.setThreadFilterTopBodyAsymmetricSim(0.5);
	//CrokageComposer.setThreadFilterTopBM25Sim(0.5);
	//CrokageComposer.setThreadFilterTopTfIdfSim(0.5);
	
	//threads round 1 weights
	//CrokageComposer.setThreadRound1TitleAsymmetricSimWeight(0.5d);
	//CrokageComposer.setThreadRound1BodyAsymmetricSimWeight(0.5d);
	//CrokageComposer.setThreadRound1QueryTitleCoverageScoreWeight(0.5d);
	//CrokageComposer.setThreadRound1QueryCodeCoverageScoreWeight(0.5d);
	//CrokageComposer.setThreadRound1TfIdfCosineSimScoreWeight(0.5d);
	//CrokageComposer.setThreadRound1BM25ScoreWeight(0.5d);
	
	//CrokageComposer.setThreadRound1NGrams2ScoreWeight(0.25d);
	//CrokageComposer.setThreadRound1NGrams3ScoreWeight(0.5d);
	//CrokageComposer.setThreadRound1NGrams4ScoreWeight(0.75d);
	
	
	//threads round 2 weights
	//CrokageComposer.setThreadRound2TitleAsymmetricSimWeight(0.75d);
	//CrokageComposer.setThreadRound2BodyAsymmetricSimWeight(0.5d);
	//CrokageComposer.setThreadRound2QueryTitleCoverageScoreWeight(0.5d);
	//CrokageComposer.setThreadRound2QueryCodeCoverageScoreWeight(0.5d);
	//CrokageComposer.setThreadRound2TfIdfCosineSimScoreWeight(0.5d);
	//CrokageComposer.setThreadRound2AnswerCountScoreWeight(0.5d);
	//CrokageComposer.setThreadRound2UpVotesScoreScoreWeight(0.5d);
	//CrokageComposer.setThreadRound2TotalUpvotesScoreWeight(0.5d);
	
	//answers weights
	//CrokageComposer.setAnswersThreadScoreWeight(0.25d);
	//CrokageComposer.setAnswersAsymmScoreWeight(1.00d);
	//CrokageComposer.setAnswersTfIdfScoreWeight(0.50d);
	//CrokageComposer.setAnswersMethodFreqScoreWeight(0.75d);
	//CrokageComposer.setAnswersBM25ScoreWeight(0.5d);
	
	
	public static SearcherParams getSearchParamForTagId(Integer tagId) {
		SearcherParams searchParam;
		if(tagId.equals(TagEnum.Java.getId())) {
			searchParam = new SearcherParams("BM25Similarity "+TagEnum.Java.getDescricao(), new BM25Similarity(1.2f,0.9f));
		}else if(tagId.equals(TagEnum.PHP.getId())) {
			searchParam = new SearcherParams("BM25Similarity "+TagEnum.PHP.getDescricao(), new BM25Similarity(1.3f,0.6f));
		}else {
			searchParam = new SearcherParams("BM25Similarity "+TagEnum.Python.getDescricao(), new BM25Similarity(0.5f,0.9f));
		}
		return searchParam;
	}

	public static String getWeightingSchemeForTagId(Integer tagId) {
		if(tagId.equals(TagEnum.Java.getId())) {
			return "TF-IDF";
		}	
		return "TF"; //python
	}

	
	
}
