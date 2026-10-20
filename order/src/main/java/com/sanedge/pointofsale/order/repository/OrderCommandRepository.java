package com.sanedge.pointofsale.order.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.order.models.Order;

@Repository
public interface OrderCommandRepository extends MongoRepository<Order, String>, OrderCommandRepositoryCustom {
}
