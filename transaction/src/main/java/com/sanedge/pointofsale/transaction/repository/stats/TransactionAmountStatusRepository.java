package com.sanedge.pointofsale.transaction.repository.stats;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.transaction.models.TransactionMonthlyAmountFailed;
import com.sanedge.pointofsale.transaction.models.TransactionMonthlyAmountSuccess;
import com.sanedge.pointofsale.transaction.models.TransactionYearlyAmountFailed;
import com.sanedge.pointofsale.transaction.models.TransactionYearlyAmountSuccess;

@Repository
public interface TransactionAmountStatusRepository {
    List<TransactionMonthlyAmountSuccess> findMonthlyTransactionSuccess(
            Integer year,
            Integer month,
            Integer prevYear,
            Integer prevMonth);

    List<TransactionYearlyAmountSuccess> findYearlyTransactionSuccess(Integer year);

    List<TransactionMonthlyAmountFailed> findMonthlyTransactionFailed(
            Integer year,
            Integer month,
            Integer prevYear,
            Integer prevMonth);

    List<TransactionYearlyAmountFailed> findYearlyTransactionFailed(Integer year);
}
