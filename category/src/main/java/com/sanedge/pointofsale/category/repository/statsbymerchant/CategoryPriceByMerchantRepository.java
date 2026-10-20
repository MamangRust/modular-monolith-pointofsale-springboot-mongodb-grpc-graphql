package com.sanedge.pointofsale.category.repository.statsbymerchant;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.category.models.CategoryMonthPrice;
import com.sanedge.pointofsale.category.models.CategoryYearPrice;

@Repository
public interface CategoryPriceByMerchantRepository {

    List<CategoryMonthPrice> findMonthlyCategoryPriceByMerchant(
            Long merchantId,
            Integer startDate);

    List<CategoryYearPrice> findYearlyCategoryPriceByMerchant(
            Long merchantId,
            Integer startDate);
}
