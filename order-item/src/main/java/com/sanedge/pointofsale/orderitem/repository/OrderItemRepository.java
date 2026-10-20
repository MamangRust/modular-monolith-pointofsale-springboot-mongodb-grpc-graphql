package com.sanedge.pointofsale.orderitem.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.orderitem.models.OrderItem;

@Repository
public interface OrderItemRepository
        extends MongoRepository<OrderItem, String> {

    @Query("{ 'order_id': ?0, 'deleted_at': null }")
    List<OrderItem> findOrderItemByOrder(@Param("orderId") Integer orderId);
}
