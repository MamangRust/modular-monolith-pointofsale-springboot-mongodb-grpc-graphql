package com.sanedge.pointofsale.order.repository.statsbymerchant;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.order.models.OrderMonth;
import com.sanedge.pointofsale.order.models.OrderYear;

@Repository
public interface OrderSoldOutByMerchantRepository {
    List<OrderMonth> findMonthlyOrdersByMerchant(Integer merchantId, Integer yearMonth);

    List<OrderYear> findYearlyOrdersByMerchant(Integer merchantId, Integer yearMonth);
}
