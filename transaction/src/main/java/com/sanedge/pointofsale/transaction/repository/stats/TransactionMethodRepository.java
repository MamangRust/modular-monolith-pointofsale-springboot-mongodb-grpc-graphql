package com.sanedge.pointofsale.transaction.repository.stats;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.transaction.models.TransactionMonthlyMethod;
import com.sanedge.pointofsale.transaction.models.TransactionYearMethod;

@Repository
public interface TransactionMethodRepository {

    List<TransactionMonthlyMethod> findMonthlyMethodsSuccess(
            Integer year1,
            Integer month1,
            Integer year2,
            Integer month2);

    List<TransactionMonthlyMethod> findMonthlyMethodsFailed(
            Integer year1,
            Integer month1,
            Integer year2,
            Integer month2);

    List<TransactionYearMethod> findYearlyMethodsSuccess(Integer year);

    List<TransactionYearMethod> findYearlyMethodsFailed(Integer year);
}
