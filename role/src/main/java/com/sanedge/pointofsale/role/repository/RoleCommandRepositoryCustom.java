package com.sanedge.pointofsale.role.repository;

import com.sanedge.pointofsale.role.models.Role;

public interface RoleCommandRepositoryCustom {
    Role trashed(String roleId);

    Role restore(String roleId);

    Role deletePermanent(String roleId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
