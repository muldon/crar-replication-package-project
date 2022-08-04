package com.ufu.crar.to;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.ufu.crar.to.PostType.PostTypeEnum;

@XmlRootElement
@Entity
@Table(name = "tags")
public class Tags {
	private static final long serialVersionUID = -111652190111805641L;
	@Id
    private Integer id;
    	
	@Column(name="tagname")
    private String tagName;
			
	@Column(name="count")
	private Integer count;
	
	@Column(name="excerptpostid")
	private Integer excerptPostId;
	
	@Column(name="wikipostid")
	private Integer wikiPostId;
	
	
	
	public Tags() {
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
		Tags other = (Tags) obj;
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

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getExcerptPostId() {
		return excerptPostId;
	}

	public void setExcerptPostId(Integer excerptPostId) {
		this.excerptPostId = excerptPostId;
	}

	public Integer getWikiPostId() {
		return wikiPostId;
	}

	public void setWikiPostId(Integer wikiPostId) {
		this.wikiPostId = wikiPostId;
	}

	@Override
	public String toString() {
		return "Tags [id=" + id + ", tagName=" + tagName + ", count=" + count + ", excerptPostId=" + excerptPostId
				+ ", wikiPostId=" + wikiPostId + "]";
	}

	

	public enum TagEnum {
		Java (1,"java"), 
	    PHP (2,"php"),
	    Python (3,"python");
	  
	   
		private final Integer id;
		private final String descricao;
				
		
		TagEnum(Integer id,String descricao){
			this.id = id;
			this.descricao = descricao;
		
		}
		
		
		public static Integer getTagIdByTagEnum(TagEnum tagEnum) {
			int id=0;
			if(tagEnum.equals(TagEnum.Java)) {
				id= TagEnum.Java.getId();
			}else if(tagEnum.equals(TagEnum.Python)) {
				id= TagEnum.Python.getId();
			}else 
				id = tagEnum.PHP.getId();
			return id;
		}		
		
	    public static TagEnum getPostType(Integer id){
	    	switch(id){
	    		case(1): return Java;
	    		case(2): return PHP;
	    		case(3): return Python;
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