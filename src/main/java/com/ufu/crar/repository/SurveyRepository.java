package com.ufu.crar.repository;

import org.springframework.data.repository.CrudRepository;

import com.ufu.crar.to.Survey;



public interface SurveyRepository extends CrudRepository<Survey, Integer> {

	Survey findByInternalSurvey(boolean active);
    
	

	
}
