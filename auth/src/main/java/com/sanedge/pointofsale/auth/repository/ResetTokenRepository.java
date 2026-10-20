package com.sanedge.pointofsale.auth.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.auth.models.ResetToken;

@Repository
public interface ResetTokenRepository extends MongoRepository<ResetToken, String> {

    Optional<ResetToken> findByToken(String token);

    void deleteByUserId(String userId);
}
