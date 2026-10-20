package com.sanedge.pointofsale.order.models;

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
@Document(collection = "orders")
public class Order extends BaseModel {
    @Id
    @Field("order_id")
    private String orderId;

    @Field("merchant_id")
    private Integer merchantId;

    @Field("cashier_id")
    private Integer cashierId;

    @Field("total_price")
    private Long totalPrice;
}
