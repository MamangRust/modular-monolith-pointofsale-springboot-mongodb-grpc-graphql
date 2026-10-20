package com.sanedge.pointofsale.merchant.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.merchant.models.Merchant;

@Repository
public interface MerchantQueryRepository extends MongoRepository<Merchant, String> {
    @Query("""
            { 'deleted_at': null, '$or': [
                { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'description': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'address': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'contact_email': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'contact_phone': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<Merchant> findMerchants(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            { 'deleted_at': null, '$or': [
                { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'description': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'address': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'contact_email': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'contact_phone': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<Merchant> findActiveMerchants(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            { 'deleted_at': { '$ne': null }, '$or': [
                { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'description': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'address': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'contact_email': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
                { 'contact_phone': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
            ] }
            """)
    Page<Merchant> findTrashedMerchants(@Param("keyword") String keyword, Pageable pageable);

    @Query("{ '_id': ?0, 'deleted_at': null }")
    Optional<Merchant> findMerchantById(@Param("merchantId") String merchantId);

    @Query("{ 'user_id': ?0, 'deleted_at': null }")
    Optional<Merchant> findByUserId(@Param("userId") Integer userId);
}
