package com.ufu.crar.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.ufu.crar.config.CrarParameters;
import com.ufu.crar.to.AnswerParentPair;
import com.ufu.crar.to.Baseline;
import com.ufu.crar.to.Bucket;
import com.ufu.crar.to.ThreadContent;

@Component
public class CrokageComposer {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/*@Value("${alphaCosSim}")
	public Double alphaCosSim; 
	
	@Value("${betaCoverageScore}")
	public Double betaCoverageScore; 
	
	@Value("${gamaCodeSizeScore}")
	public Double gamaCodeSizeScore; 
	
	@Value("${deltaRepScore}")
	public Double deltaRepScore; 
	
	@Value("${epsilonUpScore}")
	public Double epsilonUpScore; 
	
	@Value("${relationType_FROM_GOOGLE_QUESTION}")
	public Double relationTypeFromGoogleQuestion; 
	
	@Value("${relationType_FROM_GOOGLE_ANSWER}")
	public Double relationTypeFromGoogleAnswer; 
	
	@Value("${relationType_RELATED_NOT_DUPE}")
	public Double relationTypeRelatedNotDupe; 
	
	@Value("${relationType_LINKS_INSIDE_TEXTS}")
	public Double relationTypeLinksInsideTexts; 
	
	@Value("${relationType_RELATED_DUPE}")
	public Double relationTypeRelatedDupe; 
	*/
	
	public static double apiWeight;
	public static double methodWeight;
	public static double tfIdfWeight;
	public static double bm25Weight;
	public static double fastTextWeight;
	public static double sent2VecWeight;
	public static double topicWeight;
	
	/*//filter
	public static double CrarParameters.threadFilterTopTitleAsymmetricSim;
	public static double CrarParameters.threadFilterTopBodyAsymmetricSim;
	public static double CrarParameters.threadFilterTopBM25Sim;
	public static double CrarParameters.threadFilterTopTfIdfSim;
	
	public static double CrarParameters.threadFilterTopThreadTotalUpScoreNorm;
	public static double threadFilterTopThreadAnswerCountNorm;
	public static double threadFilterTopThreadUpVotes;
	
	
	//threads round1
	public static double CrarParameters.threadRound1TitleAsymmetricSimWeight;
	public static double CrarParameters.threadRound1BodyAsymmetricSimWeight;
	public static double CrarParameters.threadRound1QueryTitleCoverageScoreWeight;
	public static double CrarParameters.threadRound1QueryCodeCoverageScoreWeight;
	public static double CrarParameters.threadRound1TfIdfCosineSimScoreWeight;
	public static double CrarParameters.threadRound1BM25ScoreWeight;
	
	public static double CrarParameters.threadRound1NGrams2ScoreWeight;
	public static double CrarParameters.threadRound1NGrams3ScoreWeight;
	public static double threadRound1NGrams4ScoreWeight;
	
	
	//threads round2
	public static double CrarParameters.threadRound2TitleAsymmetricSimWeight;
	public static double CrarParameters.threadRound2BodyAsymmetricSimWeight;
	public static double CrarParameters.threadRound2QueryTitleCoverageScoreWeight;
	public static double CrarParameters.threadRound2QueryCodeCoverageScoreWeight;
	public static double CrarParameters.threadRound2TfIdfCosineSimScoreWeight;
	public static double CrarParameters.threadRound2AnswerCountScoreWeight;
	public static double CrarParameters.threadRound2UpVotesScoreScoreWeight;
	public static double CrarParameters.threadRound2TotalUpvotesScoreWeight;
	public static double CrarParameters.threadRound2BM25ScoreWeight;
	
	public static double threadRound2NGrams2ScoreWeight;
	public static double threadRound2NGrams3ScoreWeight;
	public static double threadRound2NGrams4ScoreWeight;
	
	//answers
	public static double CrarParameters.answersThreadScoreWeight;
	public static double CrarParameters.answersAsymmScoreWeight;
	public static double CrarParameters.answersTfIdfScoreWeight;
	public static double CrarParameters.answersMethodFreqScoreWeight;
	public static double CrarParameters.answersBM25ScoreWeight;
	public static double CrarParameters.answersJaccardScoreWeight;
	public static double CrarParameters.answersQueryCoverageScoreWeight;
	public static double CrarParameters.answersCodeCoverageScoreWeight;*/
	
	
	//public static double upWeight;
	//public static double repWeight;
	
	
	/*
	public void rankList(List<BucketOld> bucketsList) {
		
		logger.info("Ranking with weights: "+
				"\n alphaCosSim = "+alphaCosSim +
				"\n betaCoverageScore = "+betaCoverageScore +
				"\n gamaCodeSizeScore = "+gamaCodeSizeScore +
				"\n deltaRepScore = "+deltaRepScore +
				"\n epsilonUpScore = "+epsilonUpScore+
				//relation weights
				" \n and ajuster weights of: "+
				"\n relationTypeFromGoogleQuestion = "+relationTypeFromGoogleQuestion +
				"\n relationTypeFromGoogleAnswer = "+relationTypeFromGoogleAnswer +
				"\n relationTypeRelatedNotDupe = "+relationTypeRelatedNotDupe +
				"\n relationTypeLinksInsideTexts = "+relationTypeLinksInsideTexts +
				"\n relationTypeRelatedDupe = "+relationTypeRelatedDupe 
				);
		
		//int count = 0;
		
		for(BucketOld bucketOld: bucketsList){
			double factorsScore =  alphaCosSim 	  * bucketOld.getCosSim() 
					              + betaCoverageScore * bucketOld.getCoverageScore() 
					              + gamaCodeSizeScore * bucketOld.getCodeSizeScore() 
					              + deltaRepScore 	  * bucketOld.getRepScore()
					              + epsilonUpScore    * bucketOld.getUpScore(); 
			double adjuster;
			
			if(bucketOld.getRelationTypeId().equals(RelationTypeEnum.FROM_GOOGLE_QUESTION_T1.getId())) {
				adjuster = relationTypeFromGoogleQuestion;
			}else if(bucketOld.getRelationTypeId().equals(RelationTypeEnum.FROM_GOOGLE_ANSWER_T4.getId())) {
				adjuster = relationTypeFromGoogleAnswer;
			}else if(bucketOld.getRelationTypeId().equals(RelationTypeEnum.RELATED_DUPE_T2.getId())) {
				adjuster = relationTypeRelatedDupe;
			}else if(bucketOld.getRelationTypeId().equals(RelationTypeEnum.RELATED_NOT_DUPE_T3.getId())) {
				adjuster = relationTypeRelatedNotDupe;
			}else {
				adjuster = relationTypeLinksInsideTexts;
			}
			double composedScore = adjuster*factorsScore;
			
			
			if(count<50) {
				logger.info("Composed score = "+composedScore+ " - adjuster: "+adjuster+ " - factorsScore: "+factorsScore);
			}
			count++;
			bucketOld.setComposedScore(CrarUtils.round(composedScore,5));
		}
		
		
		Collections.sort(bucketsList, new Comparator<BucketOld>() {
		    public int compare(BucketOld o1, BucketOld o2) {
		        return o2.getComposedScore().compareTo(o1.getComposedScore());
		    }
		});

	}
*/

