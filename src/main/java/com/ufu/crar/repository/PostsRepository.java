package com.ufu.crar.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ufu.crar.to.Post;



public interface PostsRepository extends CrudRepository<Post, Integer> {

	List<Post> findByParentIdAndPostTypeId(Integer questionId, Integer postTypeId, Sort sort);

	List<Post> findByParentId(Integer questionId, Sort sort);

	@Query(value="select p.* "
			  + " from postsmin p"
			  + " where p.parentid = ?1"
			  + " and p.score>0  "
			  + " order by p.id",nativeQuery=true)
	List<Post> findUpVotedAnswersByQuestionId(Integer questionId);

	@Query(value="select p.* "
			  + " from postsmin p"
			  + " where p.parentid = ?1"
			  + " and p.score>0  "
			  + " and p.processedcode!= ''"
			  + " and p.processedbody!= ''"
			  + " order by p.id",nativeQuery=true)
	List<Post> findUpVotedAnswersWithCodeByQuestionId(Integer id);

	
	@Query(value="select p.id "
			  + " from postsmin p"
			  + " where p.posttypeid = 1",nativeQuery=true)
	List<Integer> findAllQuestionsIds();

	@Query(value="select p.id "
			  + " from postsmin p",nativeQuery=true)
	List<Integer> findAllPostsIds();

	
	 
	 @Query(value="select id " + 
	 		"	 from postsmin " + 
	 		"	 where parentid = ?" + 
	 		"	 order by score desc" + 
	 		"	 limit 1",nativeQuery=true)
	 List<Integer> getMostUpvotedAnswerForQuestionId(Integer questionId);
	 
	 @Query(value="select * " + 
		 		"	 from postsmin " + 
		 		"	 where parentid = ?" + 
		 		"	 order by score desc" + 
		 		"	 limit 1",nativeQuery=true)
		 List<Post> getMostUpvotedAnswerForQuestionId2(Integer questionId);

	 @Query(value="select * " + 
		 		"	 from postsmin " + 
		 		"	 where parentid = ?1" + 
		 		"    and id!= ?2"+
		 		"	 order by score desc" + 
		 		"	 limit 1",nativeQuery=true)
	 List<Post> getMostUpvotedAnswerForQuestion(Integer questionId,Integer currentAnswerId);
	 
	 @Query(value="select * " + 
		 		"	 from postsmin " + 
		 		"	 where parentid = ?1" +
		 		"    and processedcode!='' "+
		 		"    and score>0"+
		 		"	 order by score desc" + 
		 		"	 limit 1",nativeQuery=true)
		List<Post> getMostUpvotedAnswerWithCodeForQuestion(Integer questionId);

	@Query(value=" select id " + 
		 		" from postsmin " + 
		 		"  where tagid = ?", nativeQuery=true)
	List<Integer> findAllPostsIdsByTagId(Integer tagId);

	@Modifying
	@Query(value=" update postsmin set sentencevectors = ?2 where id=?1", nativeQuery=true)
	void updateSentenceVectors(Integer id, String vectorsContent);
	
	

	
}
