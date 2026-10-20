package com.sanedge.pointofsale.category.repository;

import com.sanedge.pointofsale.category.models.Category;

public interface CategoryCommandRepositoryCustom {
    Category trashed(String categoryId);

    Category restore(String categoryId);

    Category deletePermanent(String categoryId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
