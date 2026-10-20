package com.sanedge.pointofsale.role.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.role.models.Role;

@Repository
public interface RoleQueryRepository extends MongoRepository<Role, String>, RoleQueryRepositoryCustom {
    @Query("""
                { 'deleted_at': null, '$or': [
                    { 'role_name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                ] }
            """)
    Page<Role> findRoles(@Param("keyword") String keyword, Pageable pageable);

    Optional<Role> findByRoleName(String roleName);

    @Query("""
                { 'deleted_at': null, '$or': [
                    { 'role_name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                ] }
            """)
    Page<Role> findActiveRoles(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
                { 'deleted_at': { '$ne': null }, '$or': [
                    { 'role_name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                ] }
            """)
    Page<Role> findTrashedRoles(@Param("keyword") String keyword, Pageable pageable);
}
