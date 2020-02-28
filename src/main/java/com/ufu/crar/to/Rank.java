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
@Table(name = "rank")
public class Rank {
	@Id
    @SequenceGenerator(name="rank_id_seq", sequenceName="rank_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="rank_id_seq")
	private Integer id;
		
	@Column(name="rankorder")	
	private Integer rankOrder;
	
	
	@Column(name="relatedpostid")	
	private Integer relatedPostId;
	
	
	@Column(name="internalevaluation")	
	private Boolean internalEvaluation;
		
	@Column(name="phase")	
	private Integer phase;

	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}

	


	public Integer getRelatedPostId() {
		return relatedPostId;
	}


	public void setRelatedPostId(Integer relatedPostId) {
		this.relatedPostId = relatedPostId;
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
		Rank other = (Rank) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	public Integer getRankOrder() {
		return rankOrder;
	}


	public void setRankOrder(Integer rankOrder) {
		this.rankOrder = rankOrder;
	}



	public Rank() {
		super();
	}


	public Rank(Integer relatedPostId, Integer rankOrder, Boolean internalEvaluation,Integer phase) {
		super();
		this.rankOrder = rankOrder;
		this.relatedPostId = relatedPostId;
		this.internalEvaluation = internalEvaluation;
		this.phase = phase;
	}


	public Boolean getInternalEvaluation() {
		return internalEvaluation;
	}


	public void setInternalEvaluation(Boolean internalEvaluation) {
		this.internalEvaluation = internalEvaluation;
	}



	public Integer getPhase() {
		return phase;
	}


	public void setPhase(Integer phase) {
		this.phase = phase;
	}

	
	
	

	
	
	
	
	
}
