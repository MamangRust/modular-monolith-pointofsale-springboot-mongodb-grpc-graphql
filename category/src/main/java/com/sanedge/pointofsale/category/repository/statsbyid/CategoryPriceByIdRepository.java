package com.sanedge.pointofsale.category.repository.statsbyid;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.category.models.CategoryMonthPrice;
import com.sanedge.pointofsale.category.models.CategoryYearPrice;

@Repository
public interface CategoryPriceByIdRepository {

    List<CategoryMonthPrice> findMonthlyCategoryPriceById(
            Long categoryId,
            Integer startDate);

    List<CategoryYearPrice> findYearlyCategoryPriceById(
            Long categoryId,
            Integer startDate);
}
