package com.ufu.crar.util;

import org.apache.lucene.search.similarities.Similarity;

public class SearcherParams {
	
	private String trm;
	
	//bm25 params
	private float k; 
	private float b;
	
	//LMJelinek params
	private float lambda;
	
	private Similarity similarity;
	
	

	public SearcherParams() {
		super();
	}
	
	

	public SearcherParams(String trm, Similarity similarity) {
		super();
		this.trm = trm;
		this.similarity = similarity;
	}


	public SearcherParams(String trm, Similarity similarity, float k, float b) {
		super();
		this.trm = trm;
		this.similarity = similarity;
		this.k=k;
		this.b=b;
	}
	
	public SearcherParams(String trm, Similarity similarity, float lambda) {
		super();
		this.trm = trm;
		this.similarity = similarity;
		this.lambda=lambda;
	}


	public float getK() {
		return k;
	}

	public void setK(float k) {
		this.k = k;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	public float getLambda() {
		return lambda;
	}

	public void setLambda(float lambda) {
		this.lambda = lambda;
	}

	@Override
	public String toString() {
		//return " [trm= "+trm+", k=" + k + ", b=" + b + ", lambda=" + lambda + "]";
		return " [trm= "+trm+", k=" + k + ", b=" + b + "]";
	}

	public String getTrm() {
		return trm;
	}

	public void setTrm(String trm) {
		this.trm = trm;
	}

	public Similarity getSimilarity() {
		return similarity;
	}

	public void setSimilarity(Similarity similarity) {
		this.similarity = similarity;
	}
	
	
	
	
	
}
