package com.sanedge.pointofsale.auth.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.auth.models.RefreshToken;

@Repository
public interface RefreshTokenQueryRepository extends MongoRepository<RefreshToken, String> {

        @Query("{ 'user_id': ?0, 'deleted_at': null }")
        Optional<RefreshToken> findByUserId(@Param("userId") Integer userId);

        @Query("{ 'token': ?0, 'deleted_at': null }")
        Optional<RefreshToken> findByToken(@Param("token") String token);
}
