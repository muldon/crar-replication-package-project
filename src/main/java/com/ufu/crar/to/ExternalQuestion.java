package com.ufu.crar.to;

import java.util.List;
import java.util.Map;

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
@Table(name = "externalquestion")
public class ExternalQuestion {
	@Id
    @SequenceGenerator(name="externalquestion_id_seq", sequenceName="externalquestion_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="externalquestion_id_seq")
	private Integer id;
	
	@Column(name="rawquery")	
	private String rawQuery;

	@Column(name="googlequery")	
	private String googleQuery;
	
	private String classes;
	
	@Column(name="userack")	
	private Boolean useRack;
	
	private String obs;
	
	private String url;
	
	@Column(name="filereferenceid")	
	private Integer fileReferenceId;
	
	//@Transient
	//private Map<Integer,List<Post>> relatedPosts;
		

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	

	public String getGoogleQuery() {
		return googleQuery;
	}

	public void setGoogleQuery(String googleQuery) {
		this.googleQuery = googleQuery;
	}

	public String getClasses() {
		return classes;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}

	public Boolean getUseRack() {
		return useRack;
	}

	public void setUseRack(Boolean useRack) {
		this.useRack = useRack;
	}

	


	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
		ExternalQuestion other = (ExternalQuestion) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	

	@Override
	public String toString() {
		return "ExternalQuestion [id=" + id + ", rawQuery=" + rawQuery + ", googleQuery=" + googleQuery + ", classes="
				+ classes + ", useRack=" + useRack + ", obs=" + obs + ", url=" + url + ", fileReferenceId=" + fileReferenceId
				+ "]";
	}

	public ExternalQuestion(Integer fileReferenceId,String rawQuery, String googleQuery, String classes, Boolean useRack, String obs, String link) {
		super();
		this.googleQuery = googleQuery;
		this.classes = classes;
		this.useRack = useRack;
		this.obs =obs;
		this.url=link;
		this.fileReferenceId=fileReferenceId;
		this.rawQuery=rawQuery;
	}

	public ExternalQuestion() {
		super();
	}
	
	

	public ExternalQuestion(Integer fileReferenceId,String rawQuery) {
		super();
		this.rawQuery = rawQuery;
		this.fileReferenceId = fileReferenceId;
	}

	

	public Integer getFileReferenceId() {
		return fileReferenceId;
	}

	public void setFileReferenceId(Integer fileReferenceId) {
		this.fileReferenceId = fileReferenceId;
	}

	public String getRawQuery() {
		return rawQuery;
	}

	public void setRawQuery(String rawQuery) {
		this.rawQuery = rawQuery;
	}

	/*public Map<Integer, List<Post>> getRelatedPosts() {
		return relatedPosts;
	}

	public void setRelatedPosts(Map<Integer, List<Post>> relatedPosts) {
		this.relatedPosts = relatedPosts;
	}*/

	

	

	
	
		
	
	
	
	
	
}
