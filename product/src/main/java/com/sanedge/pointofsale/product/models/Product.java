package com.sanedge.pointofsale.product.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.sanedge.pointofsale.common.models.BaseModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "products")
public class Product extends BaseModel {
    @Id
    @Field("product_id")
    private String productId;

    @Field("merchant_id")
    private Integer merchantId;

    @Field("category_id")
    private Integer categoryId;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("price")
    private Integer price;

    @Field("count_in_stock")
    private Integer countInStock = 0;

    @Field("brand")
    private String brand;

    @Field("weight")
    private Integer weight;

    @Indexed(unique = true)
    @Field("slug_product")
    private String slugProduct;

    @Field("image_product")
    private String imageProduct;

    @Indexed(unique = true)
    @Field("barcode")
    private String barcode;
}
