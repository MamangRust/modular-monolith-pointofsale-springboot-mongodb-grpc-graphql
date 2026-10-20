package com.sanedge.pointofsale.order.repository.stats;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.order.models.OrderMonth;
import com.sanedge.pointofsale.order.models.OrderYear;

@Repository
public interface OrderSoldOutRepository {
    List<OrderMonth> findMonthlyOrdersByYear(Integer yearMonth);

    List<OrderYear> findYearlyOrders(Integer yearMonth);
}
