package com.ufu.crar.to;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ExternalQuery {
	private Integer id;
	private String query;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	@Override
	public String toString() {
		return "ExternalQuery [id=" + id + ", query=" + query + "]";
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
		ExternalQuery other = (ExternalQuery) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public ExternalQuery(Integer id) {
		super();
		this.id = id;
	}

	

	
	
		
	
	
	
	
	
}
