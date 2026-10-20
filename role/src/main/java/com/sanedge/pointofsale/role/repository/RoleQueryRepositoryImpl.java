package com.sanedge.pointofsale.role.repository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.role.models.Role;
import com.sanedge.pointofsale.role.models.UserRole;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoleQueryRepositoryImpl implements RoleQueryRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Role> findUserRoles(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        // Two-step join (no cross-collection $lookup: ids are String while the
        // caller still speaks numeric ids): user_roles by user_id, then roles by
        // role_id in (...).
        Query userRoleQuery = new Query(Criteria.where("user_id").is(String.valueOf(userId)));
        List<UserRole> userRoles = mongoTemplate.find(userRoleQuery, UserRole.class);

        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .filter(Objects::nonNull)
                .toList();

        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        Query roleQuery = new Query(Criteria.where("_id").in(roleIds))
                .with(Sort.by(Sort.Direction.ASC, "created_at"));

        return mongoTemplate.find(roleQuery, Role.class);
    }
}
