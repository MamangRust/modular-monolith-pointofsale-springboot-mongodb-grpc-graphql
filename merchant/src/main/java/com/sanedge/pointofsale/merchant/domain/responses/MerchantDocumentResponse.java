package com.sanedge.pointofsale.merchant.domain.responses;

import com.sanedge.pointofsale.merchant.models.MerchantDocument;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MerchantDocumentResponse {

    private String documentId;
    private Integer merchantId;
    private String documentType;
    private String documentUrl;
    private String status;
    private String note;

    public static MerchantDocumentResponse from(MerchantDocument doc) {
        if (doc == null) {
            return null;
        }
        return MerchantDocumentResponse.builder()
                .documentId(doc.getDocumentId())
                .merchantId(doc.getMerchantId())
                .documentType(doc.getDocumentType())
                .documentUrl(doc.getDocumentUrl())
                .status(doc.getStatus())
                .note(doc.getNote())
                .build();
    }
}
