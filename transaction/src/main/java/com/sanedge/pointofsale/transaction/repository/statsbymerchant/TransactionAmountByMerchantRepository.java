package com.sanedge.pointofsale.transaction.repository.statsbymerchant;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.transaction.models.TransactionMonthlyAmountFailed;
import com.sanedge.pointofsale.transaction.models.TransactionMonthlyAmountSuccess;
import com.sanedge.pointofsale.transaction.models.TransactionYearlyAmountFailed;
import com.sanedge.pointofsale.transaction.models.TransactionYearlyAmountSuccess;

@Repository
public interface TransactionAmountByMerchantRepository {
    List<TransactionMonthlyAmountSuccess> findMonthlySuccessByMerchant(
            Long merchantId,
            Integer year,
            Integer month,
            Integer prevYear,
            Integer prevMonth);

    List<TransactionYearlyAmountSuccess> findYearlySuccessByMerchant(
            Long merchantId,
            Integer year);

    List<TransactionMonthlyAmountFailed> findMonthlyFailedByMerchant(
            Long merchantId,
            Integer year,
            Integer month,
            Integer prevYear,
            Integer prevMonth);

    List<TransactionYearlyAmountFailed> findYearlyFailedByMerchant(
            Long merchantId,
            Integer year);
}
