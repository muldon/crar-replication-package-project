package com.ufu.crar.to;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Pair {

	protected Integer postId1; //nonMaster
	
	protected Integer postId2; //Master
	
	protected String topics1;
	
	protected String topics2;
	
	protected String hotTopics1;
	
	protected String hotTopics2;
	
	protected double topicProximity;
		
	private String nonMasterProcessedTitle;
	
	private String masterProcessedTitle;
	
	private String nonMasterProcessedBody;
	
	private String masterProcessedBody;
	

	public Integer getPostId1() {
		return postId1;
	}

	public void setPostId1(Integer postId1) {
		this.postId1 = postId1;
	}

	public Integer getPostId2() {
		return postId2;
	}

	public void setPostId2(Integer postId2) {
		this.postId2 = postId2;
	}

	public double getTopicProximity() {
		return topicProximity;
	}

	public void setTopicProximity(double topicProximity) {
		this.topicProximity = topicProximity;
	}

	public Pair() {
		super();
	}

	


	public Pair(Integer postId1, Integer postId2, String topics1, String topics2, String hotTopics1, String hotTopics2) {
		super();
		this.postId1 = postId1;
		this.postId2 = postId2;
		this.topics1 = topics1;
		this.topics2 = topics2;
		this.hotTopics1 = hotTopics1;
		this.hotTopics2 = hotTopics2;
	}

	public String getTopics1() {
		return topics1;
	}

	public void setTopics1(String topics1) {
		this.topics1 = topics1;
	}

	public String getTopics2() {
		return topics2;
	}

	public void setTopics2(String topics2) {
		this.topics2 = topics2;
	}

	public String getHotTopics1() {
		return hotTopics1;
	}

	public void setHotTopics1(String hotTopics1) {
		this.hotTopics1 = hotTopics1;
	}

	public String getHotTopics2() {
		return hotTopics2;
	}

	public void setHotTopics2(String hotTopics2) {
		this.hotTopics2 = hotTopics2;
	}

	public String getNonMasterProcessedTitle() {
		return nonMasterProcessedTitle;
	}

	public void setNonMasterProcessedTitle(String nonMasterProcessedTitle) {
		this.nonMasterProcessedTitle = nonMasterProcessedTitle;
	}

	public String getMasterProcessedTitle() {
		return masterProcessedTitle;
	}

	public void setMasterProcessedTitle(String masterProcessedTitle) {
		this.masterProcessedTitle = masterProcessedTitle;
	}

	public String getNonMasterProcessedBody() {
		return nonMasterProcessedBody;
	}

	public void setNonMasterProcessedBody(String nonMasterProcessedBody) {
		this.nonMasterProcessedBody = nonMasterProcessedBody;
	}

	public String getMasterProcessedBody() {
		return masterProcessedBody;
	}

	public void setMasterProcessedBody(String masterProcessedBody) {
		this.masterProcessedBody = masterProcessedBody;
	}
	
		
}
