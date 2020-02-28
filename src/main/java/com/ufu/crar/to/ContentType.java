package com.ufu.crar.to;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;


public class ContentType {
	private static final long serialVersionUID = -111152191111815641L;
	
	private Integer id;
	
	private String description;
	
	
			
	
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
		ContentType other = (ContentType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}





	public Integer getId() {
		return id;
	}





	public void setId(Integer id) {
		this.id = id;
	}





	public String getDescription() {
		return description;
	}





	public void setDescription(String description) {
		this.description = description;
	}





	public ContentType() {
	}
	
	



	public enum ContentTypeEnum {
		title (1,"Title"),
		title_questionBody_body_code (3,"Title + body + code"),
		title_questionBody_body (7,"Title + quesiton body + body"),
		title_body (5,"Title + body"),
		code (4,"code"),
		parentCode_code (8,"parent code + code"),
		question_body_answers_body (6,"Question body + Answers body"),
		body (11,"processed body lemma"),
		body_code (15,"processed body lemma + code"),
		parentTitleLemma_BodyLemma (22,"parent title lemma + body lemma"),
	    parentTitleLemma_parentBodyLemma_answerBodyLemma (33,"Parent title lemma + parent body lemma + answer body lemma");
		
		
		private final Integer id;
		private final String descricao;
		
		
		ContentTypeEnum(Integer id,String descricao){
			this.id = id;
			this.descricao = descricao;
		
		}
		
		
		
	    public static ContentTypeEnum getPostType(Integer id){
	    	switch(id){
	    		case(1): return title;
	    		case(3): return title_questionBody_body_code;
	    		case(4): return code;
	    		case(5): return title_body;
	    		case(6): return question_body_answers_body;
	    		case(7): return title_questionBody_body;
	    		case(8): return parentCode_code;
	    		case(11): return body;
	    		case(15): return body_code;
	    		case(22): return parentTitleLemma_BodyLemma;
	    		case(33): return parentTitleLemma_parentBodyLemma_answerBodyLemma;
	    	  	}
	    	return null;
	    }
	
		public Integer getId() {
			return id;
		}
	
		public String getDescricao() {
			return descricao;
		}
	
	}


	

	
	
    
}