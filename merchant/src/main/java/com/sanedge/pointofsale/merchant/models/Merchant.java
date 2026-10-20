package com.sanedge.pointofsale.merchant.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.sanedge.pointofsale.merchant.domain.requests.CreateMerchantRequest;
import com.sanedge.pointofsale.merchant.domain.requests.UpdateMerchantRequest;
import com.sanedge.pointofsale.common.enums.Status;
import com.sanedge.pointofsale.common.models.BaseModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "merchants")
public class Merchant extends BaseModel {
    @Id
    @Field("merchant_id")
    private String merchantId;

    @Field("user_id")
    private Integer userId;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("address")
    private String address;

    @Field("contact_email")
    private String contactEmail;

    @Field("contact_phone")
    private String contactPhone;

    @Field("status")
    private Status status = Status.PENDING;

    public static Merchant fromCreateRequest(CreateMerchantRequest req) {
        Merchant merchant = new Merchant();
        merchant.setUserId(req.getUserId());
        merchant.setName(req.getName());
        merchant.setDescription(req.getDescription());
        merchant.setAddress(req.getAddress());
        merchant.setContactEmail(req.getContactEmail());
        merchant.setContactPhone(req.getContactPhone());

        try {
            merchant.setStatus(Status.valueOf(req.getStatus().toLowerCase()));
        } catch (IllegalArgumentException | NullPointerException e) {
            merchant.setStatus(Status.PENDING);
        }

        return merchant;
    }

    public void updateFromRequest(UpdateMerchantRequest req) {
        this.userId = req.getUserId();
        this.name = req.getName();
        this.description = req.getDescription();
        this.address = req.getAddress();
        this.contactEmail = req.getContactEmail();
        this.contactPhone = req.getContactPhone();

        try {
            this.status = Status.valueOf(req.getStatus().toLowerCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            this.setStatus(Status.PENDING);
        }
    }
}
