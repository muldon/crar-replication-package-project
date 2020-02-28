package com.ufu.crar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ufu.crar.to.ResultEvaluation;

public interface ResultEvaluationRepository extends CrudRepository<ResultEvaluation, Integer> {

	@Query(value="select r.* " + 
			" from resultevaluation r, query q" + 
			" where r.queryid = q.id" + 
			" and q.querytext = ?1" + 
			" order by r.id " + 
			" desc limit 1",nativeQuery=true)
	List<ResultEvaluation> findLastEvaluation(String validEvaluatedNotDupeQuery);

	@Query(value="select r.* "
			  + " from resultevaluation r"
			  + " where r.queryid < ?1"
			  + " order by r.id",nativeQuery=true)
	List<ResultEvaluation> findWithQueryIdLowerThan(int idLimit);

	

	
	
}
