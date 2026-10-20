package com.sanedge.pointofsale.user.repository;

import com.sanedge.pointofsale.user.models.User;

public interface UserCommandRepositoryCustom {
    User trashed(String userId);

    User restore(String userId);

    int deletePermanent(String userId);

    int restoreAllDeleted();

    int deleteAllDeleted();
}