	/*public void calculateScores(Double avgReputation, Double avgScore,LinkedHashMap<String, Double> tfIdfMainBucket, LinkedHashMap<String, Double> tfIdfOtherBucket, BucketOld mainBucket, BucketOld postBucket) {
		double cosine = cosineSimilarity(tfIdfMainBucket, tfIdfOtherBucket);
		postBucket.setCosSim(CrarUtils.round(cosine,4));
		
		double coverageScore = calculateCoverageScore(mainBucket.getClassesNames(),postBucket.getClassesNames());
		postBucket.setCoverageScore(CrarUtils.round(coverageScore,4));
		
		double codeScore = calculateCodeSizeScore(postBucket.getCodes());
		postBucket.setCodeSizeScore(CrarUtils.round(codeScore,4));
		
		double repScore = calculateRepScore(postBucket.getUserReputation());
		postBucket.setRepScore(CrarUtils.round(repScore,4));
		
		double upScore = calculateUpScore(postBucket.getPostScore());
		postBucket.setUpScore(CrarUtils.round(upScore,4));
		
		if(postBucket.getPostId().equals(1323480)) {
			System.out.println("here");
		}
	}
	*/
	
	/*private double calculateUpScoreOld(Double avgScore, Integer postScore) {
		if(postScore==null) {
			return 0d;
		}
		double diff = avgScore-postScore;
		double repScore = 1/(1+ (Math.pow(Math.E,(diff))));
		
		return repScore;
	}


	
	private double calculateRepScoreOld(Double avgReputation,Integer userReputation) {
		if(userReputation==null) {
			return 0d;
		}
		double diff = avgReputation-userReputation;
		double repScore = 1/(1+ (Math.pow(Math.E,(diff))));
		
		return repScore;
	}
	
	*/
	
	
	public static double calculateUpScore(Integer upVotes) {
		Integer range1  = 1;
		Integer range2  = 5;
		Integer range3  = 10;
		Integer range4  = 25;
		Integer range5  = 50;
		Integer range6  = 75;
		Integer range7  = 100;
		Integer range8  = 200;
		Integer range9  = 500;
				
		if(upVotes==null) {
			return 0d;
		}
		double upScore = 0d;
		
		if(upVotes<=range1) {
			upScore = 0.1d;
		}else if(upVotes > range1 && upVotes <= range2) {
			upScore = 0.2d;
		}else if(upVotes > range2 && upVotes <= range3) {
			upScore = 0.3d;
		}else if(upVotes > range3 && upVotes <= range4) {
			upScore = 0.4d;		
		}else if(upVotes > range4 && upVotes <= range5) {
			upScore = 0.5d;
		}else if(upVotes > range5 && upVotes <= range6) {
			upScore = 0.6d;
		}else if(upVotes > range6 && upVotes <= range7) {
			upScore = 0.7d;
		}else if(upVotes > range7 && upVotes <= range8) {
			upScore = 0.8d;
		}else if(upVotes > range8 && upVotes <= range9) {
			upScore = 0.9d;
		}else if(upVotes > range9) {
			upScore = 1d;
		}
		
				
		return upScore;
	}
	
	
	public static double calculateRepScore(Integer userReputation) {
		Integer range1   = 1;
		Integer range2   = 5;
		Integer range3   = 10;
		Integer range4   = 20;
		Integer range5   = 50;
		Integer range6   = 75;
		Integer range7   = 100;
		Integer range8   = 200;
		Integer range9   = 500;
		Integer range10  = 1000;
		Integer range11  = 1500;
		Integer range12  = 2000;
		Integer range13  = 3000;
		Integer range14  = 5000;
		Integer range15  = 10000;
		Integer range16  = 15000;
		Integer range17  = 20000;
		Integer range18  = 50000;
		Integer range19  = 100000;
		Integer range20  = 200000;
		
		if(userReputation==null) {
			return 0d;
		}
		double repScore = 0d;
		
		if(userReputation > range1 && userReputation <= range2) {
			repScore = 0.05d;
		}else if(userReputation > range2 && userReputation <= range3) {
			repScore = 0.10d;
		}else if(userReputation > range3 && userReputation <= range4) {
			repScore = 0.15d;		
		}else if(userReputation > range4 && userReputation <= range5) {
			repScore = 0.20d;
		}else if(userReputation > range5 && userReputation <= range6) {
			repScore = 0.25d;
		}else if(userReputation > range6 && userReputation <= range7) {
			repScore = 0.30d;
		}else if(userReputation > range7 && userReputation <= range8) {
			repScore = 0.35d;
		}else if(userReputation > range8 && userReputation <= range9) {
			repScore = 0.40d;
		}else if(userReputation > range9 && userReputation <= range10) {
			repScore = 0.45d;
		}else if(userReputation > range10 && userReputation <= range11) {
			repScore = 0.50d;
		}else if(userReputation > range11 && userReputation <= range12) {
			repScore = 0.55d;
		}else if(userReputation > range12 && userReputation <= range13) {
			repScore = 0.60d;		
		}else if(userReputation > range13 && userReputation <= range14) {
			repScore = 0.65d;
		}else if(userReputation > range14 && userReputation <= range15) {
			repScore = 0.70d;
		}else if(userReputation > range15 && userReputation <= range16) {
			repScore = 0.75d;
		}else if(userReputation > range16 && userReputation <= range17) {
			repScore = 0.80d;
		}else if(userReputation > range17 && userReputation <= range18) {
			repScore = 0.85d;
		}else if(userReputation > range18 && userReputation <= range19) {
			repScore = 0.90d;
		}else if(userReputation > range19 && userReputation <= range20) {
			repScore = 0.95d;
		}else if(userReputation > range20) {
			repScore = 1.00d;
		}
		
				
		return repScore;
	}


