package com.sanedge.pointofsale.category.repository.stats;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.category.models.CategoryMonthPrice;
import com.sanedge.pointofsale.category.models.CategoryYearPrice;

@Repository
public interface CategoryPriceRepository {

    List<CategoryMonthPrice> findMonthlyCategoryPrice(Integer startYear);

    List<CategoryYearPrice> findYearlyCategoryPrice(Integer startYear);
}
