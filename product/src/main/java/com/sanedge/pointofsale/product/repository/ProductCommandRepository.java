package com.sanedge.pointofsale.product.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.product.models.Product;

@Repository
public interface ProductCommandRepository extends MongoRepository<Product, String>, ProductCommandRepositoryCustom {
}
