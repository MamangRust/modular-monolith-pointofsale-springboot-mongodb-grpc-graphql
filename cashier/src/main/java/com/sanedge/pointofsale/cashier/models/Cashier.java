package com.sanedge.pointofsale.cashier.models;

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
@Document(collection = "cashiers")
public class Cashier extends BaseModel {
    @Id
    @Field("cashier_id")
    private String cashierId;

    @Field("merchant_id")
    private Integer merchantId;

    @Field("user_id")
    private Integer userId;

    @Field("name")
    private String name;
}
