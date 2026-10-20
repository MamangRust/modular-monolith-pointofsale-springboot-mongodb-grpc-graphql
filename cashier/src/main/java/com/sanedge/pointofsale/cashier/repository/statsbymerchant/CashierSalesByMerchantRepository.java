package com.sanedge.pointofsale.cashier.repository.statsbymerchant;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.cashier.models.CashierMonthSales;
import com.sanedge.pointofsale.cashier.models.CashierYearSales;

@Repository
public interface CashierSalesByMerchantRepository {
    List<CashierMonthSales> findMonthSalesByMerchant(
            Long merchantId,
            Integer year,
            Integer startMonth,
            Integer endMonth);

    List<CashierYearSales> findYearSalesByMerchant(
            Long merchantId,
            Integer year);
}
