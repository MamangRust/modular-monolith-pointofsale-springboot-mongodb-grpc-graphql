package com.sanedge.pointofsale.transaction.repository.stats;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.transaction.models.TransactionMonthlyMethod;
import com.sanedge.pointofsale.transaction.models.TransactionYearMethod;
import com.sanedge.pointofsale.transaction.repository.TransactionStatsSupport;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TransactionMethodRepositoryImpl implements TransactionMethodRepository {

    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAILED = "FAILED";

    private final TransactionStatsSupport support;

    private record PeriodBucket(int first, int second, String paymentMethod, int totalTransactions,
            long totalAmount) {
    }

    @Override
    public List<TransactionMonthlyMethod> findMonthlyMethodsSuccess(
            Integer year1, Integer month1, Integer year2, Integer month2) {
        return findMonthlyMethods(year1, month1, year2, month2, STATUS_SUCCESS).stream()
                .map(bucket -> new TransactionMonthlyMethod(
                        Month.of(bucket.second()).getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        bucket.paymentMethod(), bucket.totalTransactions(), bucket.totalAmount()))
                .toList();
    }

    @Override
    public List<TransactionMonthlyMethod> findMonthlyMethodsFailed(
            Integer year1, Integer month1, Integer year2, Integer month2) {
        return findMonthlyMethods(year1, month1, year2, month2, STATUS_FAILED).stream()
                .map(bucket -> new TransactionMonthlyMethod(
                        Month.of(bucket.second()).getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        bucket.paymentMethod(), bucket.totalTransactions(), bucket.totalAmount()))
                .toList();
    }

    @Override
    public List<TransactionYearMethod> findYearlyMethodsSuccess(Integer year) {
        return findYearlyMethods(year, STATUS_SUCCESS).stream()
                .map(bucket -> new TransactionYearMethod(
                        String.valueOf(bucket.first()), bucket.paymentMethod(),
                        bucket.totalTransactions(), bucket.totalAmount()))
                .toList();
    }

    @Override
    public List<TransactionYearMethod> findYearlyMethodsFailed(Integer year) {
        return findYearlyMethods(year, STATUS_FAILED).stream()
                .map(bucket -> new TransactionYearMethod(
                        String.valueOf(bucket.first()), bucket.paymentMethod(),
                        bucket.totalTransactions(), bucket.totalAmount()))
                .toList();
    }

    private List<PeriodBucket> findMonthlyMethods(Integer year1, Integer month1,
            Integer year2, Integer month2, String status) {
        TransactionStatsSupport.Range range1 = TransactionStatsSupport.monthRange(year1, month1);
        TransactionStatsSupport.Range range2 = TransactionStatsSupport.monthRange(year2, month2);

        List<TransactionStatsSupport.Range> ranges = new ArrayList<>();
        if (range1 != null) {
            ranges.add(range1);
        }
        if (range2 != null) {
            ranges.add(range2);
        }

        List<String> methods = support.findDistinctPaymentMethods(null);
        if (methods.isEmpty()) {
            return List.of();
        }

        Map<String, long[]> grouped = new HashMap<>();
        for (TransactionStatsSupport.TransactionRow row : support.findTransactions(ranges, null, status)) {
            String key = row.createdAt().getYear() + ":" + row.createdAt().getMonthValue()
                    + ":" + nullToEmpty(row.paymentMethod());
            long[] acc = grouped.computeIfAbsent(key, k -> new long[2]);
            acc[0]++;
            acc[1] += row.amount();
        }

        List<TransactionStatsSupport.Range> orderedRanges = new ArrayList<>(ranges);
        orderedRanges.sort(Comparator.comparing(TransactionStatsSupport.Range::startInclusive));

        List<PeriodBucket> buckets = new ArrayList<>();
        for (TransactionStatsSupport.Range range : orderedRanges) {
            int year = range.startInclusive().getYear();
            int month = range.startInclusive().getMonthValue();
            for (String method : methods) {
                long[] acc = grouped.get(year + ":" + month + ":" + nullToEmpty(method));
                buckets.add(new PeriodBucket(month, month, method,
                        acc != null ? (int) acc[0] : 0, acc != null ? acc[1] : 0L));
            }
        }
        return buckets;
    }

    private List<PeriodBucket> findYearlyMethods(Integer year, String status) {
        if (year == null) {
            return List.of();
        }

        List<TransactionStatsSupport.Range> ranges = List.of(
                TransactionStatsSupport.yearRange(year - 1),
                TransactionStatsSupport.yearRange(year));

        List<String> methods = support.findDistinctPaymentMethods(null);
        if (methods.isEmpty()) {
            return List.of();
        }

        Map<String, long[]> grouped = new HashMap<>();
        for (TransactionStatsSupport.TransactionRow row : support.findTransactions(ranges, null, status)) {
            String key = row.createdAt().getYear() + ":" + nullToEmpty(row.paymentMethod());
            long[] acc = grouped.computeIfAbsent(key, k -> new long[2]);
            acc[0]++;
            acc[1] += row.amount();
        }

        List<PeriodBucket> buckets = new ArrayList<>();
        for (int yearValue : new int[] { year - 1, year }) {
            for (String method : methods) {
                long[] acc = grouped.get(yearValue + ":" + nullToEmpty(method));
                buckets.add(new PeriodBucket(yearValue, yearValue, method,
                        acc != null ? (int) acc[0] : 0, acc != null ? acc[1] : 0L));
            }
        }
        return buckets;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
