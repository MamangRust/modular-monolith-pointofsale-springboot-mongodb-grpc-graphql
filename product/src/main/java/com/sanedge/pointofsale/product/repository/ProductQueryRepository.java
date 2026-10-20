package com.sanedge.pointofsale.product.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.product.models.Product;

@Repository
public interface ProductQueryRepository extends MongoRepository<Product, String> {
    @Query("""
            { 'deleted_at': null, 'name': { '$regex': ?#{#search == null ? '' : #search}, '$options': 'i' } }
            """)
    Page<Product> findAllProducts(@Param("search") String search, Pageable pageable);

    @Query("""
            { 'deleted_at': null, 'name': { '$regex': ?#{#search == null ? '' : #search}, '$options': 'i' } }
            """)
    Page<Product> findActiveProducts(@Param("search") String search, Pageable pageable);

    @Query("""
            { 'deleted_at': { '$ne': null }, 'name': { '$regex': ?#{#search == null ? '' : #search}, '$options': 'i' } }
            """)
    Page<Product> findTrashedProducts(@Param("search") String search, Pageable pageable);

    @Query("{ '_id': ?0 }")
    Optional<Product> findProductById(@Param("productId") String productId);

    @Query("""
            { 'deleted_at': null,
              'merchant_id': ?0,
              'name': { '$regex': ?#{#search == null ? '' : #search}, '$options': 'i' },
              '$and': [
                { '$or': [ { 'category_id': ?2 }, { 'category_id': null } ] },
                { '$or': [ { 'price': { '$gte': ?3 } }, { 'price': null } ] },
                { '$or': [ { 'price': { '$lte': ?4 } }, { 'price': null } ] }
              ] }
            """)
    Page<Product> findProductsByMerchant(
            @Param("merchantId") Integer merchantId,
            @Param("search") String search,
            @Param("categoryId") Integer categoryId,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable);

    @Query("""
            { 'deleted_at': null,
              'category_id': ?0,
              'name': { '$regex': ?#{#search == null ? '' : #search}, '$options': 'i' },
              '$and': [
                { '$or': [ { 'price': { '$gte': ?2 } }, { 'price': null } ] },
                { '$or': [ { 'price': { '$lte': ?3 } }, { 'price': null } ] }
              ] }
            """)
    Page<Product> findProductsByCategoryId(
            @Param("categoryId") Integer categoryId,
            @Param("search") String search,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable);
}
