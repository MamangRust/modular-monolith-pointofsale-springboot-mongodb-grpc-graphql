package com.sanedge.pointofsale.transaction.repository;

import com.sanedge.pointofsale.transaction.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionQueryRepository extends MongoRepository<Transaction, String> {
    @Query("""
            { '$or': [
                { '_id': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'payment_method': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<Transaction> findTransactions(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            { 'deleted_at': null, '$or': [
                { '_id': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'payment_method': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<Transaction> findActiveTransactions(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            { 'deleted_at': { '$ne': null }, '$or': [
                { '_id': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'payment_method': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<Transaction> findTrashedTransactions(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            { 'deleted_at': null, '$or': [
                { 'payment_method': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'payment_status': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ], 'merchant_id': ?#{#merchantId == null ? null : ?1} }
            """)
    Page<Transaction> findTransactionsByMerchant(
            @Param("keyword") String keyword,
            @Param("merchantId") Integer merchantId,
            Pageable pageable);

    Optional<Transaction> findByTransactionId(String transactionId);

    Optional<Transaction> findByOrderId(Integer orderId);
}
