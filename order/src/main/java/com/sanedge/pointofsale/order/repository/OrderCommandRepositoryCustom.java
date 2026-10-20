package com.sanedge.pointofsale.order.repository;

import com.sanedge.pointofsale.order.models.Order;

public interface OrderCommandRepositoryCustom {
    Order trashed(String orderId);

    Order restore(String orderId);

    Order deletePermanent(String orderId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
