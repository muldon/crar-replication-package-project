package com.ufu.crar.to;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserEvaluation {
	
	private Integer externalQuestionId;
	private String query;
	private Integer likertScaleUser1;
	private Integer likertScaleUser2;
	private Double meanLikert;
	private Integer postId;

	public Integer getLikertScaleUser1() {
		return likertScaleUser1;
	}


	public void setLikertScaleUser1(Integer likertScaleUser1) {
		this.likertScaleUser1 = likertScaleUser1;
	}


	public Integer getLikertScaleUser2() {
		return likertScaleUser2;
	}


	public void setLikertScaleUser2(Integer likertScaleUser2) {
		this.likertScaleUser2 = likertScaleUser2;
	}


	


	public Double getMeanLikert() {
		return meanLikert;
	}


	public void setMeanLikert(Double meanLikert) {
		this.meanLikert = meanLikert;
	}


	public Integer getExternalQuestionId() {
		return externalQuestionId;
	}


	public void setExternalQuestionId(Integer externalQuestionId) {
		this.externalQuestionId = externalQuestionId;
	}


	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((externalQuestionId == null) ? 0 : externalQuestionId.hashCode());
		result = prime * result + ((postId == null) ? 0 : postId.hashCode());
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
		UserEvaluation other = (UserEvaluation) obj;
		if (externalQuestionId == null) {
			if (other.externalQuestionId != null)
				return false;
		} else if (!externalQuestionId.equals(other.externalQuestionId))
			return false;
		if (postId == null) {
			if (other.postId != null)
				return false;
		} else if (!postId.equals(other.postId))
			return false;
		return true;
	}


	public Integer getPostId() {
		return postId;
	}


	public void setPostId(Integer postId) {
		this.postId = postId;
	}


	


	@Override
	public String toString() {
		return "UserEvaluation [externalQuestionId=" + externalQuestionId + ", query=" + query + ", likertScaleUser1=" + likertScaleUser1 + ", likertScaleUser2=" + likertScaleUser2 + ", meanLikert=" + meanLikert + ", postId=" + postId + "]";
	}


	public String getQuery() {
		return query;
	}


	public void setQuery(String query) {
		this.query = query;
	}


	

	
	
	
}
