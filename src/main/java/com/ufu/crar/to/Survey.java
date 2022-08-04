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
@Table(name = "survey")
public class Survey {
	private static final long serialVersionUID = -121252191111815641L;
	@Id
    @SequenceGenerator(name="survey_id", sequenceName="survey_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="survey_id")
    private Integer id;
	
	private String name;
	
	private String description;
	
	@Column(name="internalsurvey")
	private Boolean internalSurvey;
	
	
	public Survey(SurveyEnum surveyEnum) {
		this.id = surveyEnum.getId();
		this.name = surveyEnum.getName();
	}
	
	
	

	public Survey() {
		super();
	}




	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		Survey other = (Survey) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
	
	

	@Override
	public String toString() {
		return "Survey [id=" + id + ", name=" + name + ", description=" + description + ", internal=" + internalSurvey + "]";
	}








	public Boolean getInternalSurvey() {
		return internalSurvey;
	}




	public void setInternalSurvey(Boolean internalSurvey) {
		this.internalSurvey = internalSurvey;
	}








	public enum SurveyEnum {
		BUILDING_GROUND_TRUTH (1,"Building ground truth"), 
	    DEVELOPERS_ASSESSMENT(2,"Developers assessment");
		
	   
		private final Integer id;
		private final String name;
		
		
		
		SurveyEnum(Integer id,String name){
			this.id = id;
			this.name = name;
		
		}
		
		SurveyEnum(SurveyEnum surveyEnum){
			this.id = surveyEnum.id;
			this.name = surveyEnum.name;
		}
		
	    public static SurveyEnum getTipoUnidade(Short id){
	    	switch(id){
	    		case(1): return BUILDING_GROUND_TRUTH;
	    		case(2): return DEVELOPERS_ASSESSMENT;
	    		
	    	}
	    	return null;
	    }
	
		public Integer getId() {
			return id;
		}
	
		public String getName() {
			return name;
		}
	
	
	}	
	
    
}