package com.sanedge.pointofsale.orderitem.models;

import org.springframework.data.annotation.Id;
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
@Document(collection = "order_items")
public class OrderItem extends BaseModel {
    @Id
    @Field("order_item_id")
    private String orderItemId;

    @Field("order_id")
    private Integer orderId;

    @Field("product_id")
    private Integer productId;

    @Field("quantity")
    private Integer quantity;

    @Field("price")
    private Integer price;
}
