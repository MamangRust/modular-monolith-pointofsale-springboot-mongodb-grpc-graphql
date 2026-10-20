package com.sanedge.pointofsale.cashier.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.cashier.models.Cashier;

@Repository
public interface CashierQueryRepository extends MongoRepository<Cashier, String> {
        @Query("""
                        { '$or': [
                            { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        ] }
                        """)
        Page<Cashier> findAllCashiers(
                        @Param("keyword") String keyword,
                        Pageable pageable);

        @Query("""
                        { 'deleted_at': null, '$or': [
                            { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        ], 'merchant_id': ?#{#merchantId == null ? null : ?0} }
                        """)
        Page<Cashier> findByMerchants(
                        @Param("merchantId") Long merchantId,
                        @Param("keyword") String keyword,
                        Pageable pageable);

        @Query("""
                        { 'deleted_at': null, '$or': [
                            { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        ] }
                        """)
        Page<Cashier> findActiveCashiers(
                        @Param("keyword") String keyword,
                        Pageable pageable);

        @Query("""
                        { 'deleted_at': { '$ne': null }, '$or': [
                            { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        ] }
                        """)
        Page<Cashier> findTrashedCashiers(
                        @Param("keyword") String keyword,
                        Pageable pageable);

        @Query("{ '_id': ?0 }")
        Optional<Cashier> findByCashierId(@Param("cashierId") String cashierId);

        @Query("{ 'name': { '$regex': ?#{#name}, '$options': 'i' }, 'merchant_id': ?0, 'deleted_at': null }")
        Optional<Cashier> findByNameAndMerchantId(@Param("name") String name,
                        @Param("merchantId") Integer merchantId);
}
