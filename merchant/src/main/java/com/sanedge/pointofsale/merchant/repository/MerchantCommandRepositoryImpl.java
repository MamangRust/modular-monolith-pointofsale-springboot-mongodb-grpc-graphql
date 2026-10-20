package com.sanedge.pointofsale.merchant.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.merchant.models.Merchant;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MerchantCommandRepositoryImpl implements MerchantCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Merchant trashed(String merchantId) {
        Query query = new Query(Criteria.where("_id").is(merchantId).and("deleted_at").isNull());

        Merchant merchant = mongoTemplate.findOne(query, Merchant.class);
        if (merchant == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()), Merchant.class);

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(merchantId)), Merchant.class);
    }

    @Override
    public Merchant restore(String merchantId) {
        Query query = new Query(Criteria.where("_id").is(merchantId).and("deleted_at").ne(null));

        Merchant merchant = mongoTemplate.findOne(query, Merchant.class);
        if (merchant == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), Merchant.class);

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(merchantId)), Merchant.class);
    }

    @Override
    public boolean deletePermanent(String merchantId) {
        Query query = new Query(Criteria.where("_id").is(merchantId).and("deleted_at").ne(null));

        return mongoTemplate.remove(query, Merchant.class).getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), Merchant.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), Merchant.class)
                .getDeletedCount() > 0;
    }
}
