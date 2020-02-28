package com.ufu.crar.to;

import java.sql.Timestamp;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "metricsresults")
public class MetricResult {
	@Id
    @SequenceGenerator(name="metricsresults_id_seq", sequenceName="metricsresults_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="metricsresults_id_seq")
    private Integer id;
	
	@Column(name="hitk")
	private Double hitK;
		
	@Column(name="mrrk")
	private Double mrrK;
	

	@Column(name="mapk")
	private Double mapK;
	
		
	@Column(name="mrk")
	private Double mrK;
	
	
	
	@Column(name="bm25topnsmallresults")
	private Integer bm25TopNSmallResults;
	
	@Column(name="bm25topnbigresults")
	private Integer bm25TopNBigResults;
	
	@Column(name="topapisscoredpairspercent")
	private Integer topApisScoredPairsPercent;
	
	//@Column(name="topsimilarcontentsasymrelevancenumber")
	@Transient
	private Integer topSimilarContentsAsymRelevanceNumber;
	
	private Integer cutoff;
	
	private Integer topk;
	
	private String obs;
	
	@Column(name="classfreqweight")
	public Double classFreqWeight;
	
	@Column(name="methodfreqweight")
	public Double methodFreqWeight;
	
	@Column(name="sent2vecweight")
	public Double sent2VecWeight;
	
	@Column(name="fasttextweight")
	public Double fastTextWeight;
	
	@Column(name="upweight")
	public Double upWeight;
	
	@Column(name="tfidfcossimweight")
	public Double tfIdfCosSimWeight;
	
	@Column(name="bm25weight")
	public Double bm25weight;
	
	public String approach;
	
	@Column(name="topclasses")
	private Integer topClasses;
	
	private Timestamp date;
	
	@Column(name="searchparams")
	private String searchParams;
	
	@Column(name="vectorstype")
	private Integer vectorsType;
	
	@Column(name="isquerymetric")
	public Boolean isQueryMetric;
	
	@Column(name="isweightstraining")
	public Boolean isWeightsTraining;
	
	@Column(name="tagid")
	private Integer tagId;
	
	@Transient
	private Double hitK10;
	
	@Transient
	private Double mrrK10;
	
	@Transient
	private Double mapK10;
	
	@Transient
	private Double mrK10;
	

	@Transient
	private Double hitK5;
	
	@Transient
	private Double mrrK5;
	
	@Transient
	private Double mapK5;
	
	@Transient
	private Double mrK5;
	
	
	public MetricResult(String approach,Integer bm25TopNSmallResults,Integer bm25TopNBigResults, Integer topApisScoredPairsPercent, Integer topSimilarTitlesPercent, Integer cutoff, Integer topk, String obs,Integer numberofapiclasses, String searchParams,Integer tagId) {
		super();
		this.bm25TopNSmallResults = bm25TopNSmallResults;
		this.bm25TopNBigResults = bm25TopNBigResults;
		this.topApisScoredPairsPercent = topApisScoredPairsPercent;
		this.topSimilarContentsAsymRelevanceNumber = topSimilarTitlesPercent;
		this.cutoff = cutoff;
		this.topk = topk;
		this.obs = obs;
		this.approach = approach;
		this.topClasses=numberofapiclasses;
		this.date = new Timestamp(Calendar.getInstance().getTimeInMillis());
		this.searchParams=searchParams;
		this.tagId=tagId;
	}
	public MetricResult() {
		super();
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Double getHitK() {
		return hitK;
	}
	public void setHitK(Double hitK) {
		this.hitK = hitK;
	}
	public Double getMrrK() {
		return mrrK;
	}
	public void setMrrK(Double mrrK) {
		this.mrrK = mrrK;
	}
	public Double getMapK() {
		return mapK;
	}
	public void setMapK(Double mapK) {
		this.mapK = mapK;
	}
	public Double getMrK() {
		return mrK;
	}
	public void setMrK(Double mrK) {
		this.mrK = mrK;
	}
	public Integer getBm25TopNSmallResults() {
		return bm25TopNSmallResults;
	}
	public void setBm25TopNSmallResults(Integer bm25TopNSmallResults) {
		this.bm25TopNSmallResults = bm25TopNSmallResults;
	}
	public Integer getBm25TopNBigResults() {
		return bm25TopNBigResults;
	}
	public void setBm25TopNBigResults(Integer bm25TopNBigResults) {
		this.bm25TopNBigResults = bm25TopNBigResults;
	}
	public Integer getTopApisScoredPairsPercent() {
		return topApisScoredPairsPercent;
	}
	public void setTopApisScoredPairsPercent(Integer topApisScoredPairsPercent) {
		this.topApisScoredPairsPercent = topApisScoredPairsPercent;
	}
	public Integer getTopSimilarContentsAsymRelevanceNumber() {
		return topSimilarContentsAsymRelevanceNumber;
	}
	public void setTopSimilarContentsAsymRelevanceNumber(Integer topSimilarContentsAsymRelevanceNumber) {
		this.topSimilarContentsAsymRelevanceNumber = topSimilarContentsAsymRelevanceNumber;
	}
	public Integer getCutoff() {
		return cutoff;
	}
	public void setCutoff(Integer cutoff) {
		this.cutoff = cutoff;
	}
	public Integer getTopk() {
		return topk;
	}
	public void setTopk(Integer topk) {
		this.topk = topk;
	}
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}
	public Double getClassFreqWeight() {
		return classFreqWeight;
	}
	public void setClassFreqWeight(Double classFreqWeight) {
		this.classFreqWeight = classFreqWeight;
	}
	public Double getMethodFreqWeight() {
		return methodFreqWeight;
	}
	public void setMethodFreqWeight(Double methodFreqWeight) {
		this.methodFreqWeight = methodFreqWeight;
	}
	
	public Double getUpWeight() {
		return upWeight;
	}
	public void setUpWeight(Double upWeight) {
		this.upWeight = upWeight;
	}
	public Double getTfIdfCosSimWeight() {
		return tfIdfCosSimWeight;
	}
	public void setTfIdfCosSimWeight(Double tfIdfCosSimWeight) {
		this.tfIdfCosSimWeight = tfIdfCosSimWeight;
	}
	public String getApproach() {
		return approach;
	}
	public void setApproach(String approach) {
		this.approach = approach;
	}
	public Integer getTopClasses() {
		return topClasses;
	}
	public void setTopClasses(Integer topClasses) {
		this.topClasses = topClasses;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public Double getBm25weight() {
		return bm25weight;
	}
	public void setBm25weight(Double bm25weight) {
		this.bm25weight = bm25weight;
	}
	public Double getHitK10() {
		return hitK10;
	}
	public void setHitK10(Double hitK10) {
		this.hitK10 = hitK10;
	}
	public Double getMrrK10() {
		return mrrK10;
	}
	public void setMrrK10(Double mrrK10) {
		this.mrrK10 = mrrK10;
	}
	public Double getMapK10() {
		return mapK10;
	}
	public void setMapK10(Double mapK10) {
		this.mapK10 = mapK10;
	}
	public Double getMrK10() {
		return mrK10;
	}
	public void setMrK10(Double mrK10) {
		this.mrK10 = mrK10;
	}
	public Double getHitK5() {
		return hitK5;
	}
	public void setHitK5(Double hitK5) {
		this.hitK5 = hitK5;
	}
	public Double getMrrK5() {
		return mrrK5;
	}
	public void setMrrK5(Double mrrK5) {
		this.mrrK5 = mrrK5;
	}
	public Double getMapK5() {
		return mapK5;
	}
	public void setMapK5(Double mapK5) {
		this.mapK5 = mapK5;
	}
	public Double getMrK5() {
		return mrK5;
	}
	public void setMrK5(Double mrK5) {
		this.mrK5 = mrK5;
	}
	
	
	public Double getSent2VecWeight() {
		return sent2VecWeight;
	}
	public void setSent2VecWeight(Double sent2VecWeight) {
		this.sent2VecWeight = sent2VecWeight;
	}
	public Double getFastTextWeight() {
		return fastTextWeight;
	}
	public void setFastTextWeight(Double fastTextWeight) {
		this.fastTextWeight = fastTextWeight;
	}
	@Override
	public String toString() {
		return " & " + hitK10 + " & " + hitK5 + " & " + mrrK10 + " & " + mrrK5  + " & " + mapK10 + " & " + mapK5 + " & " + mrK10 + " & " + mrK5 + " \\\\";
	}
	public String getSearchParams() {
		return searchParams;
	}
	public void setSearchParams(String searchParams) {
		this.searchParams = searchParams;
	}
	public Integer getVectorsType() {
		return vectorsType;
	}
	public void setVectorsType(Integer vectorsType) {
		this.vectorsType = vectorsType;
	}
	public Boolean getIsQueryMetric() {
		return isQueryMetric;
	}
	public void setIsQueryMetric(Boolean isQueryMetric) {
		this.isQueryMetric = isQueryMetric;
	}
	public Boolean getIsWeightsTraining() {
		return isWeightsTraining;
	}
	public void setIsWeightsTraining(Boolean isWeightsTraining) {
		this.isWeightsTraining = isWeightsTraining;
	}
	public Integer getTagId() {
		return tagId;
	}
	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}
	
	
	
}
