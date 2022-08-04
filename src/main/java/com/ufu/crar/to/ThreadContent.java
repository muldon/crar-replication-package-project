package com.ufu.crar.to;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;
import com.ufu.crar.tfidf.Document;

@XmlRootElement
@Entity
@Table(name = "threadscontents")
public class ThreadContent extends GenericBucket{
	private static final long serialVersionUID = -111152190111815641L;
	@Id
	@Expose
    private Integer id;
	
	@Expose
	@Column(name="questionbody")
	private String questionBody;
	
	@Expose
	@Column(name="answersbody")
	private String answersBody;
	
	@Expose
	private String code;
	
	@Expose
	private String title;
	
	@Expose
	@Column(name="tagid")
	private Integer tagId;
	
	
	@Expose
	@Column(name="answercount")
	private Integer answerCount;
	
	@Transient
	protected Double answerCountMax;
	
	@Expose
	protected Integer upvotes;
	
	@Transient
	protected Double upvotesMax;
	
	@Expose
	@Column(name="threadtotalupvotes")
	protected Integer threadTotalUpvotes;
	
	@Transient
	protected Double threadTotalUpvotesMax;
	
	@Expose
	@Column(name="threadtotalcomments")
	protected Integer threadTotalComments;
	
	@Column(name="sentencevectors")
    private String sentenceVectorsStr;
		
	
	@Transient
	protected Document document;
	
	@Transient
	protected Double simPair;
	
	@Transient
	protected Double titleScore;
		
	@Transient
	protected Double threadTitleAsymmetricSim;
	
	@Transient
	protected Double threadTitleAsymmetricSimNormalized;
	
	@Transient
	protected Double threadTitleAsymmetricSimMax;
	
	@Transient
	protected Double threadQuestionBodyAnswersBodyAsymmetricSim;
	
	@Transient
	protected Double threadQuestionBodyAnswersBodyAsymmetricSimNormalized;
	
	@Transient
	protected Double threadBodyAsymmetricSimMax;
	
	@Transient
	protected Double tfIdfCosineSimScore;
	
	@Transient
	protected Double tfIdfCosineSimScoreNormalized;
	
	@Transient
	protected Double tfIdfCosineSimScoreMax;
	
	@Expose
	@Transient
	protected Double bm25Score;
	
	@Transient
	protected Double bm25ScoreNormalized;
	
	@Expose
	@Transient
	protected Double bm25ScoreMax;

	@Transient
	protected Double threadFinalScore;
	
	
	
	//@Transient
	//protected Double threadAnswerCountScore;
	
	@Transient
	protected Double threadAnswerCountScoreNormalized;
	
	@Transient
	protected Double threadUpVotesScore;
	
	//@Transient
	//protected Double threadTotalUpVotesScore;
	
	@Transient
	protected Double threadTotalUpVotesScoreNormalized;
	
	@Transient
	protected Double queryTitleCoverageScore;
	
	@Transient
	protected Double queryCodeCoverageScore;
	
	@Transient
	protected Double jaccardScore;
	
	@Expose
	@Transient
	private double[] sentenceVectors;
	
	@Transient
	private Double sent2VecScore;
	
	@Transient
	private Double sent2VecScoreNormalized;
	
	@Transient
	protected Double threadSent2VecScoreMax;
	
	@Transient
	protected double[] embeddedDLInput;
	
	@Transient
	protected double[] embeddedDLOutput;

	public ThreadContent() {
	}
	
	public ThreadContent(Integer id) {
		this.id = id;
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
		ThreadContent other = (ThreadContent) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getTagId() {
		return tagId;
	}

	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}

	

	public Double getBm25Score() {
		return bm25Score;
	}

