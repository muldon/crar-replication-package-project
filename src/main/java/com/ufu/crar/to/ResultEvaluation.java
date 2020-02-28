package com.ufu.crar.to;

import java.sql.Timestamp;

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
@Table(name = "resultevaluation")
public class ResultEvaluation {
	private static final long serialVersionUID = -121252191111115641L;
	
	@Id
    @SequenceGenerator(name="resulteval_id_seq", sequenceName="resulteval_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="resulteval_id_seq")
	private Integer id;
	
	@Column(name="queryid")
	private Integer queryId;
	
	@Column(name="postsids")
	private String postsIds;
	
	@Column(name="likertvalue")
	private Integer likertValue;
	
	
	private Timestamp date;
	
	@Transient
	private Query query;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	


	

	public Integer getQueryId() {
		return queryId;
	}

	public void setQueryId(Integer queryId) {
		this.queryId = queryId;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public String getPostsIds() {
		return postsIds;
	}

	public void setPostsIds(String postsIds) {
		this.postsIds = postsIds;
	}

	public Integer getLikertValue() {
		return likertValue;
	}

	public void setLikertValue(Integer likertValue) {
		this.likertValue = likertValue;
	}

	

	

	@Override
	public String toString() {
		return "ResultEvaluation [id=" + id + ", queryId=" + queryId + ", postsIds=" + postsIds + ", likertValue="
				+ likertValue + ", date=" + date + ", query=" + query + "]";
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}
	

	
    
}