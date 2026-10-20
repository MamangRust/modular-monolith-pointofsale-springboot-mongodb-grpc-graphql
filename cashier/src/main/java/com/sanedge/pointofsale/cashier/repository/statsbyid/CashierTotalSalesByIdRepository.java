package com.sanedge.pointofsale.cashier.repository.statsbyid;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.cashier.models.CashierMonthTotalSales;
import com.sanedge.pointofsale.cashier.models.CashierYearTotalSales;

@Repository
public interface CashierTotalSalesByIdRepository {
    List<CashierMonthTotalSales> findMonthTotalSalesById(
            Long cashierId,
            Integer startYear,
            Integer startMonth,
            Integer endYear,
            Integer endMonth);

    List<CashierYearTotalSales> findYearTotalSalesById(
            Long cashierId,
            Integer year,
            Integer yearMinusOne);
}
