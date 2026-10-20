package com.sanedge.pointofsale.transaction.repository.statsbymerchant;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.transaction.models.TransactionMonthlyAmountFailed;
import com.sanedge.pointofsale.transaction.models.TransactionMonthlyAmountSuccess;
import com.sanedge.pointofsale.transaction.models.TransactionYearlyAmountFailed;
import com.sanedge.pointofsale.transaction.models.TransactionYearlyAmountSuccess;
import com.sanedge.pointofsale.transaction.repository.TransactionStatsSupport;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TransactionAmountByMerchantRepositoryImpl implements TransactionAmountByMerchantRepository {

    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAILED = "FAILED";

    private final TransactionStatsSupport support;

    private record MonthlyBucket(int year, int month, int total, long totalAmount) {
    }

    private record YearlyBucket(int year, int total, long totalAmount) {
    }

    @Override
    public List<TransactionMonthlyAmountSuccess> findMonthlySuccessByMerchant(
            Long merchantId, Integer year, Integer month, Integer prevYear, Integer prevMonth) {
        return findMonthlyBuckets(merchantId, year, month, prevYear, prevMonth, STATUS_SUCCESS).stream()
                .map(bucket -> new TransactionMonthlyAmountSuccess(
                        String.valueOf(bucket.year()),
                        Month.of(bucket.month()).getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        bucket.total(), bucket.totalAmount()))
                .toList();
    }

    @Override
    public List<TransactionYearlyAmountSuccess> findYearlySuccessByMerchant(Long merchantId, Integer year) {
        return findYearlyBuckets(merchantId, year, STATUS_SUCCESS).stream()
                .map(bucket -> new TransactionYearlyAmountSuccess(
                        String.valueOf(bucket.year()), bucket.total(), bucket.totalAmount()))
                .toList();
    }

    @Override
    public List<TransactionMonthlyAmountFailed> findMonthlyFailedByMerchant(
            Long merchantId, Integer year, Integer month, Integer prevYear, Integer prevMonth) {
        return findMonthlyBuckets(merchantId, year, month, prevYear, prevMonth, STATUS_FAILED).stream()
                .map(bucket -> new TransactionMonthlyAmountFailed(
                        String.valueOf(bucket.year()),
                        Month.of(bucket.month()).getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        bucket.total(), bucket.totalAmount()))
                .toList();
    }

    @Override
    public List<TransactionYearlyAmountFailed> findYearlyFailedByMerchant(Long merchantId, Integer year) {
        return findYearlyBuckets(merchantId, year, STATUS_FAILED).stream()
                .map(bucket -> new TransactionYearlyAmountFailed(
                        String.valueOf(bucket.year()), bucket.total(), bucket.totalAmount()))
                .toList();
    }

    private Integer toMerchantId(Long merchantId) {
        return merchantId == null ? null : Integer.valueOf(String.valueOf(merchantId));
    }

    private List<MonthlyBucket> findMonthlyBuckets(Long merchantId, Integer year, Integer month,
            Integer prevYear, Integer prevMonth, String status) {
        List<TransactionStatsSupport.Range> ranges = new ArrayList<>();
        TransactionStatsSupport.Range current = TransactionStatsSupport.monthRange(year, month);
        TransactionStatsSupport.Range previous = TransactionStatsSupport.monthRange(prevYear, prevMonth);
        if (current != null) {
            ranges.add(current);
        }
        if (previous != null) {
            ranges.add(previous);
        }

        Map<String, long[]> grouped = new HashMap<>();
        for (TransactionStatsSupport.TransactionRow row : support.findTransactions(ranges,
                toMerchantId(merchantId), status)) {
            String key = row.createdAt().getYear() + ":" + row.createdAt().getMonthValue();
            long[] acc = grouped.computeIfAbsent(key, k -> new long[2]);
            acc[0]++;
            acc[1] += row.amount();
        }

        List<MonthlyBucket> buckets = new ArrayList<>();
        for (Map.Entry<String, long[]> entry : grouped.entrySet()) {
            String[] parts = entry.getKey().split(":");
            buckets.add(new MonthlyBucket(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
                    (int) entry.getValue()[0], entry.getValue()[1]));
        }
        addZeroMonthIfNeeded(buckets, grouped, year, month);
        addZeroMonthIfNeeded(buckets, grouped, prevYear, prevMonth);

        buckets.sort(Comparator.comparingInt(MonthlyBucket::year)
                .thenComparingInt(MonthlyBucket::month)
                .reversed());
        return buckets;
    }

    private void addZeroMonthIfNeeded(List<MonthlyBucket> buckets, Map<String, long[]> grouped,
            Integer year, Integer month) {
        if (year == null || month == null) {
            return;
        }
        if (grouped.containsKey(year + ":" + month)) {
            return;
        }
        buckets.add(new MonthlyBucket(year, month, 0, 0L));
    }

    private List<YearlyBucket> findYearlyBuckets(Long merchantId, Integer year, String status) {
        if (year == null) {
            return List.of();
        }

        List<TransactionStatsSupport.Range> ranges = List.of(
                TransactionStatsSupport.yearRange(year),
                TransactionStatsSupport.yearRange(year - 1));

        Map<Integer, long[]> grouped = new HashMap<>();
        for (TransactionStatsSupport.TransactionRow row : support.findTransactions(ranges,
                toMerchantId(merchantId), status)) {
            long[] acc = grouped.computeIfAbsent(row.createdAt().getYear(), k -> new long[2]);
            acc[0]++;
            acc[1] += row.amount();
        }

        List<YearlyBucket> buckets = new ArrayList<>();
        for (Map.Entry<Integer, long[]> entry : grouped.entrySet()) {
            buckets.add(new YearlyBucket(entry.getKey(), (int) entry.getValue()[0], entry.getValue()[1]));
        }
        if (!grouped.containsKey(year)) {
            buckets.add(new YearlyBucket(year, 0, 0L));
        }
        if (!grouped.containsKey(year - 1)) {
            buckets.add(new YearlyBucket(year - 1, 0, 0L));
        }

        buckets.sort(Comparator.comparingInt(YearlyBucket::year).reversed());
        return buckets;
    }
}
