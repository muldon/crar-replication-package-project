package com.ufu.crar.to;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;
import com.ufu.crar.tfidf.Document;

@XmlRootElement
public class Bucket extends GenericBucket{
	private static final long serialVersionUID = -11118191211815641L;
	@Id
	@Expose
	private Integer id;
	
	private String body;
	
	private String processedTitle;
	
	private String title;
	
	@Expose
	private String processedBody;
	
	@Expose
	private String code;
	
	@Expose
	private String processedCode;
	
	private Integer upVotesScore;
	
	private Integer userReputation;
	
	private Integer commentCount;
	
	private Integer viewCount;
	
	private Boolean acceptedAnswer;
	
	private Integer acceptedAnswerId;
	
	private String acceptedAnswerBody;
	
	@Expose
	private Integer parentId;
	
	private Integer parentUpVotesScore;
	
	private Double calculatedScore;
	
	@Expose
	private String parentProcessedTitle;
	
	@Expose
	private String parentProcessedTitleLemma;
	
	@Expose
	private String parentProcessedBody;
	
	@Expose
	private String parentProcessedBodyLemma;
	
	@Expose
	private String parentProcessedCode;
	
	@Expose
    private String processedBodyLemma;
	
    private String processedTitleLemma;
	
	private Double titleScore;
	
	private Document document;
	
	private Double tfIdfCosineSimScore;
	
	private Double tfIdfCosineSimScoreNormalized;
	
	private Double methodScore;
	
	private Double methodScoreNormalized;
	
	@Expose
	private Double bm25Score;
	
	private Double bm25ScoreNormalized;
	
	protected Double jaccardScore;
	
	private Double simPair;
	
	private Double simPairNormalized;
	
	private Double threadSim;
	
	private Double threadSimNormalized;
	
	protected Double queryCoverageScore;
	
	protected Double codeCoverageScore;
	
	private String acceptedOrMostUpvotedAnswerOfParentProcessedBody;
	
	private String acceptedOrMostUpvotedAnswerOfParentProcessedCode;
	
	private String threadContent;
	
	private Set<Bucket> answers;
	
	private String topics;
	
	private String hotTopics;
	
	private Map<Integer,Double> hotTopicsIdValueMap;
	
	private Map<Integer,Double> topicsIdValueMap;
	
	private Set<Integer> topScoredAnswersSet;
		
	private String topScoredAnswers;
	
	private Double topicScore;
	
	private Integer intersectionSize;
	
	private Integer tagId;
	
	private Set<String> methods;
	
	private Boolean containCode;
	
	
	
	private Integer answerCount;
	
	private Integer score;
	
	private Double finalScore;
	
	
	private Double apiScore;
	
	private Double apiScoreNormalized;
	
	@Expose
	private double[] sentenceVectors;
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	

	


	public double[] getSentenceVectors() {
		return sentenceVectors;
	}

	public void setSentenceVectors(double[] sentenceVectors) {
		this.sentenceVectors = sentenceVectors;
	}

	public Integer getUpVotesScore() {
		return upVotesScore;
	}

