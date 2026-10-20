package com.sanedge.pointofsale.merchant.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.sanedge.pointofsale.common.models.BaseModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Merchant verification document (collection {@code merchant_documents}).
 *
 * <p>Lives in the merchant module (no dedicated module) and backs the
 * {@code email-service-topic-merchant-document-*} email topics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "merchant_documents")
public class MerchantDocument extends BaseModel {

    @Id
    @Field("document_id")
    private String documentId;

    @Field("merchant_id")
    private Integer merchantId;

    @Field("document_type")
    private String documentType;

    @Field("document_url")
    private String documentUrl;

    @Field("status")
    private String status = "PENDING";

    @Field("note")
    private String note;
}
