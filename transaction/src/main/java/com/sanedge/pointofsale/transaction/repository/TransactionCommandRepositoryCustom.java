package com.sanedge.pointofsale.transaction.repository;

import com.sanedge.pointofsale.transaction.models.Transaction;

public interface TransactionCommandRepositoryCustom {
    Transaction trashed(String transactionId);

    Transaction restore(String transactionId);

    Transaction deletePermanent(String transactionId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
