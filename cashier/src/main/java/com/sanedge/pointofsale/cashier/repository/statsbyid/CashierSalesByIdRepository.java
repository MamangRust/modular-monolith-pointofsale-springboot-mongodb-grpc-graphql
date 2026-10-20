package com.sanedge.pointofsale.cashier.repository.statsbyid;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.cashier.models.CashierMonthSales;
import com.sanedge.pointofsale.cashier.models.CashierYearSales;

@Repository
public interface CashierSalesByIdRepository {
    List<CashierMonthSales> findMonthSalesById(
            Long cashierId,
            Integer year,
            Integer startMonth,
            Integer endMonth);

    List<CashierYearSales> findYearSalesById(
            Long cashierId,
            Integer year);
}
