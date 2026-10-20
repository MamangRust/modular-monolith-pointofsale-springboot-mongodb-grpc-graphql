package com.sanedge.pointofsale.cashier.repository.stats;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.cashier.models.CashierMonthTotalSales;
import com.sanedge.pointofsale.cashier.models.CashierYearTotalSales;
import com.sanedge.pointofsale.cashier.repository.CashierStatsSupport;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CashierTotalSalesRepositoryImpl implements CashierTotalSalesRepository {

    private final CashierStatsSupport support;

    @Override
    public List<CashierMonthTotalSales> findMonthTotalSales(
            Integer startYear, Integer startMonth, Integer endYear, Integer endMonth) {

        LocalDateTime start = LocalDateTime.of(startYear, startMonth, 1, 0, 0);
        LocalDateTime endExclusive = LocalDateTime.of(endYear, endMonth, 1, 0, 0).plusMonths(1);

        Map<Integer, Long> salesByMonth = totalSalesByMonth(start, endExclusive, null, null);

        List<YearMonth> months = List.of(YearMonth.of(startYear, startMonth), YearMonth.of(endYear, endMonth));
        return months.stream()
                .sorted(Comparator.comparingInt((YearMonth ym) -> ym.getYear())
                        .thenComparingInt(YearMonth::getMonthValue)
                        .reversed())
                .map(ym -> new CashierMonthTotalSales(
                        String.valueOf(ym.getYear()),
                        ym.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        salesByMonth.getOrDefault(ym.getYear() * 100 + ym.getMonthValue(), 0L)))
                .toList();
    }

    @Override
    public List<CashierYearTotalSales> findYearTotalSales(Integer year, Integer yearMinusOne) {
        LocalDateTime start = LocalDateTime.of(yearMinusOne, 1, 1, 0, 0);
        LocalDateTime endExclusive = LocalDateTime.of(year, 1, 1, 0, 0).plusYears(1);

        Map<Integer, Long> salesByYear = totalSalesByYear(start, endExclusive, null, null);

        return orderedDesc(List.of(year, yearMinusOne)).stream()
                .map(y -> new CashierYearTotalSales(String.valueOf(y), salesByYear.getOrDefault(y, 0L)))
                .toList();
    }

    public Map<Integer, Long> totalSalesByMonth(LocalDateTime start, LocalDateTime endExclusive,
            Integer merchantId, Integer cashierId) {
        return support.findJoinedRows(start, endExclusive, merchantId, cashierId).stream()
                .filter(row -> row.orderCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        row -> row.orderCreatedAt().getYear() * 100 + row.orderCreatedAt().getMonthValue(),
                        Collectors.summingLong(CashierStatsSupport.CashierRow::totalPrice)));
    }

    public Map<Integer, Long> totalSalesByYear(LocalDateTime start, LocalDateTime endExclusive,
            Integer merchantId, Integer cashierId) {
        return support.findJoinedRows(start, endExclusive, merchantId, cashierId).stream()
                .filter(row -> row.orderCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        row -> row.orderCreatedAt().getYear(),
                        Collectors.summingLong(CashierStatsSupport.CashierRow::totalPrice)));
    }

    public static String fullMonthName(int monthValue) {
        return Month.of(monthValue).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    public static List<Integer> orderedDesc(List<Integer> values) {
        List<Integer> copy = new ArrayList<>(values);
        copy.sort(Comparator.reverseOrder());
        return copy;
    }
}
