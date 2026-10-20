package com.sanedge.pointofsale.role.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.role.models.Role;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoleCommandRepositoryImpl implements RoleCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Role trashed(String roleId) {
        Query query = new Query(Criteria.where("_id").is(roleId).and("deleted_at").isNull());

        Role role = mongoTemplate.findOne(query, Role.class);
        if (role == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().set("deleted_at", LocalDateTime.now()), Role.class);

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(roleId)), Role.class);
    }

    @Override
    public Role restore(String roleId) {
        Query query = new Query(Criteria.where("_id").is(roleId).and("deleted_at").ne(null));

        Role role = mongoTemplate.findOne(query, Role.class);
        if (role == null) {
            return null;
        }

        mongoTemplate.updateFirst(query, new Update().unset("deleted_at"), Role.class);

        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(roleId)), Role.class);
    }

    @Override
    public Role deletePermanent(String roleId) {
        Query query = new Query(Criteria.where("_id").is(roleId).and("deleted_at").ne(null));

        Role role = mongoTemplate.findOne(query, Role.class);
        if (role == null) {
            return null;
        }

        mongoTemplate.remove(query, Role.class);

        return role;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate
                .updateMulti(new Query(Criteria.where("deleted_at").ne(null)),
                        new Update().unset("deleted_at"), Role.class)
                .getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate
                .remove(new Query(Criteria.where("deleted_at").ne(null)), Role.class)
                .getDeletedCount() > 0;
    }
}
