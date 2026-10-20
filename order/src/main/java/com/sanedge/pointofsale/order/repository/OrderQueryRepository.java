package com.sanedge.pointofsale.order.repository;

import com.sanedge.pointofsale.order.models.Order;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderQueryRepository extends MongoRepository<Order, String> {
    @Query("""
            { '$or': [
                { '_id': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'cashier_id': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<Order> findOrders(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            { 'merchant_id': ?0, '$or': [
                { '_id': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'cashier_id': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<Order> findOrdersByMerchant(@Param("merchantId") Long merchantId,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("""
            { 'deleted_at': null, '$or': [
                { '_id': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'cashier_id': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<Order> findActiveOrders(@Param("keyword") String keyword, Pageable pageable);

    @Query("{ '_id': ?0, 'deleted_at': null }")
    Optional<Order> findOrderById(@Param("orderId") String orderId);

    @Query("""
            { 'deleted_at': { '$ne': null }, '$or': [
                { '_id': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'cashier_id': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<Order> findTrashedOrders(@Param("keyword") String keyword, Pageable pageable);
}
