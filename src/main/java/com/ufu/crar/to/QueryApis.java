package com.ufu.crar.to;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class QueryApis {
	private String query;
	
	private Set<String> apis;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Set<String> getApis() {
		return apis;
	}

	public void setApis(Set<String> apis) {
		this.apis = apis;
	}

	public QueryApis(String query, Set<String> apis) {
		super();
		this.query = query;
		this.apis = apis;
	}

	@Override
	public String toString() {
		return "QueryApis [query=" + query + ", apis=" + apis + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((query == null) ? 0 : query.hashCode());
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
		QueryApis other = (QueryApis) obj;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		return true;
	}
	
	

	
	

	
	
    
}