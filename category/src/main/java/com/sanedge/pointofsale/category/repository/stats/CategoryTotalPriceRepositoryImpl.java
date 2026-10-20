package com.sanedge.pointofsale.category.repository.stats;

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

import com.sanedge.pointofsale.category.models.CategoryMonthTotalPrice;
import com.sanedge.pointofsale.category.models.CategoryYearTotalPrice;
import com.sanedge.pointofsale.category.repository.CategoryStatsSupport;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryTotalPriceRepositoryImpl implements CategoryTotalPriceRepository {

    private final CategoryStatsSupport support;

    @Override
    public List<CategoryMonthTotalPrice> findMonthlyTotalPrice(
            Integer startYear, Integer startMonth, Integer endYear, Integer endMonth) {

        LocalDateTime start = LocalDateTime.of(startYear, startMonth, 1, 0, 0);
        LocalDateTime endExclusive = LocalDateTime.of(endYear, endMonth, 1, 0, 0).plusMonths(1);

        Map<Integer, Long> revenueByMonth = totalRevenueByMonth(start, endExclusive, null, null);

        List<YearMonth> months = List.of(YearMonth.of(startYear, startMonth), YearMonth.of(endYear, endMonth));
        return months.stream()
                .sorted(Comparator.comparingInt((YearMonth ym) -> ym.getYear())
                        .thenComparingInt(YearMonth::getMonthValue)
                        .reversed())
                .map(ym -> new CategoryMonthTotalPrice(
                        String.valueOf(ym.getYear()),
                        ym.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        revenueByMonth.getOrDefault(ym.getYear() * 100 + ym.getMonthValue(), 0L)))
                .toList();
    }

    @Override
    public List<CategoryYearTotalPrice> findYearlyTotalPrice(Integer year, Integer yearMinusOne) {
        LocalDateTime start = LocalDateTime.of(yearMinusOne, 1, 1, 0, 0);
        LocalDateTime endExclusive = LocalDateTime.of(year, 1, 1, 0, 0).plusYears(1);

        Map<Integer, Long> revenueByYear = totalRevenueByYear(start, endExclusive, null, null);

        return orderedDesc(List.of(year, yearMinusOne)).stream()
                .map(y -> new CategoryYearTotalPrice(String.valueOf(y), revenueByYear.getOrDefault(y, 0L)))
                .toList();
    }

    public Map<Integer, Long> totalRevenueByMonth(LocalDateTime start, LocalDateTime endExclusive,
            Integer merchantId, Integer categoryId) {
        return support.findJoinedRows(start, endExclusive, merchantId, categoryId).stream()
                .collect(Collectors.groupingBy(
                        row -> row.orderCreatedAt().getYear() * 100
                                + row.orderCreatedAt().getMonthValue(),
                        Collectors.summingLong(CategoryStatsSupport.JoinedRow::orderTotalPrice)));
    }

    public Map<Integer, Long> totalRevenueByYear(LocalDateTime start, LocalDateTime endExclusive,
            Integer merchantId, Integer categoryId) {
        return support.findJoinedRows(start, endExclusive, merchantId, categoryId).stream()
                .collect(Collectors.groupingBy(
                        row -> row.orderCreatedAt().getYear(),
                        Collectors.summingLong(CategoryStatsSupport.JoinedRow::orderTotalPrice)));
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