	public static double calculateCodeSizeScore(List<String> postCodes) {
		if(postCodes.size()==0) {
			return 0d;
		}
		List<String> cleanList = new ArrayList<>();
		String adjusted = "";
		for(String code: postCodes) {
			adjusted = code.replaceAll("(?m)^[ \t]*\r?\n", "");
			cleanList.add(adjusted);
		}
		
		double totalSize = 0d;
		double codeScore = 0d;
		double average= 0d;
		
		if(cleanList.size()>0) {
			int numberOfLines=0;
			for(String code: cleanList) {
				numberOfLines+= CrarUtils.countLines(code);
			}
			average = numberOfLines / cleanList.size();
			totalSize = average;
		}
		codeScore = 1 / Math.sqrt(totalSize);
		
		return codeScore;
	}


	public static double calculateCoverageScore(Set<String> mainBucketClassesNames, Set<String> postBucketlassesNames) {
		Set<String> intersection = new LinkedHashSet<String>(mainBucketClassesNames);
		
		float pSetSize = postBucketlassesNames.size();
		if(pSetSize==0f) {
			return 0d;
		}
		
		intersection.retainAll(postBucketlassesNames);
				
		int mSetIntersectionPsetSize = intersection.size();
		
		
		double coverageScore = mSetIntersectionPsetSize / pSetSize;
		
		return coverageScore;
	}
	
	
	private static double getDotProduct(LinkedHashMap<String, Double> query, LinkedHashMap<String, Double> weights2) {
		double product = 0;
		for (String term : query.keySet()) {
			Double secondWeight = weights2.get(term);
			if(secondWeight!=null){
				product += query.get(term) * secondWeight;
			}
			
		}
		
		return product;
	}
	
	private static double getMagnitude(LinkedHashMap<String, Double> weights) {
		double magnitude = 0;
		for (double weight : weights.values()) {
			magnitude += weight * weight;
		}
		
		return Math.sqrt(magnitude);
	}
	
	
	public static double cosineSimilarity(LinkedHashMap<String, Double> query, LinkedHashMap<String, Double> weights2) {
		if(query.isEmpty()||weights2.isEmpty()) {
			return 0d;
		}
		return getDotProduct(query, weights2) / (getMagnitude(query) * getMagnitude(weights2));
	}


	
	
	public static double calculateScoreForPresentClasses(String code, Set<String> topClasses) {
		
		double i = 0;
		for (String topClass : topClasses) {
			if (code.contains(topClass)) {
				return 1 - i;
			}
			i += 0.1;
		}

		return 0;
	}
	

	public static void calculateApiScore(AnswerParentPair answerParentPair, Set<String> topClasses, Map<Integer, Set<String>> upvotedPostsIdsWithCodeApisMap) {
		Set<String> allApis = new LinkedHashSet<>();
		ArrayList<String> topClassesArray = new ArrayList<>(topClasses);
		double score=0;
		double found = 0;
		double linePrec = 0;
		
		Set<String> parentApis = upvotedPostsIdsWithCodeApisMap.get(answerParentPair.getParentId());
		Set<String> answerApis = upvotedPostsIdsWithCodeApisMap.get(answerParentPair.getAnswerId());
		if(parentApis!=null) {
			allApis.addAll(parentApis);
		}
		if(answerApis!=null) {
			allApis.addAll(answerApis);
		}
		
		
		
		outer:for (int i = 0; i < topClassesArray.size(); i++) {
			String api = topClassesArray.get(i);
			if(allApis.contains(api)) {
				score += 1.0 / (i + 2);
				//break outer;
			}
		}
		
		/*outer:for (int i = 0; i < topClassesArray.size(); i++) {
			String api = topClassesArray.get(i);
			if(allApis.contains(api)) {
				found++;
				linePrec += (found / (i + 1));
			}
		}
		if (found == 0) {
			score=0;
		}else {
			score = linePrec / found;
		}*/
			
		answerParentPair.setApiScore(score);
		topClassesArray= null;
		allApis=null;
	}
	
	
	public static double calculateScoreForCommonMethods(Bucket bucket, Map<String, Integer> methodsCounterMap, List<String> topFrequencyMethods) {
		/*for(String topMethod:methodsCounterMap.keySet()) {
			if(code.contains(topMethod)) {
				int topMethodFrequency = methodsCounterMap.get(topMethod);
				double score = CrarUtils.log2(topMethodFrequency)/10;
				return score;
			}
		}
		return 0;*/
		
		Set<String> codes = bucket.getMethods();
		double score=0;
		for (int i = 0; i < topFrequencyMethods.size(); i++) {
			String method = topFrequencyMethods.get(i);
			if(codes.contains(method)) {
				score += 1.0 / (i + 2);
			}
		}
		
		return score;
		
	}
	
