package com.sanedge.pointofsale.order.repository.statsbymerchant;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.order.models.OrderMonthTotalRevenue;
import com.sanedge.pointofsale.order.models.OrderYearTotalRevenue;

@Repository
public interface OrderTotalRevenueByMerchantRepository {
    List<OrderMonthTotalRevenue> findMonthlyTotalRevenueByMerchant(
            Long merchantId,
            Integer year1,
            Integer month1,
            Integer year2,
            Integer month2);

    List<OrderYearTotalRevenue> findYearlyTotalRevenueByMerchant(
            Long merchantId,
            Integer year);
}
