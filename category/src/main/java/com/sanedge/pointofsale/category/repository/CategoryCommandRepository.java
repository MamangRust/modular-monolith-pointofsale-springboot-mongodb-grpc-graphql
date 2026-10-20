package com.sanedge.pointofsale.category.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.category.models.Category;

@Repository
public interface CategoryCommandRepository extends MongoRepository<Category, String>, CategoryCommandRepositoryCustom {
}
