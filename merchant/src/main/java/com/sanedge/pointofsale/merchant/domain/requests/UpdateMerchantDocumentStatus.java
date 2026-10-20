package com.sanedge.pointofsale.merchant.domain.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateMerchantDocumentStatus {

    @NotBlank(message = "Document ID wajib diisi")
    private String documentId;

    @NotNull(message = "Merchant ID wajib diisi")
    private Integer merchantId;

    @NotBlank(message = "Status wajib diisi")
    private String status;

    private String note;
}
