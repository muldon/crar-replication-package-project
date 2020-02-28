package com.ufu.crar.repository;

import org.springframework.data.repository.CrudRepository;

import com.ufu.crar.to.SurveyUser;



public interface SurveyUserRepository extends CrudRepository<SurveyUser, Integer> {

	SurveyUser findByLogin(String login);
    
	

	
}