	public static double calculateScoreForCommonMethods(String code, Map<String, Integer> methodsCounterMap) {
		for(String topMethod:methodsCounterMap.keySet()) {
			if(code.contains(topMethod)) {
				int topMethodFrequency = methodsCounterMap.get(topMethod);
				double score = CrarUtils.log2(topMethodFrequency)/10;
				return score;
			}
		}
		return 0;
	}
	
	public static double calculateScoreForCommonMethodsDynamic(String code, Map<String, Integer> methodsCounterMap) {
		for(String topMethod:methodsCounterMap.keySet()) {
			if(code.contains(topMethod)) {
				int methodFrequency = methodsCounterMap.get(topMethod);
				double score = CrarUtils.log2(methodFrequency)/10;
				return score;
			}
		}
		return 0;
	}
	
	/*public static double calculateScoreForCommonMethodsDynamic(String code, Map<String, Integer> methodsCounterMap) {
		double score=0;
		for(String topMethod:methodsCounterMap.keySet()) {
			if(code.contains(topMethod)) {
				int methodFrequency = methodsCounterMap.get(topMethod);
				double result = CrarUtils.log2(methodFrequency)/10; 
				score += result;
			}
		}
		return score;
	}*/


	public static double calculateScoreForCommonTopics(Set<Integer> keySet, Map<Integer, Integer> topTopicsCounterMap) {
		/*for(Integer topTopic:topTopicsCounterMap.keySet()) {
			if(keySet.contains(topTopic)) {
				int topTopicFrequency = topTopicsCounterMap.get(topTopic);
				double score = CrarUtils.log2(topTopicFrequency)/10;
				return score;
			}
		}
		return 0;*/
		
		List<Integer> topics = new ArrayList(topTopicsCounterMap.keySet());
		
		double score=0;
		for (int i = 0; i < topics.size(); i++) {
			Integer topic = topics.get(i);
			if(keySet.contains(topic)) {
				score += 1.0 / (i + 2);
			}
		}
		topics=null;
		return score;
		
	}

	
	public static double calculateQuestionRankingScore(Bucket bucket, double simPair, double tfIdfCosineSimScore, double bm25Score) {
		double finalScore;
			
		finalScore = simPair*fastTextWeight + tfIdfCosineSimScore*tfIdfWeight + bm25Score*bm25Weight;
		
		return finalScore;
		
	}
	
	
	public static double calculateThreadsRankingScoreRound1(ThreadContent candidateThread, Baseline baseline) {
		double finalScore = 
				(baseline.getThreadRound1TitleAsymmetricSimWeight()		  * candidateThread.getThreadTitleAsymmetricSimNormalized())+
		        (baseline.getThreadRound1BodyAsymmetricSimWeight() 	 	  * candidateThread.getThreadQuestionBodyAnswersBodyAsymmetricSimNormalized())+
		        (baseline.getThreadRound1TfIdfCosineSimScoreWeight() 	  * candidateThread.getTfIdfCosineSimScoreNormalized())+
		        (baseline.getThreadRound1BM25ScoreWeight()				  * candidateThread.getBm25ScoreNormalized())+
		        (baseline.getThreadRound1QueryTitleCoverageScoreWeight()  * candidateThread.getQueryTitleCoverageScore())+
		        (baseline.getThreadRound1QueryCodeCoverageScoreWeight()   * candidateThread.getQueryCodeCoverageScore())+
		        (baseline.getThreadRound1NGrams2ScoreWeight()   		  * candidateThread.getTwoGramsScoreNormalized())+
		        (baseline.getThreadRound1JaccardScoreWeight()   		  * candidateThread.getJaccardScore())+
		        (baseline.getThreadRound1Sent2VecWeight()   		      * candidateThread.getSent2VecScoreNormalized())+
		        (baseline.getThreadRound1NGrams3ScoreWeight()   		  * candidateThread.getThreeGramsScoreNormalized());
		 
		
		return finalScore;
	}

/*
	public static double calculateThreadsRankingScoreRound1(double threadTitleAsymmetricSim, double threadBodyAsymmetricSim, double tfIdfCosineSimScore,double queryTitleCoverageScore,double queryCodeCoverageScore, double jaccard, double bm25Score) {

		
		double finalScore = (CrarParameters.threadRound1TitleAsymmetricSimWeight	 	* threadTitleAsymmetricSim)+
					        (CrarParameters.threadRound1BodyAsymmetricSimWeight 	 	* threadBodyAsymmetricSim)+
					        (CrarParameters.threadRound1QueryTitleCoverageScoreWeight  * queryTitleCoverageScore)+
					        (CrarParameters.threadRound1QueryCodeCoverageScoreWeight   * queryCodeCoverageScore)+
					        (CrarParameters.threadRound1TfIdfCosineSimScoreWeight 		* tfIdfCosineSimScore)+
					        (CrarParameters.threadRound1BM25ScoreWeight				* bm25Score);
		
		
		return finalScore;
	}*/

	

