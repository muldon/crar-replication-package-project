package com.ufu.crar.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ufu.crar.to.SoContentWordVector;



public interface SoContentWordVectorsRepository extends CrudRepository<SoContentWordVector, Integer> {

	List<SoContentWordVector> findByTagId(Integer tagId);

	@Query(value="select w.word "
			  + " from wordvectors w"
			  + " where tagid=?1",nativeQuery=true)
	Set<String> loadAllWordsOfVocabulary(int tagid);

	@Query(value="update wordvectors w"
			  + " set senttwovecvectors=?2 where id=?1",nativeQuery=true)
	void updateSentence2Vec(Integer id, String sent2vecVectors);

	
	
}
