package com.sanedge.pointofsale.cashier.repository.statsbymerchant;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.cashier.models.CashierMonthTotalSales;
import com.sanedge.pointofsale.cashier.models.CashierYearTotalSales;
import com.sanedge.pointofsale.cashier.repository.stats.CashierTotalSalesRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CashierTotalSalesByMerchantRepositoryImpl implements CashierTotalSalesByMerchantRepository {

    private final CashierTotalSalesRepositoryImpl totalSalesHelper;

    @Override
    public List<CashierMonthTotalSales> findMonthTotalSalesByMerchant(
            Long merchantId, int startYear, int startMonth, int endYear, int endMonth) {

        Integer merId = toMerchantId(merchantId);
        LocalDateTime start = LocalDateTime.of(startYear, startMonth, 1, 0, 0);
        LocalDateTime endExclusive = LocalDateTime.of(endYear, endMonth, 1, 0, 0).plusMonths(1);

        Map<Integer, Long> salesByMonth = totalSalesHelper.totalSalesByMonth(start, endExclusive, merId, null);

        List<YearMonth> months = List.of(YearMonth.of(startYear, startMonth), YearMonth.of(endYear, endMonth));
        return months.stream()
                .sorted(java.util.Comparator
                        .comparingInt((YearMonth ym) -> ym.getYear())
                        .thenComparingInt(YearMonth::getMonthValue)
                        .reversed())
                .map(ym -> new CashierMonthTotalSales(
                        String.valueOf(ym.getYear()),
                        CashierTotalSalesRepositoryImpl.fullMonthName(ym.getMonthValue()),
                        salesByMonth.getOrDefault(ym.getYear() * 100 + ym.getMonthValue(), 0L)))
                .toList();
    }

    @Override
    public List<CashierYearTotalSales> findYearTotalSalesByMerchant(Long merchantId, int year, int yearMinusOne) {
        Integer merId = toMerchantId(merchantId);
        LocalDateTime start = LocalDateTime.of(yearMinusOne, 1, 1, 0, 0);
        LocalDateTime endExclusive = LocalDateTime.of(year, 1, 1, 0, 0).plusYears(1);

        Map<Integer, Long> salesByYear = totalSalesHelper.totalSalesByYear(start, endExclusive, merId, null);

        return CashierTotalSalesRepositoryImpl.orderedDesc(List.of(year, yearMinusOne)).stream()
                .map(y -> new CashierYearTotalSales(String.valueOf(y), salesByYear.getOrDefault(y, 0L)))
                .toList();
    }

    private static Integer toMerchantId(Long merchantId) {
        return merchantId == null ? null : Integer.valueOf(String.valueOf(merchantId));
    }
}
