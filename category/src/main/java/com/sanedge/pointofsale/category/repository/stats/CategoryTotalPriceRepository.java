package com.sanedge.pointofsale.category.repository.stats;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.category.models.CategoryMonthTotalPrice;
import com.sanedge.pointofsale.category.models.CategoryYearTotalPrice;

@Repository
public interface CategoryTotalPriceRepository {
    List<CategoryMonthTotalPrice> findMonthlyTotalPrice(
            Integer startYear,
            Integer startMonth,
            Integer endYear,
            Integer endMonth);

    List<CategoryYearTotalPrice> findYearlyTotalPrice(
            Integer year,
            Integer yearMinusOne);
}
