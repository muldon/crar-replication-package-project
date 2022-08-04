package com.ufu.crar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ufu.crar.to.Evaluation;

public interface EvaluationRepository extends CrudRepository<Evaluation, Integer> {

	@Query(value="select e.* "
			  + " from evaluation e, rank r"
			  + " where r.id = e.rankid"
			  + " and r.phase = ?  "
			  + " and e.surveyuserid = ?",nativeQuery=true)
	List<Evaluation> findByPhaseUser(Integer phase, Integer userId);

	List<Evaluation> findByRankId(Integer rankId);

	@Query(value="select e.*" + 
			" from relatedpost rp, rank r, evaluation e" + 
			" where rp.id=r.relatedpostid" + 
			" and e.rankid = r.id" + 
			" and r.phase = ?1" + 
			" and e.surveyuserid = ?2" + 
			" and rp.externalquestionid = ?3",nativeQuery=true)
	List<Evaluation> getEvaluationsByUserExternalQuestionPhase(int phaseNum, Integer userId, Integer id);

	//Evaluation findByExternalQuestionIdAndPostIdAndSurveyUserId(Integer id, Integer id2, Integer userId);
    
	

	
}
