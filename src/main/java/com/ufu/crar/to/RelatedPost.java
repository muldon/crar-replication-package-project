package com.ufu.crar.to;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.ufu.crar.to.PostType.PostTypeEnum;

@XmlRootElement
@Entity
@Table(name = "relatedpost")
public class RelatedPost {
	private static final long serialVersionUID = -151652190111815641L;
	@Id
    @SequenceGenerator(name="relatedpost_id_seq", sequenceName="relatedpost_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="relatedpost_id_seq")
	private Integer id;
	
	@Column(name="postid")
	private Integer postId;
	
	@Column(name="externalquestionid")
	private Integer externalQuestionId;
	
	@Column(name="relationtypeid")
	private Integer relationTypeId;
	
		
	
	public RelatedPost() {
	}

	


	public RelatedPost(Integer postId, Integer externalQuestionId, Integer relationTypeId) {
		super();
		this.postId = postId;
		this.externalQuestionId = externalQuestionId;
		this.relationTypeId = relationTypeId;
	}




	public Integer getId() {
		return id;
	}




	public void setId(Integer id) {
		this.id = id;
	}




	public Integer getPostId() {
		return postId;
	}




	public void setPostId(Integer postId) {
		this.postId = postId;
	}




	public Integer getExternalQuestionId() {
		return externalQuestionId;
	}




	public void setExternalQuestionId(Integer externalQuestionId) {
		this.externalQuestionId = externalQuestionId;
	}




	public static long getSerialversionuid() {
		return serialVersionUID;
	}







	@Override
	public String toString() {
		return "RelatedPost [id=" + id + ", postId=" + postId + ", externalQuestionId=" + externalQuestionId
				+ ", relationTypeId=" + relationTypeId + "]";
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}




	public Integer getRelationTypeId() {
		return relationTypeId;
	}




	public void setRelationTypeId(Integer relationTypeId) {
		this.relationTypeId = relationTypeId;
	}




	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelatedPost other = (RelatedPost) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
		
	

	public enum RelationTypeEnum {
		FROM_GOOGLE_QUESTION_T1 (1), 
	    RELATED_DUPE_T2 (2),
	    RELATED_NOT_DUPE_T3 (3),	    
		FROM_GOOGLE_ANSWER_T4 (4),
		FROM_GOOGLE_QUESTION_OR_ANSWER_T5 (5),
		LINKS_INSIDE_TEXTS_T6 (5);
	   
		private final Integer id;
						
		RelationTypeEnum(Integer id){
			this.id = id;
		}

		public Integer getId() {
			return id;
		}
		
		
					
	}
    
}