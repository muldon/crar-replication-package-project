package com.ufu.crar.to;

import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class PostRestTransfer extends GenericRestTransfer{
	@Expose
	public List<Post> posts;
	public Set<String> tags;
	public Integer queryId;
	public List<Integer> recommendedIds;
	@Expose
	public Set<Bucket> buckets;
	@Expose
	public Double similarity;
	@Expose
	public Set<ThreadContent> threads;

	public PostRestTransfer(List<Post> posts,Set<Bucket> buckets, Set<String> tags,String descricao, Integer queryId, List<Integer> recommendedIds, String infoMessage, String errorMessage)
	{
		this.posts = posts;
		this.errorMessage = errorMessage;
		this.infoMessage = infoMessage;
		this.descricao = descricao;
		this.queryId = queryId;
		this.tags= tags;
		this.recommendedIds=  recommendedIds;
		this.buckets=buckets;
		
	}
	
	
	




	public PostRestTransfer(String errorMessage, String infoMessage, Set<ThreadContent> threads,Set<Bucket> buckets) {
		super(errorMessage, infoMessage);
		this.threads = threads;
		this.buckets = buckets;
	}







	public PostRestTransfer(Integer id, String descricao, String infoMessage, String errorMessage, Double similarity) {
		super(id, descricao, infoMessage, errorMessage);
		this.similarity = similarity;
	}



	public PostRestTransfer() {
		this.descricao=null;
		this.infoMessage=null;
		this.id=null;
		this.errorMessage=null;
	}
	

	public Integer getId() {
		return id;
	}


	public String getErrorMessage() {
		return errorMessage;
	}


	public String getInfoMessage() {
		return infoMessage;
	}


	public String getDescricao() {
		return descricao;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public Integer getQueryId() {
		return queryId;
	}

	public void setQueryId(Integer queryId) {
		this.queryId = queryId;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public List<Integer> getRecommendedIds() {
		return recommendedIds;
	}

	public void setRecommendedIds(List<Integer> recommendedIds) {
		this.recommendedIds = recommendedIds;
	}

	public Set<Bucket> getBuckets() {
		return buckets;
	}

	public void setBuckets(Set<Bucket> buckets) {
		this.buckets = buckets;
	}



	public Double getSimilarity() {
		return similarity;
	}



	public void setSimilarity(Double similarity) {
		this.similarity = similarity;
	}




	public Set<ThreadContent> getThreads() {
		return threads;
	}




	public void setThreads(Set<ThreadContent> threads) {
		this.threads = threads;
	}
	
	
	

}