	public void setBm25Score(Double bm25Score) {
		this.bm25Score = bm25Score;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Double getSimPair() {
		return simPair;
	}

	public void setSimPair(Double simPair) {
		this.simPair = simPair;
	}

	public Double getTfIdfCosineSimScore() {
		return tfIdfCosineSimScore;
	}

	public void setTfIdfCosineSimScore(Double tfIdfCosineSimScore) {
		this.tfIdfCosineSimScore = tfIdfCosineSimScore;
	}

	public Double getTitleScore() {
		return titleScore;
	}

	public void setTitleScore(Double titleScore) {
		this.titleScore = titleScore;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getAnswerCount() {
		return answerCount;
	}

	public void setAnswerCount(Integer answerCount) {
		this.answerCount = answerCount;
	}

	
	

	public double[] getEmbeddedDLInput() {
		return embeddedDLInput;
	}

	public void setEmbeddedDLInput(double[] embeddedDLInput) {
		this.embeddedDLInput = embeddedDLInput;
	}

	public double[] getEmbeddedDLOutput() {
		return embeddedDLOutput;
	}

	public void setEmbeddedDLOutput(double[] embeddedDLOutput) {
		this.embeddedDLOutput = embeddedDLOutput;
	}

	public Double getThreadFinalScore() {
		return threadFinalScore;
	}

	public void setThreadFinalScore(Double threadFinalScore) {
		this.threadFinalScore = threadFinalScore;
	}

	public Integer getThreadTotalUpvotes() {
		return threadTotalUpvotes;
	}

	public void setThreadTotalUpvotes(Integer threadTotalUpvotes) {
		this.threadTotalUpvotes = threadTotalUpvotes;
	}

	public Integer getThreadTotalComments() {
		return threadTotalComments;
	}

	public void setThreadTotalComments(Integer threadTotalComments) {
		this.threadTotalComments = threadTotalComments;
	}
	
	

	public ThreadContent(Integer id, String title, String questionBody,String answersBody,String code, 
			Integer answerCount,Integer threadTotalUpvotes, Integer threadTotalComments, Integer tagId, Integer upvotes) {
		super();
		this.id = id;
		this.title = title;
		this.questionBody = questionBody;
		this.answersBody = answersBody;
		this.code = code;
		this.answerCount = answerCount;
		this.threadTotalUpvotes = threadTotalUpvotes;
		this.threadTotalComments = threadTotalComments;
		this.tagId = tagId;
		this.upvotes = upvotes;
	}

	public Integer getUpvotes() {
		return upvotes;
	}

	public void setUpvotes(Integer upvotes) {
		this.upvotes = upvotes;
	}

	

	public Double getThreadUpVotesScore() {
		return threadUpVotesScore;
	}

	public void setThreadUpVotesScore(Double threadUpVotesScore) {
		this.threadUpVotesScore = threadUpVotesScore;
	}

	

	public Double getThreadTitleAsymmetricSim() {
		return threadTitleAsymmetricSim;
	}

	public void setThreadTitleAsymmetricSim(Double threadTitleAsymmetricSim) {
		this.threadTitleAsymmetricSim = threadTitleAsymmetricSim;
	}

	public Double getThreadQuestionBodyAnswersBodyAsymmetricSim() {
		return threadQuestionBodyAnswersBodyAsymmetricSim;
	}

	public void setThreadQuestionBodyAnswersBodyAsymmetricSim(Double threadBodyAsymmetricSim) {
		this.threadQuestionBodyAnswersBodyAsymmetricSim = threadBodyAsymmetricSim;
	}

	public Double getQueryTitleCoverageScore() {
		return queryTitleCoverageScore;
	}

	public void setQueryTitleCoverageScore(Double queryCoverageScore) {
		this.queryTitleCoverageScore = queryCoverageScore;
	}

	public Double getQueryCodeCoverageScore() {
		return queryCodeCoverageScore;
	}

	public void setQueryCodeCoverageScore(Double queryCodeCoverageScore) {
		this.queryCodeCoverageScore = queryCodeCoverageScore;
	}

	public String getQuestionBody() {
		return questionBody;
	}

	public void setQuestionBody(String questionBody) {
		this.questionBody = questionBody;
	}

	public String getAnswersBody() {
		return answersBody;
	}

	public void setAnswersBody(String answersBody) {
		this.answersBody = answersBody;
	}

	

	

	@Override
	public String toString() {
		return "ThreadContent [id=" + id + ", questionBody=" + questionBody + ", title=" + title + ", tagId=" + tagId
				+ "]";
	}

	public Double getJaccardScore() {
		return jaccardScore;
	}

	public void setJaccardScore(Double jaccardScore) {
		this.jaccardScore = jaccardScore;
	}

	public Double getBm25ScoreMax() {
		return bm25ScoreMax;
	}

	public void setBm25ScoreMax(Double bm25ScoreMax) {
		this.bm25ScoreMax = bm25ScoreMax;
	}

	public Double getThreadTitleAsymmetricSimMax() {
		return threadTitleAsymmetricSimMax;
	}

	public void setThreadTitleAsymmetricSimMax(Double threadTitleAsymmetricSimMax) {
		this.threadTitleAsymmetricSimMax = threadTitleAsymmetricSimMax;
	}

	public Double getThreadBodyAsymmetricSimMax() {
		return threadBodyAsymmetricSimMax;
	}

	public void setThreadBodyAsymmetricSimMax(Double threadBodyAsymmetricSimMax) {
		this.threadBodyAsymmetricSimMax = threadBodyAsymmetricSimMax;
	}

	public Double getTfIdfCosineSimScoreMax() {
		return tfIdfCosineSimScoreMax;
	}

	public void setTfIdfCosineSimScoreMax(Double tfIdfCosineSimScoreMax) {
		this.tfIdfCosineSimScoreMax = tfIdfCosineSimScoreMax;
	}

	public Double getAnswerCountMax() {
		return answerCountMax;
	}

	public void setAnswerCountMax(Double answerCountMax) {
		this.answerCountMax = answerCountMax;
	}

	public Double getUpvotesMax() {
		return upvotesMax;
	}

	public void setUpvotesMax(Double upvotesMax) {
		this.upvotesMax = upvotesMax;
	}

	public Double getThreadTotalUpvotesMax() {
		return threadTotalUpvotesMax;
	}

	public void setThreadTotalUpvotesMax(Double threadTotalUpvotesMax) {
		this.threadTotalUpvotesMax = threadTotalUpvotesMax;
	}

	public Double getThreadTitleAsymmetricSimNormalized() {
		return threadTitleAsymmetricSimNormalized;
	}

	public void setThreadTitleAsymmetricSimNormalized(Double threadTitleAsymmetricSimNormalized) {
		this.threadTitleAsymmetricSimNormalized = threadTitleAsymmetricSimNormalized;
	}

	public Double getThreadQuestionBodyAnswersBodyAsymmetricSimNormalized() {
		return threadQuestionBodyAnswersBodyAsymmetricSimNormalized;
	}

	public void setThreadQuestionBodyAnswersBodyAsymmetricSimNormalized(Double threadBodyAsymmetricSimNormalized) {
		this.threadQuestionBodyAnswersBodyAsymmetricSimNormalized = threadBodyAsymmetricSimNormalized;
	}

	public Double getTfIdfCosineSimScoreNormalized() {
		return tfIdfCosineSimScoreNormalized;
	}

	public void setTfIdfCosineSimScoreNormalized(Double tfIdfCosineSimScoreNormalized) {
		this.tfIdfCosineSimScoreNormalized = tfIdfCosineSimScoreNormalized;
	}

	public Double getBm25ScoreNormalized() {
		return bm25ScoreNormalized;
	}

	public void setBm25ScoreNormalized(Double bm25ScoreNormalized) {
		this.bm25ScoreNormalized = bm25ScoreNormalized;
	}

	public Double getThreadAnswerCountScoreNormalized() {
		return threadAnswerCountScoreNormalized;
	}

	public void setThreadAnswerCountScoreNormalized(Double threadAnswerCountScoreNormalized) {
		this.threadAnswerCountScoreNormalized = threadAnswerCountScoreNormalized;
	}

	public Double getThreadTotalUpVotesScoreNormalized() {
		return threadTotalUpVotesScoreNormalized;
	}

	public void setThreadTotalUpVotesScoreNormalized(Double threadTotalUpVotesScoreNormalized) {
		this.threadTotalUpVotesScoreNormalized = threadTotalUpVotesScoreNormalized;
	}

	public String getSentenceVectorsStr() {
		return sentenceVectorsStr;
	}

	public void setSentenceVectorsStr(String sentenceVectorsStr) {
		this.sentenceVectorsStr = sentenceVectorsStr;
	}

	public double[] getSentenceVectors() {
		return sentenceVectors;
	}

	public void setSentenceVectors(double[] sentenceVectors) {
		this.sentenceVectors = sentenceVectors;
	}

	public Double getSent2VecScore() {
		return sent2VecScore;
	}

	public void setSent2VecScore(Double sent2VecScore) {
		this.sent2VecScore = sent2VecScore;
	}

	public Double getSent2VecScoreNormalized() {
		return sent2VecScoreNormalized;
	}

	public void setSent2VecScoreNormalized(Double sent2VecScoreNormalized) {
		this.sent2VecScoreNormalized = sent2VecScoreNormalized;
	}

	public Double getThreadSent2VecScoreMax() {
		return threadSent2VecScoreMax;
	}

	public void setThreadSent2VecScoreMax(Double threadSent2VecScoreMax) {
		this.threadSent2VecScoreMax = threadSent2VecScoreMax;
	}

	
	
	

	
		
    
}