package com.sanedge.pointofsale.auth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.auth.models.RefreshToken;

@Repository
public interface RefreshTokenCommandRepository extends MongoRepository<RefreshToken, String> {
        @Query(value = """
                        { 'token': ?0 }
                        """, delete = true)
        int deleteByToken(@Param("token") String token);

        @Query(value = """
                        { 'user_id': ?0 }
                        """, delete = true)
        int deleteByUserId(@Param("userId") Integer userId);
}
