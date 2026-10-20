package com.sanedge.pointofsale.cashier.repository.statsbyid;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.cashier.models.CashierMonthSales;
import com.sanedge.pointofsale.cashier.models.CashierYearSales;
import com.sanedge.pointofsale.cashier.repository.CashierStatsSupport;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CashierSalesByIdRepositoryImpl implements CashierSalesByIdRepository {

    private final CashierStatsSupport support;

    @Override
    public List<CashierMonthSales> findMonthSalesById(Long cashierId, Integer year, Integer startMonth,
            Integer endMonth) {
        Integer cashId = toCashierId(cashierId);
        LocalDate start = LocalDate.of(year, startMonth, 1);
        LocalDate endExclusive = LocalDate.of(year, endMonth, 1).plusMonths(1);
        List<CashierStatsSupport.CashierRow> rows = support.findJoinedRows(start.atStartOfDay(),
                endExclusive.atStartOfDay(), null, cashId);

        Map<String, List<CashierStatsSupport.CashierRow>> grouped = rows.stream()
                .filter(row -> row.orderCreatedAt() != null)
                .collect(Collectors.groupingBy(row -> (row.orderCreatedAt().getMonthValue() + ":"
                        + row.cashierId())));

        record Entry(int monthNum, int cashierId, CashierMonthSales dto) {
        }

        List<Entry> entries = grouped.values().stream().map(bucket -> {
            int monthNum = bucket.get(0).orderCreatedAt().getMonthValue();
            String monthName = Month.of(monthNum).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            int orderCount = bucket.size();
            long totalSales = bucket.stream().mapToLong(CashierStatsSupport.CashierRow::totalPrice).sum();
            return new Entry(monthNum, bucket.get(0).cashierId(), new CashierMonthSales(monthName,
                    bucket.get(0).cashierId(), bucket.get(0).cashierName(), orderCount, totalSales));
        }).toList();

        return entries.stream()
                .sorted(Comparator.comparingInt(Entry::monthNum).thenComparingInt(Entry::cashierId))
                .map(Entry::dto)
                .toList();
    }

    @Override
    public List<CashierYearSales> findYearSalesById(Long cashierId, Integer year) {
        Integer cashId = toCashierId(cashierId);
        List<CashierStatsSupport.CashierRow> rows = support.findJoinedRows(
                LocalDate.of(year - 4, 1, 1).atStartOfDay(),
                LocalDate.of(year + 1, 1, 1).atStartOfDay(), null, cashId);

        Map<String, List<CashierStatsSupport.CashierRow>> grouped = rows.stream()
                .filter(row -> row.orderCreatedAt() != null)
                .collect(Collectors.groupingBy(row -> (row.orderCreatedAt().getYear() + ":"
                        + row.cashierId())));

        record Entry(int yearNum, int cashierId, CashierYearSales dto) {
        }

        List<Entry> entries = grouped.values().stream().map(bucket -> {
            int yearNum = bucket.get(0).orderCreatedAt().getYear();
            int orderCount = bucket.size();
            long totalSales = bucket.stream().mapToLong(CashierStatsSupport.CashierRow::totalPrice).sum();
            return new Entry(yearNum, bucket.get(0).cashierId(), new CashierYearSales(String.valueOf(yearNum),
                    bucket.get(0).cashierId(), bucket.get(0).cashierName(), orderCount, totalSales));
        }).toList();

        return entries.stream()
                .sorted(Comparator.comparingInt(Entry::yearNum).thenComparingInt(Entry::cashierId))
                .map(Entry::dto)
                .toList();
    }

    private static Integer toCashierId(Long cashierId) {
        return cashierId == null ? null : Integer.valueOf(String.valueOf(cashierId));
    }
}
