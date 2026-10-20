package com.sanedge.pointofsale.product.repository;

import com.sanedge.pointofsale.product.models.Product;

public interface ProductCommandRepositoryCustom {
    Product trashed(String productId);

    Product restore(String productId);

    Product deletePermanent(String productId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
