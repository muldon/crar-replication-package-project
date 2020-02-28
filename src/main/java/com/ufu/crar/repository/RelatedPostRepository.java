package com.ufu.crar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ufu.crar.to.Post;
import com.ufu.crar.to.RelatedPost;

public interface RelatedPostRepository extends CrudRepository<RelatedPost, Integer> {

	@Query(value="select p.* "
			  + " from postsmin p, relatedpost r"
			  + " where p.id = r.postid "
			  + " and r.externalquestionid = ?1",nativeQuery=true)
	List<Post> findRelatedPosts(Integer externalQuestionId);

	@Query(value="select r.postid "
			  + " from relatedpost r"
			  + " where r.externalquestionid = ?1",nativeQuery=true)
	List<Integer> findRelatedPostsIds(Integer externalQuestionId);

	List<RelatedPost> findByExternalQuestionId(Integer externalQuestionId);

	RelatedPost findByExternalQuestionIdAndPostId(Integer externalQuestionId, Integer postId);

	@Query(value=" select rp.*" + 
			" from relatedpost rp, rank r" + 
			" where rp.id=r.relatedpostid" + 
			" and rp.externalquestionid = ?1" + 
			" and r.phase = ?2",nativeQuery=true)
	List<RelatedPost> findRelatedPostsByExternalQuestionIdAndPhase(Integer externalQuestionId, int phase);
    
	

	
}
