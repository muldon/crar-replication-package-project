package com.ufu.crar.to;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement
@Entity
@Table(name = "result")
public class Result {
	@Id
    @SequenceGenerator(name="result_id_seq", sequenceName="result_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="result_id_seq")
	private Integer id;

	@Column(name="experimentid")
	private Integer experimentId;
	
	private String obs;

	@Column(name="minlikertscale")
	private Integer minLikertScale;

	@Column(name="evaluationphase")	
	private Integer evaluationPhase;
	
	@Column(name="recallrate1")
	private Double recallRate1;
	
	@Column(name="recallrate5")
	private Double recallRate5;
	
	@Column(name="recallrate10")
	private Double recallRate10;
	
	private Double mrr;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getExperimentId() {
		return experimentId;
	}

	public void setExperimentId(Integer experimentId) {
		this.experimentId = experimentId;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	

	public Double getRecallRate1() {
		return recallRate1;
	}

	public void setRecallRate1(Double recallRate1) {
		this.recallRate1 = recallRate1;
	}

	public Double getRecallRate5() {
		return recallRate5;
	}

	public void setRecallRate5(Double recallRate5) {
		this.recallRate5 = recallRate5;
	}

	public Double getRecallRate10() {
		return recallRate10;
	}

	public void setRecallRate10(Double recallRate10) {
		this.recallRate10 = recallRate10;
	}

	public Double getMrr() {
		return mrr;
	}

	public void setMrr(Double mrr) {
		this.mrr = mrr;
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
		Result other = (Result) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Result [id=" + id + ", experimentId=" + experimentId + ", obs=" + obs + ", likertScale=" + minLikertScale
				+ ", evaluationPhase=" + evaluationPhase + ", recallRate1=" + recallRate1 + ", recallRate5="
				+ recallRate5 + ", recallRate10=" + recallRate10 + ", mrr=" + mrr + "]";
	}

	public Result(Integer experimentId, String obs, Integer minLikertScale, Integer evaluationPhase, Double recallRate1,
			Double recallRate5, Double recallRate10, Double mrr) {
		super();
		this.experimentId = experimentId;
		this.obs = obs;
		this.minLikertScale = minLikertScale;
		this.evaluationPhase = evaluationPhase;
		this.recallRate1 = recallRate1;
		this.recallRate5 = recallRate5;
		this.recallRate10 = recallRate10;
		this.mrr = mrr;
	}

	public Result() {
		super();
	}

	public Integer getMinLikertScale() {
		return minLikertScale;
	}

	public void setMinLikertScale(Integer minLikertScale) {
		this.minLikertScale = minLikertScale;
	}

	public Integer getEvaluationPhase() {
		return evaluationPhase;
	}

	public void setEvaluationPhase(Integer evaluationPhase) {
		this.evaluationPhase = evaluationPhase;
	}
	
	
		
	

}
