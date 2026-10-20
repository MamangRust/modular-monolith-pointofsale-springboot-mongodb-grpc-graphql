package com.sanedge.pointofsale.transaction.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.transaction.models.Transaction;

@Repository
public interface TransactionCommandRepository
        extends MongoRepository<Transaction, String>, TransactionCommandRepositoryCustom {
}
