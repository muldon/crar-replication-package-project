package com.ufu.crar.to;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AnswerParentPair extends Bucket implements Comparable<AnswerParentPair> {
	protected Integer answerId;
	
	protected Integer parentId;
	
	protected Double apiScore;

	public Integer getAnswerId() {
		return answerId;
	}

	public void setAnswerId(Integer answerId) {
		this.answerId = answerId;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((answerId == null) ? 0 : answerId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnswerParentPair other = (AnswerParentPair) obj;
		if (answerId == null) {
			if (other.answerId != null)
				return false;
		} else if (!answerId.equals(other.answerId))
			return false;
		return true;
	}

	public AnswerParentPair(Integer answerId, Integer parentId) {
		super();
		this.answerId = answerId;
		this.parentId = parentId;
	}

	public AnswerParentPair() {
		super();
	}

	@Override
	public int compareTo(AnswerParentPair o) {
		return apiScore.compareTo(o.getApiScore());
	}

	@Override
	public String toString() {
		return "AnswerParentPair [answerId=" + answerId + ", parentId=" + parentId + ", apiScore=" + apiScore + "]";
	}

	public Double getApiScore() {
		return apiScore;
	}

	public void setApiScore(Double apiScore) {
		this.apiScore = apiScore;
	}
	
	
	

	
	
    
}