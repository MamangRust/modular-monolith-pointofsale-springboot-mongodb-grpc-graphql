package com.sanedge.pointofsale.category.repository.statsbymerchant;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.category.models.CategoryMonthTotalPrice;
import com.sanedge.pointofsale.category.models.CategoryYearTotalPrice;
import com.sanedge.pointofsale.category.repository.stats.CategoryTotalPriceRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryTotalPriceByMerchantRepositoryImpl implements CategoryTotalPriceByMerchantRepository {

    private final CategoryTotalPriceRepositoryImpl totalPriceHelper;

    @Override
    public List<CategoryMonthTotalPrice> findMonthlyTotalPriceByMerchant(
            Long merchantId, Integer startYear, Integer startMonth, Integer endYear, Integer endMonth) {

        Integer merId = toMerchantId(merchantId);
        LocalDateTime start = LocalDateTime.of(startYear, startMonth, 1, 0, 0);
        LocalDateTime endExclusive = LocalDateTime.of(endYear, endMonth, 1, 0, 0).plusMonths(1);

        Map<Integer, Long> revenueByMonth = totalPriceHelper.totalRevenueByMonth(start, endExclusive, merId, null);

        List<YearMonth> months = List.of(YearMonth.of(startYear, startMonth), YearMonth.of(endYear, endMonth));
        return months.stream()
                .sorted(java.util.Comparator
                        .comparingInt((YearMonth ym) -> ym.getYear())
                        .thenComparingInt(YearMonth::getMonthValue)
                        .reversed())
                .map(ym -> new CategoryMonthTotalPrice(
                        String.valueOf(ym.getYear()),
                        CategoryTotalPriceRepositoryImpl.fullMonthName(ym.getMonthValue()),
                        revenueByMonth.getOrDefault(ym.getYear() * 100 + ym.getMonthValue(), 0L)))
                .toList();
    }

    @Override
    public List<CategoryYearTotalPrice> findYearlyTotalPriceByMerchant(
            Long merchantId, Integer year, Integer yearMinusOne) {
        Integer merId = toMerchantId(merchantId);
        LocalDateTime start = LocalDateTime.of(yearMinusOne, 1, 1, 0, 0);
        LocalDateTime endExclusive = LocalDateTime.of(year, 1, 1, 0, 0).plusYears(1);

        Map<Integer, Long> revenueByYear = totalPriceHelper.totalRevenueByYear(start, endExclusive, merId, null);

        return CategoryTotalPriceRepositoryImpl.orderedDesc(List.of(year, yearMinusOne)).stream()
                .map(y -> new CategoryYearTotalPrice(String.valueOf(y), revenueByYear.getOrDefault(y, 0L)))
                .toList();
    }

    private static Integer toMerchantId(Long merchantId) {
        return merchantId == null ? null : Integer.valueOf(String.valueOf(merchantId));
    }
}
