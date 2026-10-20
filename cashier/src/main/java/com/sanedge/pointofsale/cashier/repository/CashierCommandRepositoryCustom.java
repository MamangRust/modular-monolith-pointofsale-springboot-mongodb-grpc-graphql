package com.sanedge.pointofsale.cashier.repository;

import com.sanedge.pointofsale.cashier.models.Cashier;

public interface CashierCommandRepositoryCustom {
    Cashier trashed(String cashierId);

    Cashier restore(String cashierId);

    Cashier deletePermanent(String cashierId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
