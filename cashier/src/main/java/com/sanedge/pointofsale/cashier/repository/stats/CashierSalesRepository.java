package com.sanedge.pointofsale.cashier.repository.stats;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.cashier.models.CashierMonthSales;
import com.sanedge.pointofsale.cashier.models.CashierYearSales;

@Repository
public interface CashierSalesRepository {

    List<CashierMonthSales> findMonthSales(
            Integer year,
            Integer startMonth,
            Integer endMonth);

    List<CashierYearSales> findYearSales(Integer year);
}
