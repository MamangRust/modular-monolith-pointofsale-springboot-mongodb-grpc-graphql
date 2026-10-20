package com.sanedge.pointofsale.category.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.category.models.Category;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryCommandRepositoryImpl implements CategoryCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Category trashed(String categoryId) {
        Query query = new Query(Criteria.where("_id").is(categoryId).and("deleted_at").isNull());

        Category category = mongoTemplate.findOne(query, Category.class);
        if (category == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()), Category.class);

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(categoryId)), Category.class);
    }

    @Override
    public Category restore(String categoryId) {
        Query query = new Query(Criteria.where("_id").is(categoryId).and("deleted_at").ne(null));

        Category category = mongoTemplate.findOne(query, Category.class);
        if (category == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), Category.class);

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(categoryId)), Category.class);
    }

    @Override
    public Category deletePermanent(String categoryId) {
        Query query = new Query(Criteria.where("_id").is(categoryId).and("deleted_at").ne(null));

        Category category = mongoTemplate.findOne(query, Category.class);
        if (category == null) {
            return null;
        }

        mongoTemplate.remove(query, Category.class);

        return category;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), Category.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), Category.class)
                .getDeletedCount() > 0;
    }
}
