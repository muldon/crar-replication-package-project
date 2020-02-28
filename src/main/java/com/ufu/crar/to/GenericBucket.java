package com.ufu.crar.to;

import java.util.List;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GenericBucket {
	private static final long serialVersionUID = -11118291211815641L;
	
	
	@Transient
	protected List<String> twoGrams;
	
	@Transient
	protected List<String> threeGrams;
	
	@Transient
	protected List<String> fourGrams;
	
	@Transient
	protected Integer twoGramsCount;
	
	@Transient
	protected Integer threeGramsCount;
	
	@Transient
	protected Integer fourGramsCount;
	
	@Transient
	protected Integer twoGramsScoreMax;
	
	@Transient
	protected Integer threeGramsScoreMax;
	
	@Transient
	protected Integer fourGramsScoreMax;
	
	@Transient
	protected Double twoGramsScoreNormalized;
	
	@Transient
	protected Double threeGramsScoreNormalized;
	
	@Transient
	protected Double fourGramsScoreNormalized;
	
	
	public void setNGrams(List<String> twoGrams,List<String> threeGrams,List<String> fourGrams) {
		this.twoGrams=twoGrams;
		this.threeGrams=threeGrams;
		this.fourGrams=fourGrams;
	}
	
	public void setNGramsMax(int twoGramsScoreMax, int threeGramsScoreMax, int fourGramsScoreMax) {
		this.twoGramsScoreMax=twoGramsScoreMax;
		this.threeGramsScoreMax=threeGramsScoreMax;
		this.fourGramsScoreMax=fourGramsScoreMax;
	}



	public List<String> getTwoGrams() {
		return twoGrams;
	}


	public void setTwoGrams(List<String> twoGrams) {
		this.twoGrams = twoGrams;
	}


	public List<String> getThreeGrams() {
		return threeGrams;
	}


	public void setThreeGrams(List<String> threeGrams) {
		this.threeGrams = threeGrams;
	}


	public List<String> getFourGrams() {
		return fourGrams;
	}


	public void setFourGrams(List<String> fourGrams) {
		this.fourGrams = fourGrams;
	}


	public Integer getTwoGramsCount() {
		return twoGramsCount;
	}


	public void setTwoGramsCount(Integer twoGramsCount) {
		this.twoGramsCount = twoGramsCount;
	}


	public Integer getThreeGramsCount() {
		return threeGramsCount;
	}


	public void setThreeGramsCount(Integer threeGramsCount) {
		this.threeGramsCount = threeGramsCount;
	}


	public Integer getFourGramsCount() {
		return fourGramsCount;
	}


	public void setFourGramsCount(Integer fourGramsCount) {
		this.fourGramsCount = fourGramsCount;
	}


	public Double getTwoGramsScoreNormalized() {
		return twoGramsScoreNormalized;
	}


	public void setTwoGramsScoreNormalized(Double twoGramsScoreNormalized) {
		this.twoGramsScoreNormalized = twoGramsScoreNormalized;
	}


	public Double getThreeGramsScoreNormalized() {
		return threeGramsScoreNormalized;
	}


	public void setThreeGramsScoreNormalized(Double threeGramsScoreNormalized) {
		this.threeGramsScoreNormalized = threeGramsScoreNormalized;
	}


	public Double getFourGramsScoreNormalized() {
		return fourGramsScoreNormalized;
	}


	public void setFourGramsScoreNormalized(Double fourGramsScoreNormalized) {
		this.fourGramsScoreNormalized = fourGramsScoreNormalized;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public Integer getTwoGramsScoreMax() {
		return twoGramsScoreMax;
	}


	public void setTwoGramsScoreMax(Integer twoGramsScoreMax) {
		this.twoGramsScoreMax = twoGramsScoreMax;
	}


	public Integer getThreeGramsScoreMax() {
		return threeGramsScoreMax;
	}


	public void setThreeGramsScoreMax(Integer threeGramsScoreMax) {
		this.threeGramsScoreMax = threeGramsScoreMax;
	}


	public Integer getFourGramsScoreMax() {
		return fourGramsScoreMax;
	}


	public void setFourGramsScoreMax(Integer fourGramsScoreMax) {
		this.fourGramsScoreMax = fourGramsScoreMax;
	}
	
	


	
    
}