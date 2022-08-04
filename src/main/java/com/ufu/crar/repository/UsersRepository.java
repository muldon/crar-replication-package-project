package com.ufu.crar.repository;

import org.springframework.data.repository.CrudRepository;

import com.ufu.crar.to.User;



public interface UsersRepository extends CrudRepository<User, Integer> {

	
	
}
