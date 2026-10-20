package com.sanedge.pointofsale.category.repository.statsbyid;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.category.models.CategoryMonthTotalPrice;
import com.sanedge.pointofsale.category.models.CategoryYearTotalPrice;

@Repository
public interface CategoryTotalPriceByIdRepository {

    List<CategoryMonthTotalPrice> findMonthlyTotalPriceByCategoryId(
            Long categoryId,
            Integer startYear,
            Integer startMonth,
            Integer endYear,
            Integer endMonth);

    List<CategoryYearTotalPrice> findYearlyTotalPriceByCategoryId(
            Long categoryId,
            Integer year,
            Integer yearMinusOne);
}
