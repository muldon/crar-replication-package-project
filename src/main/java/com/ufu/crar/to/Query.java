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
@Table(name = "query")
public class Query {
	private static final long serialVersionUID = -121252191111815641L;
	
	@Id
    @SequenceGenerator(name="query_id_seq", sequenceName="query_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="query_id_seq")
	private Integer id;
	
	@Column(name="querytext")
	private String queryText;
	
	@Column(name="numberofcomposedanswers")
	private Integer numberOfComposedAnswers;
	
	@Column(name="ipaddress")
	private String ipAddress;
	
	private Timestamp date;
	
	
	@Transient
	private Boolean reduceSentences;
	
	@Transient
	private Integer trmLimit1;
	
	@Transient
	private Integer trmLimit2;
	
	@Transient
	private Integer trmLimit3;
	
	@Transient
	private Integer tagId;
	
	@Transient
	private String comparingContent;
	
	@Transient
	private Integer vectorsTypeId;
	
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	

	public String getQueryText() {
		return queryText;
	}

	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}

	public Integer getNumberOfComposedAnswers() {
		return numberOfComposedAnswers;
	}

	public void setNumberOfComposedAnswers(Integer numberOfComposedAnswers) {
		this.numberOfComposedAnswers = numberOfComposedAnswers;
	}

	

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	

	@Override
	public String toString() {
		return "Query [id=" + id + ", query=" + queryText + ", numberOfComposedAnswers=" + numberOfComposedAnswers
				+ ", ipAddress=" + ipAddress + ", date=" + date + "]";
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public Boolean getReduceSentences() {
		return reduceSentences;
	}

	public void setReduceSentences(Boolean reduceSentences) {
		this.reduceSentences = reduceSentences;
	}


	public Integer getTagId() {
		return tagId;
	}

	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}

	public String getComparingContent() {
		return comparingContent;
	}

	public void setComparingContent(String comparingContent) {
		this.comparingContent = comparingContent;
	}

	public Integer getTrmLimit1() {
		return trmLimit1;
	}

	public void setTrmLimit1(Integer trmLimit1) {
		this.trmLimit1 = trmLimit1;
	}

	public Integer getTrmLimit2() {
		return trmLimit2;
	}

	public void setTrmLimit2(Integer trmLimit2) {
		this.trmLimit2 = trmLimit2;
	}

	public Integer getTrmLimit3() {
		return trmLimit3;
	}

	public void setTrmLimit3(Integer trmLimit3) {
		this.trmLimit3 = trmLimit3;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Integer getVectorsTypeId() {
		return vectorsTypeId;
	}

	public void setVectorsTypeId(Integer vectorsTypeId) {
		this.vectorsTypeId = vectorsTypeId;
	}
	

	
    
}