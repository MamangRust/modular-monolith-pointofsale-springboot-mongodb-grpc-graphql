package com.sanedge.pointofsale.transaction.repository.statsbymerchant;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.transaction.models.TransactionMonthlyMethod;
import com.sanedge.pointofsale.transaction.models.TransactionYearMethod;

@Repository
public interface TransactionMethodByMerchantRepository {
    List<TransactionMonthlyMethod> findMonthlyTransactionMethodsSuccessByMerchant(
            Long merchantId,
            Integer year1,
            Integer month1,
            Integer year2,
            Integer month2);

    List<TransactionMonthlyMethod> findMonthlyTransactionMethodsFailedByMerchant(
            Long merchantId,
            Integer year1,
            Integer month1,
            Integer year2,
            Integer month2);

    List<TransactionYearMethod> findYearlyTransactionMethodsSuccessByMerchant(
            Long merchantId,
            Integer year);

    List<TransactionYearMethod> findYearlyTransactionMethodsFailedByMerchant(
            Long merchantId,
            Integer year);
}