	public static double calculateThreadsRankingScoreRound2(ThreadContent candidateThread, Baseline baseline) {
		
		double finalScore = 
				 (baseline.getThreadRound2TitleAsymmetricSimWeight()        * candidateThread.getThreadTitleAsymmetricSimNormalized())+
				 (baseline.getThreadRound2JaccardScoreWeight()        		* candidateThread.getJaccardScore())+
				 (baseline.getThreadRound2BodyAsymmetricSimWeight() 		* candidateThread.getThreadQuestionBodyAnswersBodyAsymmetricSimNormalized())+
				 (baseline.getThreadRound2TfIdfCosineSimScoreWeight() 		* candidateThread.getTfIdfCosineSimScoreNormalized())+
				 (baseline.getThreadRound2BM25ScoreWeight() 				* candidateThread.getBm25ScoreNormalized())+
				 (baseline.getThreadRound2QueryTitleCoverageScoreWeight() 	* candidateThread.getQueryTitleCoverageScore())+
				 (baseline.getThreadRound2QueryCodeCoverageScoreWeight()	* candidateThread.getQueryCodeCoverageScore())+
				 (baseline.getThreadRound2AnswerCountScoreWeight() 			* candidateThread.getThreadAnswerCountScoreNormalized())+
				 (baseline.getThreadRound2UpVotesScoreScoreWeight() 		* candidateThread.getThreadUpVotesScore())+
				 (baseline.getThreadRound2Sent2VecWeight()   		        * candidateThread.getSent2VecScoreNormalized())+
				 (baseline.getThreadRound2TotalUpvotesScoreWeight()  		* candidateThread.getThreadTotalUpVotesScoreNormalized());
		return finalScore;
	}
	
	/*public static double calculateThreadsRankingScoreRound2(double threadTitleAsymmetricSim,double threadBodyAsymmetricSim, double tfIdfCosineSimScore,
			double threadAnswerCountScore, double threadUpVotesScore,double threadTotalUpvotes,double queryTitleCoverageScore, double queryCodeCoverage, double jaccard, double bm25Score) {
		
	
		
		
		double finalScore = (CrarParameters.threadRound2TitleAsymmetricSimWeight       * threadTitleAsymmetricSim)+
							(CrarParameters.threadRound2QueryTitleCoverageScoreWeight 	* queryTitleCoverageScore)+
							(CrarParameters.threadRound2QueryCodeCoverageScoreWeight 	* queryCodeCoverage)+
							(CrarParameters.threadRound2TfIdfCosineSimScoreWeight 		* tfIdfCosineSimScore)+
							(CrarParameters.threadRound2AnswerCountScoreWeight 		* threadAnswerCountScore)+
							(CrarParameters.threadRound2UpVotesScoreScoreWeight 		* threadUpVotesScore)+
							(CrarParameters.threadRound2BM25ScoreWeight 				* bm25Score)+
							(CrarParameters.threadRound2TotalUpvotesScoreWeight  		* threadTotalUpvotes);
					
		return finalScore;
	}*/
	
	
	public static double calculateAnswersDynamicRankingScore(Bucket candidateBucket, Baseline baseline) {
		double finalScore=0d;
		try {
		
		finalScore = (baseline.getAnswersThreadScoreWeight()        * candidateBucket.getThreadSimNormalized())
				   + (baseline.getAnswersAsymmScoreWeight()         * candidateBucket.getSimPairNormalized())
				   + (baseline.getAnswersTfIdfScoreWeight()         * candidateBucket.getTfIdfCosineSimScoreNormalized())
				   + (baseline.getAnswersBM25ScoreWeight()          * candidateBucket.getBm25ScoreNormalized())
				   + (baseline.getAnswersJaccardScoreWeight()       * candidateBucket.getJaccardScore())
				   + (baseline.getAnswersQueryCoverageScoreWeight() * candidateBucket.getQueryCoverageScore())
				   + (baseline.getAnswersCodeCoverageScoreWeight()  * candidateBucket.getCodeCoverageScore())
				   + (baseline.getAnswersMethodFreqScoreWeight()    * candidateBucket.getMethodScore())
				   + (baseline.getAnswersAPIClassScoreWeight()      * candidateBucket.getApiScoreNormalized());
		
		} catch (Exception e) {
			System.out.println("error in final score: "+candidateBucket);
		}
		
		
		/*if(candidateBucket.getApiScoreNormalized()>0) { 
			System.out.println("here"); 
			}*/
		 
		return finalScore;
	}

	
	/*
	public static double calculateAnswersDynamicRankingScore(double threadScore, double asymmScore, double tfIdfScore, double methodFreqScore) {
		double finalScore;
		double a;
		double b;
		
		//double lexicalSim = (bm25Score + tfIdfCosineSimScore)/2;
		
		//a = bm25Score * tfIdfCosineSimScore;
		//a = tfIdfCosineSimScore;
		//b = (1-bm25Score);
		
		//finalScore = (0.5*lexicalSim) + (asymmScore) + (0.75*methodFreqScore);
		
		finalScore = (CrarParameters.answersThreadScoreWeight     * threadScore)
				   + (CrarParameters.answersAsymmScoreWeight      * asymmScore)
				   + (CrarParameters.answersTfIdfScoreWeight      * tfIdfScore)
				   + (CrarParameters.answersMethodFreqScoreWeight * methodFreqScore);
		
//		finalScore = (1.0     * threadScore)
//				   + (0.5      * asymmScore)
//				   + (0.5      * tfIdfScore)
//				   + (0 	* methodFreqScore);
		
				//+ (0.5*codeCoverage)
				//+ (0.5*methodFreqScore);
		
		//finalScore = a + (b * simPair) + methodFreqScore; 
		
		return finalScore;
		
	}*/
	
	
	
