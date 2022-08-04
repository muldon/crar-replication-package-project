package com.ufu.crar.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.ufu.crar.to.Query;





public interface QueryRepository extends CrudRepository<Query, Integer> {

	@org.springframework.data.jpa.repository.Query(value="select q.* "
			  + " from query q"
			  + " where q.id< ?1",nativeQuery=true)
	List<Query> findWithIdLowerThan(int idLimit);

		
	
}
