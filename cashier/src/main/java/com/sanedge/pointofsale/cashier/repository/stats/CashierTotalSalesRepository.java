package com.sanedge.pointofsale.cashier.repository.stats;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.cashier.models.CashierMonthTotalSales;
import com.sanedge.pointofsale.cashier.models.CashierYearTotalSales;

@Repository
public interface CashierTotalSalesRepository {

    List<CashierMonthTotalSales> findMonthTotalSales(
            Integer startYear,
            Integer startMonth,
            Integer endYear,
            Integer endMonth);

    List<CashierYearTotalSales> findYearTotalSales(
            Integer year,
            Integer yearMinusOne);
}
