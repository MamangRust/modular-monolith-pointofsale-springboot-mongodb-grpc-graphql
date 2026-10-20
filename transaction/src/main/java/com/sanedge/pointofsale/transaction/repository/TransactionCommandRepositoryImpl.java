package com.sanedge.pointofsale.transaction.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.transaction.models.Transaction;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TransactionCommandRepositoryImpl implements TransactionCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Transaction trashed(String transactionId) {
        Query query = new Query(Criteria.where("_id").is(transactionId).and("deleted_at").isNull());

        Transaction transaction = mongoTemplate.findOne(query, Transaction.class);
        if (transaction == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()), Transaction.class);

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(transactionId)),
                Transaction.class);
    }

    @Override
    public Transaction restore(String transactionId) {
        Query query = new Query(Criteria.where("_id").is(transactionId).and("deleted_at").ne(null));

        Transaction transaction = mongoTemplate.findOne(query, Transaction.class);
        if (transaction == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), Transaction.class);

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(transactionId)),
                Transaction.class);
    }

    @Override
    public Transaction deletePermanent(String transactionId) {
        Query query = new Query(Criteria.where("_id").is(transactionId).and("deleted_at").ne(null));

        Transaction transaction = mongoTemplate.findOne(query, Transaction.class);
        if (transaction == null) {
            return null;
        }

        mongoTemplate.remove(query, Transaction.class);

        return transaction;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), Transaction.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), Transaction.class)
                .getDeletedCount() > 0;
    }
}