	/*
	public static double calculateRankingScore(double simPair, double methodFreqScore, double apiAnswerPairScore,double tfIdfCosineSimScore, double bm25Score) {
		double finalScore;
		
		//double methodFreqScore = calculateScoreForCommonMethods(bucket.getCode(),methodsCounterMap);
		
		double repScore = calculateRepScore(bucket.getUserReputation());
	
		double upScore = calculateUpScore(bucket.getUpVotesScore());
		
		//finalScore = simPair*fastTextWeight + apiAnswerPairScore*apiWeight + repScore*repWeight + upScore*upWeight + methodFreqScore*methodWeight + tfIdfCosineSimScore*tfIdfWeight;
		
		finalScore = simPair*fastTextWeight + apiAnswerPairScore*apiWeight + methodFreqScore*methodWeight + tfIdfCosineSimScore*tfIdfWeight + bm25Score*bm25Weight;
		
		return finalScore;
		
	}*/
	
	



	public static double getApiWeight() {
		return apiWeight;
	}


	public static void setApiWeight(double classFreqWeight) {
		CrokageComposer.apiWeight = classFreqWeight;
	}


	public static double getMethodFreqWeight() {
		return methodWeight;
	}


	public static void setMethodFreqWeight(double methodFreqWeight) {
		CrokageComposer.methodWeight = methodFreqWeight;
	}


	


	public static double getSent2VecWeight() {
		return sent2VecWeight;
	}


	public static void setSent2VecWeight(double sent2VecWeight) {
		CrokageComposer.sent2VecWeight = sent2VecWeight;
	}


	public static double getFastTextWeight() {
		return fastTextWeight;
	}


	public static void setFastTextWeight(double simWeight) {
		CrokageComposer.fastTextWeight = simWeight;
	}




	public static double getTfIdfWeight() {
		return tfIdfWeight;
	}


	public static void setTfIdfWeight(double tfIdfWeight) {
		CrokageComposer.tfIdfWeight = tfIdfWeight;
	}


	public static double getBm25Weight() {
		return bm25Weight;
	}


	public static void setBm25Weight(double bm25Weight) {
		CrokageComposer.bm25Weight = bm25Weight;
	}


	public static double getTopicWeight() {
		return topicWeight;
	}


	public static void setTopicWeight(double topicWeight) {
		CrokageComposer.topicWeight = topicWeight;
	}


	public static double getMethodWeight() {
		return methodWeight;
	}


	public static void setMethodWeight(double methodWeight) {
		CrokageComposer.methodWeight = methodWeight;
	}


	public Logger getLogger() {
		return logger;
	}

	
	/*public static void filterBadCandidateThreads(Set<ThreadContent> candidateThreads,
			Map<String, Set<String>> queryAntonymsMap, Set<String> queryAntonyms, String rawQuery,
			Map<String, Set<Integer>> groundTruthThreadsMap, boolean processAntonyms, Baseline baseline) {
		
		
		Set<ThreadContent> antonymsRemoveList = new LinkedHashSet<>();
		Set<ThreadContent> antonymsGoldSetRemoveList = new LinkedHashSet<>();
		Set<ThreadContent> queryCoverageRemoveList = new LinkedHashSet<>();
		Set<ThreadContent> queryCoverageGoldSetRemoveList = new LinkedHashSet<>();
		Set<Integer> groundTruthThreads = groundTruthThreadsMap.get(rawQuery);
		
		if(baseline.isUseThreadFilterQueryCoverageScoreMinThreshold()) {
			//int sizeBefore= candidateThreads.size();
			
			for(ThreadContent candidateThread: candidateThreads) {
				if(candidateThread.getQueryCodeCoverageScore()<=baseline.getThreadFilterQueryCoverageScoreMinThresholdValue()) {
					if(groundTruthThreads.contains(candidateThread.getId())) {
						queryCoverageGoldSetRemoveList.add(candidateThread);
					}
					queryCoverageRemoveList.add(candidateThread);
				}
			}
			
			if(queryCoverageRemoveList.size()>0) {
				int removeListSize= queryCoverageRemoveList.size();
				int before = candidateThreads.size();
				candidateThreads.removeAll(queryCoverageRemoveList);
				int after = candidateThreads.size();
				System.out.println("MinThreshold filtered "+removeListSize+" threads for query: "+rawQuery
						+ " -Before: "+before+" -after:"+after+ " - of which "+queryCoverageGoldSetRemoveList.size()+" were goldset");
				
			}
			
		}
		
		
		if(processAntonyms && (baseline.getAntonymsFilter().equals("threads") || baseline.getAntonymsFilter().equals("threads&answers"))) {
			//System.out.println("\n\n\nFiltering threads for query: "+rawQuery +" - antonyms: "+queryAntonyms);
			for(ThreadContent candidateThread: candidateThreads) {
				//String content = candidateThread.getTitle()+" "+candidateThread.getQuestionBody();
				//String processedCandidadeThreadTitle = CrarUtils.processQuery(candidateThread.getTitle(),false);
				String processedCandidadeThreadContent = CrarUtils.processQuery(candidateThread.getTitle()+" "+candidateThread.getQuestionBody(),false);
				
				Set<String> candidadeThreadTitleWords = Arrays.stream(processedCandidadeThreadContent.split("\\s+")).collect(Collectors.toCollection(LinkedHashSet::new));
				//for(String titleWord)
				
				SetView<String> intersection = Sets.intersection(candidadeThreadTitleWords, queryAntonyms); 
				int intersectionNumber = intersection.size();
				if(intersectionNumber>0) {
					
					if(groundTruthThreads.contains(candidateThread.getId())) {
						//System.out.println("$$$$$ Filtering gold set thread: "+candidateThread.getId()+ " - candidate:"+ candidateThread.getTitle()+" - inter:"+intersectionNumber+ ": "+intersection);
						antonymsGoldSetRemoveList.add(candidateThread);
					}
					antonymsRemoveList.add(candidateThread);
				}
				
			}
			
			if(antonymsRemoveList.size()>0) {
				int removeListSize= antonymsRemoveList.size();
				int before = candidateThreads.size();
				candidateThreads.removeAll(antonymsRemoveList);
				int after = candidateThreads.size();
				System.out.println("Filtered "+removeListSize+ " -Before: "+before+" -after:"+after+" - "+antonymsGoldSetRemoveList.size()+" were goldset - query: "+rawQuery);
			}
		}
		
		antonymsRemoveList=null;
		antonymsGoldSetRemoveList=null;
		queryCoverageRemoveList=null;
		queryCoverageGoldSetRemoveList=null;
		//TODO remove non-how to threads, ex: 39033525
		//TODO tratar mais de um verbo na query: https://stackoverflow.com/questions/28246317

		
	}
	*/
	
