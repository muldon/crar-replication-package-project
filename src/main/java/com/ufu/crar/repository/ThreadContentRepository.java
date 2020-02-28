package com.ufu.crar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ufu.crar.to.ThreadContent;



public interface ThreadContentRepository extends CrudRepository<ThreadContent, Integer> {

	List<ThreadContent> findByTagIdOrderById(int tagId);

	@Modifying
	@Query(value=" update threadscontents set sentencevectors = ?2 where id=?1", nativeQuery=true)
	void updateSentenceVectors(Integer id, String vectorsContent);
		
	
}
