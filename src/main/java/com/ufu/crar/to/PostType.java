package com.ufu.crar.to;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "posttype")
public class PostType {
	private static final long serialVersionUID = -111652191111815641L;
	    	
	@Id
	@Column(name="postid")
	private Integer postId;
	
	@Column(name="typeid")
	private Integer typeId;
	
	
			
	
	public PostType() {
	}
	
	
	
	public PostType(Integer postId, String classDescription) {
		super();
		this.postId = postId;
		this.typeId = getPostTypeByDescription(classDescription);
		
		
		
	}
	
	public Integer getPostTypeByDescription(String classDescription) {
		if(classDescription.equals("How-to-do-it")) {
			return 1;
		}else if(classDescription.equals("Debug-Corrective")) {
			return 2;
		}
		//else
		return 3;
	}
	
	

	public PostType(Integer postId, Integer classId) {
		super();
		this.postId = postId;
		this.typeId = classId;
	}




	public Integer getPostId() {
		return postId;
	}




	public void setPostId(Integer postId) {
		this.postId = postId;
	}







	public Integer getTypeId() {
		return typeId;
	}




	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		PostType other = (PostType) obj;
		if (postId == null) {
			if (other.postId != null)
				return false;
		} else if (!postId.equals(other.postId))
			return false;
		return true;
	}




	@Override
	public String toString() {
		return "PostType [postId=" + postId + ", typeId=" + typeId + "]";
	}



	public enum PostTypeEnum {
		HOW_TO (((Integer)1).shortValue(),"How-to-do-it"), 
	    DEBUG_CORRECTIVE (((Integer)2).shortValue(),"Debug-corrective"),
	    OTHERS (((Integer)3).shortValue(),"Others");
	  
	   
		private final Short id;
		private final String descricao;
		
		
		PostTypeEnum(Short id,String descricao){
			this.id = id;
			this.descricao = descricao;
		
		}
		
		
		
	    public static PostTypeEnum getPostType(Short id){
	    	switch(id){
	    		case(1): return HOW_TO;
	    		case(2): return DEBUG_CORRECTIVE;
	    		case(3): return OTHERS;
	    	  	}
	    	return null;
	    }
	
		public Short getId() {
			return id;
		}
	
		public String getDescricao() {
			return descricao;
		}
	
	}


	

	
	
    
}