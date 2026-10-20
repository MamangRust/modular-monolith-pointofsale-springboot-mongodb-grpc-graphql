package com.sanedge.pointofsale.order.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.order.models.Order;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderCommandRepositoryImpl implements OrderCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Order trashed(String orderId) {
        Query query = new Query(Criteria.where("_id").is(orderId).and("deleted_at").isNull());

        Order order = mongoTemplate.findOne(query, Order.class);
        if (order == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()), Order.class);

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(orderId)), Order.class);
    }

    @Override
    public Order restore(String orderId) {
        Query query = new Query(Criteria.where("_id").is(orderId).and("deleted_at").ne(null));

        Order order = mongoTemplate.findOne(query, Order.class);
        if (order == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), Order.class);

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(orderId)), Order.class);
    }

    @Override
    public Order deletePermanent(String orderId) {
        Query query = new Query(Criteria.where("_id").is(orderId).and("deleted_at").ne(null));

        Order order = mongoTemplate.findOne(query, Order.class);
        if (order == null) {
            return null;
        }

        mongoTemplate.remove(query, Order.class);

        return order;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), Order.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), Order.class)
                .getDeletedCount() > 0;
    }
}
