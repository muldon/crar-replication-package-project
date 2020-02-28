package com.ufu.crar.to;

import java.sql.Timestamp;
import java.util.List;

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
@Table(name = "evaluation")
public class Evaluation {
	@Id
    @SequenceGenerator(name="evaluation_id_seq", sequenceName="evaluation_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="evaluation_id_seq")
	private Integer id;
	
	
	@Column(name="rankid")
	private Integer rankId;

	@Column(name="surveyuserid")	
	private Integer surveyUserId;
	
	@Column(name="likertscale")	
	private Integer likertScale;
		
	@Column(name="ratingdate")
	private Timestamp ratingDate;
	
	
	
	
	@Transient
	private List<Integer> ratings;
	
	@Transient
	private List<Integer> postsIds;
	
	@Transient
	private Integer externalQuestionId;
	
	@Transient
	private Integer postId;
	
	@Transient
	private Integer likertScaleUser1;
	
	@Transient
	private Integer likertScaleUser2;

	@Transient
	private Integer relatedTypeId;
	
	@Transient
	private Rank rank;
	
	@Transient
	private Double meanLikert;
	
	
	
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}



	public Integer getSurveyUserId() {
		return surveyUserId;
	}


	public void setSurveyUserId(Integer surveyUserId) {
		this.surveyUserId = surveyUserId;
	}


	public Integer getLikertScale() {
		return likertScale;
	}


	public void setLikertScale(Integer likertScale) {
		this.likertScale = likertScale;
	}


	public Timestamp getRatingDate() {
		return ratingDate;
	}


	public void setRatingDate(Timestamp ratingDate) {
		this.ratingDate = ratingDate;
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
		Evaluation other = (Evaluation) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}




	public List<Integer> getRatings() {
		return ratings;
	}


	public void setRatings(List<Integer> ratings) {
		this.ratings = ratings;
	}


	public List<Integer> getPostsIds() {
		return postsIds;
	}


	public void setPostsIds(List<Integer> postsIds) {
		this.postsIds = postsIds;
	}


	public Integer getRankId() {
		return rankId;
	}


	public void setRankId(Integer rankId) {
		this.rankId = rankId;
	}


	

	@Override
	public String toString() {
		return "Evaluation [id=" + id + ", postId=" + postId + ", meanLikert=" + meanLikert + ", externalQuestionId="
				+ externalQuestionId + ", surveyUserId=" + surveyUserId + ", rankId=" + rankId + ", likertScale="
				+ likertScale + ", ratingDate=" + ratingDate + ", ratings=" + ratings + ", postsIds=" + postsIds
				+ ", likertScaleUser1=" + likertScaleUser1 + ", likertScaleUser2=" + likertScaleUser2
				+ ", relatedTypeId=" + relatedTypeId + ", rank=" + rank + "]";
	}


	public Evaluation(Integer rankId, Integer surveyUserId, Integer likertScale, Timestamp ratingDate) {
		super();
		this.rankId = rankId;
		this.surveyUserId = surveyUserId;
		this.likertScale = likertScale;
		this.ratingDate = ratingDate;
	
	}


	public Evaluation() {
		super();
	}


	public Integer getExternalQuestionId() {
		return externalQuestionId;
	}


	public void setExternalQuestionId(Integer externalQuestionId) {
		this.externalQuestionId = externalQuestionId;
	}


	public Integer getPostId() {
		return postId;
	}


	public void setPostId(Integer postId) {
		this.postId = postId;
	}


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


	public Integer getRelatedTypeId() {
		return relatedTypeId;
	}


	public void setRelatedTypeId(Integer relatedTypeId) {
		this.relatedTypeId = relatedTypeId;
	}


	public Rank getRank() {
		return rank;
	}


	public void setRank(Rank rank) {
		this.rank = rank;
	}


	public Double getMeanLikert() {
		return meanLikert;
	}


	public void setMeanLikert(Double meanLikert) {
		this.meanLikert = meanLikert;
	}


	

	
	
	
}
