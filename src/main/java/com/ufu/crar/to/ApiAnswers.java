package com.ufu.crar.to;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Type;

@XmlRootElement
@Entity
@Table(name = "apianswers")
public class ApiAnswers {
	private static final long serialVersionUID = -111152191111815641L;
	    	
	@Id
    @SequenceGenerator(name="apianswers_id_seq", sequenceName="apianswers_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="apianswers_id_seq")
	private Integer id;

	private String api;
	
	@Column(name="answersids")
	private String answersIds;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getAnswersIds() {
		return answersIds;
	}

	public void setAnswersIds(String answersIds) {
		this.answersIds = answersIds;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "ApiAnswers [id=" + id + ", api=" + api + ", answersIds=" + answersIds + "]";
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
		ApiAnswers other = (ApiAnswers) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public ApiAnswers(String api, String answersIds) {
		super();
		this.api = api;
		this.answersIds = answersIds;
	}

	public ApiAnswers() {
		super();
	}	
	
	
	


	

	
	
    
}