	public void setUpVotesScore(Integer upVotesScore) {
		this.upVotesScore = upVotesScore;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Bucket other = (Bucket) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	

	public Bucket() {
		super();
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	


	@Override
	public String toString() {
		return "Bucket [id=" + id + "]";
	}

	public Integer getUserReputation() {
		return userReputation;
	}

	public void setUserReputation(Integer userReputation) {
		this.userReputation = userReputation;
	}

	public Integer getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}

	public Integer getViewCount() {
		return viewCount;
	}

	public void setViewCount(Integer viewCount) {
		this.viewCount = viewCount;
	}

	public Boolean getAcceptedAnswer() {
		return acceptedAnswer;
	}

	public void setAcceptedAnswer(Boolean acceptedAnswer) {
		this.acceptedAnswer = acceptedAnswer;
	}

	public Integer getParentUpVotesScore() {
		return parentUpVotesScore;
	}

	public void setParentUpVotesScore(Integer parentUpVotesScore) {
		this.parentUpVotesScore = parentUpVotesScore;
	}

	public Double getCalculatedScore() {
		return calculatedScore;
	}

	public void setCalculatedScore(Double calculatedScore) {
		this.calculatedScore = calculatedScore;
	}

	public String getProcessedBody() {
		return processedBody;
	}

	public void setProcessedBody(String processedBody) {
		this.processedBody = processedBody;
	}

	public String getProcessedCode() {
		return processedCode;
	}

	public void setProcessedCode(String processedCode) {
		this.processedCode = processedCode;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getAcceptedAnswerId() {
		return acceptedAnswerId;
	}

	public void setAcceptedAnswerId(Integer acceptedAnswerId) {
		this.acceptedAnswerId = acceptedAnswerId;
	}

	public String getProcessedTitle() {
		return processedTitle;
	}

	public void setProcessedTitle(String processedTitle) {
		this.processedTitle = processedTitle;
	}

	public String getParentProcessedTitle() {
		return parentProcessedTitle;
	}

	public void setParentProcessedTitle(String parentProcessedTitle) {
		this.parentProcessedTitle = parentProcessedTitle;
	}

	public String getParentProcessedBody() {
		return parentProcessedBody;
	}

	public void setParentProcessedBody(String parentProcessedBody) {
		this.parentProcessedBody = parentProcessedBody;
	}

	public String getParentProcessedCode() {
		return parentProcessedCode;
	}

	public void setParentProcessedCode(String parentProcessedCode) {
		this.parentProcessedCode = parentProcessedCode;
	}

	public Double getTitleScore() {
		return titleScore;
	}

	public void setTitleScore(Double titleScore) {
		this.titleScore = titleScore;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Double getTfIdfCosineSimScore() {
		return tfIdfCosineSimScore;
	}

	public void setTfIdfCosineSimScore(Double tfIdfCosineSimScore) {
		this.tfIdfCosineSimScore = tfIdfCosineSimScore;
	}

	public Double getSimPair() {
		return simPair;
	}

	public void setSimPair(Double simPair) {
		this.simPair = simPair;
	}

	public String getAcceptedOrMostUpvotedAnswerOfParentProcessedBody() {
		return acceptedOrMostUpvotedAnswerOfParentProcessedBody;
	}

	public void setAcceptedOrMostUpvotedAnswerOfParentProcessedBody(
			String acceptedOrMostUpvotedAnswerOfParentProcessedBody) {
		this.acceptedOrMostUpvotedAnswerOfParentProcessedBody = acceptedOrMostUpvotedAnswerOfParentProcessedBody;
	}

	public String getAcceptedOrMostUpvotedAnswerOfParentProcessedCode() {
		return acceptedOrMostUpvotedAnswerOfParentProcessedCode;
	}

	public void setAcceptedOrMostUpvotedAnswerOfParentProcessedCode(
			String acceptedOrMostUpvotedAnswerOfParentProcessedCode) {
		this.acceptedOrMostUpvotedAnswerOfParentProcessedCode = acceptedOrMostUpvotedAnswerOfParentProcessedCode;
	}

	public String getAcceptedAnswerBody() {
		return acceptedAnswerBody;
	}

	public void setAcceptedAnswerBody(String acceptedAnswerBody) {
		this.acceptedAnswerBody = acceptedAnswerBody;
	}

	

	public Bucket(Integer id, String processedBody) {
		super();
		this.id = id;
		this.processedBody = processedBody;
	}

	public String getThreadContent() {
		return threadContent;
	}

	public void setThreadContent(String threadContent) {
		this.threadContent = threadContent;
	}

	public Double getBm25Score() {
		return bm25Score;
	}

	public void setBm25Score(Double bm25Score) {
		this.bm25Score = bm25Score;
	}

	public String getProcessedBodyLemma() {
		return processedBodyLemma;
	}

	public void setProcessedBodyLemma(String processedBodyLemma) {
		this.processedBodyLemma = processedBodyLemma;
	}

	public String getProcessedTitleLemma() {
		return processedTitleLemma;
	}

	public void setProcessedTitleLemma(String processedTitleLemma) {
		this.processedTitleLemma = processedTitleLemma;
	}

	public Set<Bucket> getAnswers() {
		return answers;
	}

	public void setAnswers(Set<Bucket> answers) {
		this.answers = answers;
	}

	public String getParentProcessedBodyLemma() {
		return parentProcessedBodyLemma;
	}

	public void setParentProcessedBodyLemma(String parentProcessedBodyLemma) {
		this.parentProcessedBodyLemma = parentProcessedBodyLemma;
	}

	public String getParentProcessedTitleLemma() {
		return parentProcessedTitleLemma;
	}

	public void setParentProcessedTitleLemma(String parentProcessedTitleLemma) {
		this.parentProcessedTitleLemma = parentProcessedTitleLemma;
	}

	public String getTopics() {
		return topics;
	}

	public void setTopics(String topics) {
		this.topics = topics;
	}

	public String getHotTopics() {
		return hotTopics;
	}

	public void setHotTopics(String hotTopics) {
		this.hotTopics = hotTopics;
	}

	public Map<Integer, Double> getHotTopicsIdValueMap() {
		return hotTopicsIdValueMap;
	}

	public void setHotTopicsIdValueMap(Map<Integer, Double> hotTopicsIdValueMap) {
		this.hotTopicsIdValueMap = hotTopicsIdValueMap;
	}

	

	public String getTopScoredAnswers() {
		return topScoredAnswers;
	}

	public void setTopScoredAnswers(String topScoredAnswers) {
		this.topScoredAnswers = topScoredAnswers;
	}

	public Set<Integer> getTopScoredAnswersSet() {
		return topScoredAnswersSet;
	}

	public void setTopScoredAnswersSet(Set<Integer> topScoredAnswersSet) {
		this.topScoredAnswersSet = topScoredAnswersSet;
	}

	public Double getTopicScore() {
		return topicScore;
	}

	public void setTopicScore(Double topicScore) {
		this.topicScore = topicScore;
	}

	public Map<Integer, Double> getTopicsIdValueMap() {
		return topicsIdValueMap;
	}

	public void setTopicsIdValueMap(Map<Integer, Double> topicsIdValueMap) {
		this.topicsIdValueMap = topicsIdValueMap;
	}

	public Integer getIntersectionSize() {
		return intersectionSize;
	}

	public void setIntersectionSize(Integer intersectionSize) {
		this.intersectionSize = intersectionSize;
	}

	public Set<String> getMethods() {
		return methods;
	}

	public void setMethods(Set<String> methods) {
		this.methods = methods;
	}

	public Integer getTagId() {
		return tagId;
	}

	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}

	public Boolean getContainCode() {
		return containCode;
	}

	public void setContainCode(Boolean containCode) {
		this.containCode = containCode;
	}

	public Double getThreadSim() {
		return threadSim;
	}

	public void setThreadSim(Double threadSim) {
		this.threadSim = threadSim;
	}

	public Integer getAnswerCount() {
		return answerCount;
	}

	public void setAnswerCount(Integer answerCount) {
		this.answerCount = answerCount;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Double getMethodScore() {
		return methodScore;
	}

	public void setMethodScore(Double methodScore) {
		this.methodScore = methodScore;
	}

	public Double getTfIdfCosineSimScoreNormalized() {
		return tfIdfCosineSimScoreNormalized;
	}

	public void setTfIdfCosineSimScoreNormalized(Double tfIdfCosineSimScoreNormalized) {
		this.tfIdfCosineSimScoreNormalized = tfIdfCosineSimScoreNormalized;
	}

	public Double getMethodScoreNormalized() {
		return methodScoreNormalized;
	}

	public void setMethodScoreNormalized(Double methodScoreNormalized) {
		this.methodScoreNormalized = methodScoreNormalized;
	}

	public Double getBm25ScoreNormalized() {
		return bm25ScoreNormalized;
	}

	public void setBm25ScoreNormalized(Double bm25ScoreNormalized) {
		this.bm25ScoreNormalized = bm25ScoreNormalized;
	}

	public Double getSimPairNormalized() {
		return simPairNormalized;
	}

	public void setSimPairNormalized(Double simPairNormalized) {
		this.simPairNormalized = simPairNormalized;
	}

	public Double getThreadSimNormalized() {
		return threadSimNormalized;
	}

	public void setThreadSimNormalized(Double threadSimNormalized) {
		this.threadSimNormalized = threadSimNormalized;
	}

	public Double getJaccardScore() {
		return jaccardScore;
	}

	public void setJaccardScore(Double jaccardScore) {
		this.jaccardScore = jaccardScore;
	}

	public Double getFinalScore() {
		return finalScore;
	}

	public void setFinalScore(Double finalScore) {
		this.finalScore = finalScore;
	}

	public Double getQueryCoverageScore() {
		return queryCoverageScore;
	}

	public void setQueryCoverageScore(Double queryCoverageScore) {
		this.queryCoverageScore = queryCoverageScore;
	}

	public Double getCodeCoverageScore() {
		return codeCoverageScore;
	}

	public void setCodeCoverageScore(Double codeCoverageScore) {
		this.codeCoverageScore = codeCoverageScore;
	}

	public Double getApiScore() {
		return apiScore;
	}

	public void setApiScore(Double apiScore) {
		this.apiScore = apiScore;
	}

	public Double getApiScoreNormalized() {
		return apiScoreNormalized;
	}

	public void setApiScoreNormalized(Double apiScoreNormalized) {
		this.apiScoreNormalized = apiScoreNormalized;
	}


	

	
    
}