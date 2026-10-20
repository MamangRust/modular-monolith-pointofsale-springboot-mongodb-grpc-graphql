package com.sanedge.pointofsale.user.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sanedge.pointofsale.user.models.UserRole;

@Repository
public interface UserRoleRepository extends MongoRepository<UserRole, String> {

    List<UserRole> findByUserId(String userId);

    @Transactional
    void deleteByUserId(String userId);
}
