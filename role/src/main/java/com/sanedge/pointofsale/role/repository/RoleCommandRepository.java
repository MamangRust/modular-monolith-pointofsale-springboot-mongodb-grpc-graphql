package com.sanedge.pointofsale.role.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.role.models.Role;

@Repository
public interface RoleCommandRepository extends MongoRepository<Role, String>, RoleCommandRepositoryCustom {
}
