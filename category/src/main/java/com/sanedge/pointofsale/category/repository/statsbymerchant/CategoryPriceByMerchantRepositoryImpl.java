package com.sanedge.pointofsale.category.repository.statsbymerchant;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.category.models.CategoryMonthPrice;
import com.sanedge.pointofsale.category.models.CategoryYearPrice;
import com.sanedge.pointofsale.category.repository.CategoryStatsSupport;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryPriceByMerchantRepositoryImpl implements CategoryPriceByMerchantRepository {

    private final CategoryStatsSupport support;

    @Override
    public List<CategoryMonthPrice> findMonthlyCategoryPriceByMerchant(Long merchantId, Integer startDate) {
        Integer merId = toMerchantId(merchantId);
        LocalDateTime start = LocalDateTime.of(startDate, 1, 1, 0, 0);
        List<CategoryStatsSupport.JoinedRow> rows = support.findJoinedRows(start, start.plusYears(1), merId, null);

        Map<String, List<CategoryStatsSupport.JoinedRow>> grouped = rows.stream()
                .collect(Collectors.groupingBy(row -> (row.orderCreatedAt().getMonthValue() + ":"
                        + row.categoryId())));

        record Entry(int monthNum, CategoryMonthPrice dto) {
        }

        List<Entry> entries = grouped.values().stream().map(bucket -> {
            int monthNum = bucket.get(0).orderCreatedAt().getMonthValue();
            String monthName = Month.of(monthNum).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            int orderCount = (int) bucket.stream().map(CategoryStatsSupport.JoinedRow::orderId).distinct().count();
            int itemsSold = (int) bucket.stream().mapToLong(CategoryStatsSupport.JoinedRow::quantity).sum();
            long totalRevenue = bucket.stream().mapToLong(CategoryStatsSupport.JoinedRow::orderTotalPrice).sum();
            Integer categoryId = bucket.get(0).categoryId();
            return new Entry(monthNum, new CategoryMonthPrice(monthName, categoryId,
                    bucket.get(0).categoryName(), orderCount, itemsSold, totalRevenue));
        }).toList();

        return entries.stream()
                .sorted(Comparator.comparingInt(Entry::monthNum)
                        .thenComparing(e -> -e.dto().getTotalRevenue()))
                .map(Entry::dto)
                .toList();
    }

    @Override
    public List<CategoryYearPrice> findYearlyCategoryPriceByMerchant(Long merchantId, Integer startDate) {
        Integer merId = toMerchantId(merchantId);
        LocalDateTime start = LocalDateTime.of(startDate - 4, 1, 1, 0, 0);
        List<CategoryStatsSupport.JoinedRow> rows = support.findJoinedRows(start, start.plusYears(5), merId, null);

        Map<String, List<CategoryStatsSupport.JoinedRow>> grouped = rows.stream()
                .collect(Collectors.groupingBy(row -> (row.orderCreatedAt().getYear() + ":"
                        + row.categoryId())));

        return grouped.values().stream().map(bucket -> {
            int rowYear = bucket.get(0).orderCreatedAt().getYear();
            int orderCount = (int) bucket.stream().map(CategoryStatsSupport.JoinedRow::orderId).distinct().count();
            int itemsSold = (int) bucket.stream().mapToLong(CategoryStatsSupport.JoinedRow::quantity).sum();
            long totalRevenue = bucket.stream().mapToLong(CategoryStatsSupport.JoinedRow::orderTotalPrice).sum();
            int uniqueProductsSold = (int) bucket.stream().map(CategoryStatsSupport.JoinedRow::productId).distinct()
                    .count();
            Integer categoryId = bucket.get(0).categoryId();
            return new CategoryYearPrice(String.valueOf(rowYear), categoryId,
                    bucket.get(0).categoryName(), orderCount, itemsSold, totalRevenue, uniqueProductsSold);
        })
                .sorted(Comparator.comparingInt((CategoryYearPrice p) -> Integer.parseInt(p.getYear()))
                        .thenComparing(p -> -p.getTotalRevenue()))
                .toList();
    }

    private static Integer toMerchantId(Long merchantId) {
        return merchantId == null ? null : Integer.valueOf(String.valueOf(merchantId));
    }
}
