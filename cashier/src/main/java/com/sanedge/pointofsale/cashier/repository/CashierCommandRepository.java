package com.sanedge.pointofsale.cashier.repository;

import com.sanedge.pointofsale.cashier.models.Cashier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashierCommandRepository extends MongoRepository<Cashier, String>, CashierCommandRepositoryCustom {
}
