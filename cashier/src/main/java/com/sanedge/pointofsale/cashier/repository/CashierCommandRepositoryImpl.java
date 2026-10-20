package com.sanedge.pointofsale.cashier.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.cashier.models.Cashier;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CashierCommandRepositoryImpl implements CashierCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Cashier trashed(String cashierId) {
        Query query = new Query(Criteria.where("_id").is(cashierId).and("deleted_at").isNull());

        Cashier cashier = mongoTemplate.findOne(query, Cashier.class);
        if (cashier == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()), Cashier.class);

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(cashierId)), Cashier.class);
    }

    @Override
    public Cashier restore(String cashierId) {
        Query query = new Query(Criteria.where("_id").is(cashierId).and("deleted_at").ne(null));

        Cashier cashier = mongoTemplate.findOne(query, Cashier.class);
        if (cashier == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), Cashier.class);

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(cashierId)), Cashier.class);
    }

    @Override
    public Cashier deletePermanent(String cashierId) {
        Query query = new Query(Criteria.where("_id").is(cashierId).and("deleted_at").ne(null));

        Cashier cashier = mongoTemplate.findOne(query, Cashier.class);
        if (cashier == null) {
            return null;
        }

        mongoTemplate.remove(query, Cashier.class);

        return cashier;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), Cashier.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), Cashier.class)
                .getDeletedCount() > 0;
    }
}
