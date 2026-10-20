package com.sanedge.pointofsale.user.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.user.models.User;

@Repository
public interface UserQueryRepository extends MongoRepository<User, String> {
  @Query("""
          { 'deleted_at': null, '$or': [
              { 'firstname': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
              { 'lastname': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
              { 'email': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
          ] }
      """)
  Page<User> findAllUsers(@Param("keyword") String keyword, Pageable pageable);

  @Query("""
          { 'deleted_at': null, '$or': [
              { 'firstname': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
              { 'lastname': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
              { 'email': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
          ] }
      """)
  Page<User> findActiveUsers(@Param("keyword") String keyword, Pageable pageable);

  @Query("""
          { 'deleted_at': { '$ne': null }, '$or': [
              { 'firstname': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
              { 'lastname': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
              { 'email': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
          ] }
      """)
  Page<User> findTrashedUsers(@Param("keyword") String keyword, Pageable pageable);

  @Query("{ '_id': ?0, 'deleted_at': null }")
  Optional<User> findByUserId(@Param("userId") String userId);

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

}
