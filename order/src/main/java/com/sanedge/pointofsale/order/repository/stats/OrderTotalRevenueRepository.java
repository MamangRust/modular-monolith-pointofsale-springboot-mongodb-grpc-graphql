package com.sanedge.pointofsale.order.repository.stats;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.order.models.OrderMonthTotalRevenue;
import com.sanedge.pointofsale.order.models.OrderYearTotalRevenue;

@Repository
public interface OrderTotalRevenueRepository {
    List<OrderMonthTotalRevenue> findMonthlyTotalRevenue(
            Integer year1,
            Integer month1,
            Integer year2,
            Integer month2);

    List<OrderYearTotalRevenue> findYearlyTotalRevenue(Integer year);
}
