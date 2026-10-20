package com.sanedge.pointofsale.user.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.user.models.User;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserCommandRepositoryImpl implements UserCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public User trashed(String userId) {
        Query query = new Query(Criteria.where("_id").is(userId).and("deleted_at").isNull());

        User user = mongoTemplate.findOne(query, User.class);
        if (user == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()), User.class);

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(userId)), User.class);
    }

    @Override
    public User restore(String userId) {
        Query query = new Query(Criteria.where("_id").is(userId).and("deleted_at").ne(null));

        User user = mongoTemplate.findOne(query, User.class);
        if (user == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), User.class);

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(userId)), User.class);
    }

    @Override
    public int deletePermanent(String userId) {
        Query query = new Query(Criteria.where("_id").is(userId).and("deleted_at").ne(null));

        return (int) mongoTemplate.remove(query, User.class).getDeletedCount();
    }

    @Override
    public int restoreAllDeleted() {
        return (int) mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), User.class)
                .getModifiedCount();
    }

    @Override
    public int deleteAllDeleted() {
        return (int) mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), User.class)
                .getDeletedCount();
    }
}
