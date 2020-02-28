package com.ufu.crar.to;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Type;

import com.ufu.crar.to.PostType.PostTypeEnum;

@XmlRootElement
@Entity
@Table(name = "wordvectors")
public class SoContentWordVector {
	private static final long serialVersionUID = -111652191111815641L;
	    	
	@Id
    @SequenceGenerator(name="wordvectors_id_seq", sequenceName="wordvectors_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="wordvectors_id_seq")
	private Integer id;
	
	@Column(name="tagid")
	private Integer tagId;
	
	@Column(name="word")
	private String word;
	
	@Column(name="vectors")
	private String vectors;
	
	private Double idf;	
	
	@Transient
	private double[] fastTextVectorsValues;
	
	
	
	public SoContentWordVector(Integer tagId, String word, String vectors, Double idf,double[] fastTextVectorsValues, Integer id) {
		super();
		this.tagId = tagId;
		this.word = word;
		this.vectors=vectors;
		this.idf = idf;
		this.fastTextVectorsValues=fastTextVectorsValues;
		this.id = id;
	}


	public SoContentWordVector() {
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getTagId() {
		return tagId;
	}


	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}


	public String getWord() {
		return word;
	}


	public void setWord(String word) {
		this.word = word;
	}

	


	public static long getSerialversionuid() {
		return serialVersionUID;
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
		SoContentWordVector other = (SoContentWordVector) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	



	public String getVectors() {
		return vectors;
	}


	public void setVectors(String vectors) {
		this.vectors = vectors;
	}


	public Double getIdf() {
		return idf;
	}


	public void setIdf(Double idf) {
		this.idf = idf;
	}


	@Override
	public String toString() {
		return "SoContentWordVector [id=" + id + ", tagId=" + tagId + ", word=" + word + "]";
	}


	


	public double[] getFastTextVectorsValues() {
		return fastTextVectorsValues;
	}


	public void setFastTextVectorsValues(double[] fastTextVectorsValues) {
		this.fastTextVectorsValues = fastTextVectorsValues;
	}


	

	public enum VectorsType {
		FAST_TEXT(1), 
	    SENTENCE2VEC(2);
	  
	   private final Integer id;
		
		
	   VectorsType(Integer id){
			this.id = id;
		}
		
		
		
	    public static VectorsType getPostType(Integer id){
	    	switch(id){
	    		case(1): return FAST_TEXT;
	    		case(2): return SENTENCE2VEC;
	    	  	}
	    	return null;
	    }
	
		public Integer getId() {
			return id;
		}
	
	
	}



	

	
	
    
}