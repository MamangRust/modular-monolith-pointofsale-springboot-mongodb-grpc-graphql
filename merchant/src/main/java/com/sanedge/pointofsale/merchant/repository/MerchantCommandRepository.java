package com.sanedge.pointofsale.merchant.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.merchant.models.Merchant;

@Repository
public interface MerchantCommandRepository extends MongoRepository<Merchant, String>, MerchantCommandRepositoryCustom {
}
