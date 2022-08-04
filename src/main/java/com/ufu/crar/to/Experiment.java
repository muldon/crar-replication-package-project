package com.ufu.crar.to;

import java.sql.Timestamp;

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
@Table(name = "experiment")
public class Experiment {
	@Id
    @SequenceGenerator(name="experiment_id", sequenceName="experiment_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="experiment_id")
	private Integer id;

	@Column(name="externalquestionid")
	private Integer externalQuestionId;

	@Column(name="date")
	private Timestamp date;
	
	private String obs;
	
	private String duration;
	
	private Double alpha;
		
	private Double beta;
		
	private Double gamma;
	
	private Double delta;
	
	private Double epsilon;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getExternalQuestionId() {
		return externalQuestionId;
	}

	public void setExternalQuestionId(Integer externalQuestionId) {
		this.externalQuestionId = externalQuestionId;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Double getAlpha() {
		return alpha;
	}

	public void setAlpha(Double alpha) {
		this.alpha = alpha;
	}

	public Double getBeta() {
		return beta;
	}

	public void setBeta(Double beta) {
		this.beta = beta;
	}

	public Double getGamma() {
		return gamma;
	}

	public void setGamma(Double gamma) {
		this.gamma = gamma;
	}

	public Double getDelta() {
		return delta;
	}

	public void setDelta(Double delta) {
		this.delta = delta;
	}

	public Double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(Double epsilon) {
		this.epsilon = epsilon;
	}

	@Override
	public String toString() {
		return "Experiment [id=" + id + ", externalQuestionId=" + externalQuestionId + ", date=" + date + ", obs=" + obs
				+ ", duration=" + duration + ", alpha=" + alpha + ", beta=" + beta + ", gamma=" + gamma + ", delta="
				+ delta + ", epsilon=" + epsilon + "]";
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
		Experiment other = (Experiment) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Experiment(Integer externalQuestionId, Double alpha, Double beta,
			Double gamma, Double delta, Double epsilon, Timestamp date, String duration,String obs) {
		super();
		this.externalQuestionId = externalQuestionId;
		this.date = date;
		this.obs = obs;
		this.duration = duration;
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
		this.delta = delta;
		this.epsilon = epsilon;
	}

	public Experiment() {
		super();
	}
		

	
		
	

}
