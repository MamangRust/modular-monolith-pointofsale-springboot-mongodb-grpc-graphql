package com.sanedge.pointofsale.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.user.models.User;

@Repository
public interface UserCommandRepository extends MongoRepository<User, String>, UserCommandRepositoryCustom {

}
