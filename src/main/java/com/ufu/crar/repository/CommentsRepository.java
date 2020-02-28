package com.ufu.crar.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import com.ufu.crar.to.Comment;



public interface CommentsRepository extends CrudRepository<Comment, Integer> {

	List<Comment> findByPostId(Integer postId, Sort sort);
    
	

	
}
