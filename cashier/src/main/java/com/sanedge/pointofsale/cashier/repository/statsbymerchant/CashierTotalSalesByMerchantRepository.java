package com.sanedge.pointofsale.cashier.repository.statsbymerchant;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.cashier.models.CashierMonthTotalSales;
import com.sanedge.pointofsale.cashier.models.CashierYearTotalSales;

@Repository
public interface CashierTotalSalesByMerchantRepository {
    List<CashierMonthTotalSales> findMonthTotalSalesByMerchant(
            Long merchantId,
            int startYear,
            int startMonth,
            int endYear,
            int endMonth);

    List<CashierYearTotalSales> findYearTotalSalesByMerchant(
            Long merchantId,
            int year,
            int yearMinusOne);
}