	public static void filterThreadsWithAntonyms(Set<ThreadContent> candidateThreads,
			Map<String, Set<String>> queryAntonymsMap, Set<String> queryAntonyms, String rawQuery,
			Map<String, Set<Integer>> groundTruthThreadsMap, boolean processAntonyms, Baseline baseline) {
		Set<ThreadContent> removeList = new LinkedHashSet<>();
		Set<ThreadContent> goldSetRemoveList = new LinkedHashSet<>();
		Set<Integer> groundTruthThreads = groundTruthThreadsMap.get(rawQuery);
		
		if(processAntonyms && (baseline.getAntonymsFilter().equals("threads") || baseline.getAntonymsFilter().equals("threads&answers"))) {
			//System.out.println("\n\n\nFiltering threads for query: "+rawQuery +" - antonyms: "+queryAntonyms);
			for(ThreadContent candidateThread: candidateThreads) {
				//String content = candidateThread.getTitle()+" "+candidateThread.getQuestionBody();
				//String processedCandidadeThreadTitle = CrarUtils.processQuery(candidateThread.getTitle(),false);
				String processedCandidadeThreadContent = CrarUtils.processQuery(candidateThread.getTitle()+" "+candidateThread.getQuestionBody(),false);
				
				Set<String> candidadeThreadWords = Arrays.stream(processedCandidadeThreadContent.split("\\s+")).collect(Collectors.toCollection(LinkedHashSet::new));
				//for(String titleWord)
				
				SetView<String> intersection = Sets.intersection(candidadeThreadWords, queryAntonyms); 
				int intersectionNumber = intersection.size();
				if(intersectionNumber>0) {
					
					if(groundTruthThreads.contains(candidateThread.getId())) {
						//System.out.println("$$$$$ Filtering gold set thread: "+candidateThread.getId()+ " - candidate:"+ candidateThread.getTitle()+" - inter:"+intersectionNumber+ ": "+intersection);
						goldSetRemoveList.add(candidateThread);
					}
					removeList.add(candidateThread);
				}
				
			}
			
			if(removeList.size()>0) {
				int removeListSize= removeList.size();
				//int before = candidateThreads.size();
				candidateThreads.removeAll(removeList);
				//int after = candidateThreads.size();
				//System.out.println("Filtered threads: "+removeListSize+ " -Before: "+before+" -after:"+after+" - "+goldSetRemoveList.size()+" were goldset - query: "+rawQuery);
				//System.out.println("Filtered threads: "+removeListSize+" - goldset: "+goldSetRemoveList.size()+" for query: "+rawQuery);
			}
		}
		goldSetRemoveList=null;
		removeList=null;
	
	}
	
	
	public static void filterBadCandidateThreadsWithMinFeatures(Set<ThreadContent> candidateThreads, ThreadContent maxFeaturesThread,
			Map<String, Set<String>> queryAntonymsMap, Set<String> queryAntonyms, String rawQuery,
			Map<String, Set<Integer>> groundTruthThreadsMap, boolean processAntonyms, Baseline baseline) {
		Set<ThreadContent> queryCoverageRemoveList = new LinkedHashSet<>();
		Set<ThreadContent> queryCoverageGoldSetRemoveList = new LinkedHashSet<>();
		Set<Integer> groundTruthThreads = groundTruthThreadsMap.get(rawQuery);
		
		
		if(baseline.isUseThreadFilterQueryCoverageScoreMinThreshold()) {
			//int sizeBefore= candidateThreads.size();
			
			for(ThreadContent candidateThread: candidateThreads) {
				if(candidateThread.getQueryCodeCoverageScore()<=baseline.getThreadFilterQueryCoverageScoreMinThresholdValue()) {
					if(groundTruthThreads.contains(candidateThread.getId())) {
						queryCoverageGoldSetRemoveList.add(candidateThread);
					}
					queryCoverageRemoveList.add(candidateThread);
				}
			}
			
			if(queryCoverageRemoveList.size()>0) {
				int removeListSize= queryCoverageRemoveList.size();
				//int before = candidateThreads.size();
				candidateThreads.removeAll(queryCoverageRemoveList);
				//int after = candidateThreads.size();
				//System.out.println("Filtered minThreshold threads: "+removeListSize+" threads for query: "+rawQuery + " -Before: "+before+" -after:"+after+ " - of which "+queryCoverageGoldSetRemoveList.size()+" were goldset");
				
				//System.out.println("Filtered minThreshold threads: "+removeListSize+" - goldset: "+queryCoverageGoldSetRemoveList.size()+" for query: "+rawQuery);
			}
			
			//candidateThreads.removeIf(e-> e.getQueryCodeCoverageScore()<=baseline.getThreadFilterQueryCoverageScoreMinThresholdValue());
			//int sizeAfter = candidateThreads.size();
			//int diff = sizeAfter - sizeBefore;
			//System.out.println("MinThreshold, filtered "+diff+" threads for query: "+rawQuery + " -Before: "+sizeBefore+" -after:"+sizeAfter+ " - of which "+goldSetRemoveList.size()+" were goldset");
		}
		
		if(baseline.getThreadFilterTopTitleAsymmetricSim()>0) {
			double titleLimit = baseline.getThreadFilterTopTitleAsymmetricSim() * maxFeaturesThread.getThreadTitleAsymmetricSimMax();
			candidateThreads.removeIf(e-> e.getThreadTitleAsymmetricSim() < titleLimit);
		}
		if(baseline.getThreadFilterTopBodyAsymmetricSim()>0) {
			double bodyLimit = baseline.getThreadFilterTopBodyAsymmetricSim() * maxFeaturesThread.getThreadBodyAsymmetricSimMax();
			candidateThreads.removeIf(e-> e.getThreadQuestionBodyAnswersBodyAsymmetricSim() < bodyLimit);
		}
		if(baseline.getThreadFilterTopBM25Sim()>0) {
			double bm25Limit = baseline.getThreadFilterTopBM25Sim() * maxFeaturesThread.getBm25ScoreMax();
			candidateThreads.removeIf(e-> e.getBm25Score() < bm25Limit);
		}
		if(baseline.getThreadFilterTopTfIdfSim()>0) {
			double tfIdfLimit = baseline.getThreadFilterTopBM25Sim() * maxFeaturesThread.getTfIdfCosineSimScoreMax();
			candidateThreads.removeIf(e-> e.getTfIdfCosineSimScore() < tfIdfLimit);
		}
		
		if(baseline.getThreadFilterTopThreadTotalUpScoreNorm()>0) {
			double limit = baseline.getThreadFilterTopThreadTotalUpScoreNorm() * maxFeaturesThread.getThreadTotalUpvotesMax();
			candidateThreads.removeIf(e-> e.getThreadTotalUpvotes() < limit);
		}
		
		queryCoverageRemoveList=null;
		queryCoverageGoldSetRemoveList=null;
	
		
		//TODO remove non-how to threads, ex: 39033525
		//TODO tratar mais de um verbo na query: https://stackoverflow.com/questions/28246317

		
	}


	
	public static void filterAnswersWithAntonyms(String rawQuery, Map<String, Set<Integer>> groundTruthMap, 
			Set<Bucket> candidateBuckets, Map<String, Set<String>> queryAntonymsMap, Set<String> queryAntonyms, 
			boolean processAntonyms, Baseline baseline) {
		Set<Bucket> removeList = new LinkedHashSet<>();
		Set<Bucket> goldSetRemoveList = new LinkedHashSet<>();
		Set<Integer> groundTruthAnswers = groundTruthMap.get(rawQuery);
				
		if(processAntonyms && (baseline.getAntonymsFilter().equals("answers") || baseline.getAntonymsFilter().equals("threads&answers"))) {
			//System.out.println("Filtering answers for query: "+rawQuery +" - antonyms: "+queryAntonyms);
			for(Bucket answer: candidateBuckets) {
				//String processedCandidadeContent = CrarUtils.processQuery(answer.getParentProcessedTitle()+ " "+answer.getProcessedCode(),false);
				String processedCandidadeContent = CrarUtils.processQuery(answer.getParentProcessedTitle()+ " "+answer.getProcessedCode()+ " "+answer.getProcessedBody(),false);
				//String processedCandidadeContent = CrarUtils.processQuery(answer.getProcessedCode(),false);
				
				Set<String> candidadeWords = Arrays.stream(processedCandidadeContent.split("\\s+")).collect(Collectors.toCollection(LinkedHashSet::new));
				
				SetView<String> intersection = Sets.intersection(candidadeWords, queryAntonyms); 
				int intersectionNumber = intersection.size();
				if(intersectionNumber>0) {
					
					if(groundTruthAnswers.contains(answer.getId())) {
						//System.out.println("$$$$$ Filtering gold set answer: "+answer.getId()+ " - candidate:"+ answer.getParentProcessedTitle()+" -body: "+answer.getProcessedBody()+" - inter:"+intersectionNumber+ ": "+intersection);
						goldSetRemoveList.add(answer);
					}
					removeList.add(answer);
				}
				
			}
			
			if(removeList.size()>0) {
				int removeListSize= removeList.size();
				//int before = candidateBuckets.size();
				candidateBuckets.removeAll(removeList);
				//int after = candidateBuckets.size();
				//System.out.println("Filtered answers: "+removeListSize+" - goldset: "+goldSetRemoveList.size()+" for query: "+rawQuery);
				
			}
		}
		
	}


	


	

	


	
		
	
	
	
}
