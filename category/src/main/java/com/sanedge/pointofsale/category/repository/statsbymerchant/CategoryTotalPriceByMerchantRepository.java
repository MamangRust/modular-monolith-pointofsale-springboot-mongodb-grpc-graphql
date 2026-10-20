package com.sanedge.pointofsale.category.repository.statsbymerchant;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.category.models.CategoryMonthTotalPrice;
import com.sanedge.pointofsale.category.models.CategoryYearTotalPrice;

@Repository
public interface CategoryTotalPriceByMerchantRepository {
    List<CategoryMonthTotalPrice> findMonthlyTotalPriceByMerchant(
            Long merchantId,
            Integer startYear,
            Integer startMonth,
            Integer endYear,
            Integer endMonth);

    List<CategoryYearTotalPrice> findYearlyTotalPriceByMerchant(
            Long merchantId,
            Integer year,
            Integer yearMinusOne);
}
