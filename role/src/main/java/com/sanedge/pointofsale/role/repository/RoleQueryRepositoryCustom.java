package com.sanedge.pointofsale.role.repository;

import java.util.List;

import com.sanedge.pointofsale.role.models.Role;

public interface RoleQueryRepositoryCustom {
    List<Role> findUserRoles(Long userId);
}
