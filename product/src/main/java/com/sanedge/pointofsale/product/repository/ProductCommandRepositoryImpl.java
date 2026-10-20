package com.sanedge.pointofsale.product.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.product.models.Product;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductCommandRepositoryImpl implements ProductCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Product trashed(String productId) {
        Query query = new Query(Criteria.where("_id").is(productId).and("deleted_at").isNull());

        Product product = mongoTemplate.findOne(query, Product.class);
        if (product == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()), Product.class);

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(productId)), Product.class);
    }

    @Override
    public Product restore(String productId) {
        Query query = new Query(Criteria.where("_id").is(productId).and("deleted_at").ne(null));

        Product product = mongoTemplate.findOne(query, Product.class);
        if (product == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), Product.class);

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(productId)), Product.class);
    }

    @Override
    public Product deletePermanent(String productId) {
        Query query = new Query(Criteria.where("_id").is(productId).and("deleted_at").ne(null));

        Product product = mongoTemplate.findOne(query, Product.class);
        if (product == null) {
            return null;
        }

        mongoTemplate.remove(query, Product.class);

        return product;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), Product.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), Product.class)
                .getDeletedCount() > 0;
    }
}
