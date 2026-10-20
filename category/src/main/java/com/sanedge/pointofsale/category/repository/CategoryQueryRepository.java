package com.sanedge.pointofsale.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.category.models.Category;

@Repository
public interface CategoryQueryRepository extends MongoRepository<Category, String> {
        @Query("""
                        { 'deleted_at': null, '$or': [
                            { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        ] }
                        """)
        Page<Category> findCategories(@Param("keyword") String keyword, Pageable pageable);

        @Query("""
                        { 'deleted_at': null, '$or': [
                            { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        ] }
                        """)
        Page<Category> findActiveCategories(@Param("keyword") String keyword, Pageable pageable);

        @Query("""
                        { 'deleted_at': { '$ne': null }, '$or': [
                            { 'name': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
                        ] }
                        """)
        Page<Category> findTrashedCategories(@Param("keyword") String keyword, Pageable pageable);

        @Query(value = "{ 'deleted_at': null }", fields = "{ '_id': 1, 'name': 1 }")
        List<Category> findNameAndId();

        Optional<Category> findByName(String name);

        @Query("{ '_id': ?0, 'deleted_at': null }")
        Optional<Category> findCategoryById(@Param("categoryId") String categoryId);

}
