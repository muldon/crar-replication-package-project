package com.ufu.crar.repository;

import org.springframework.data.repository.CrudRepository;

import com.ufu.crar.to.MetricResult;

public interface MetricResultRepository extends CrudRepository<MetricResult, Integer> {

	//Rank findByExternalQuestionIdAndPostIdAndInternalEvaluation(Integer externalQuestionId, Integer postId, boolean isInternalSurveyUser);

	

	
	
}
