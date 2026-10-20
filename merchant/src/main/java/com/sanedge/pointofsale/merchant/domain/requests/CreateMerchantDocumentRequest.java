package com.sanedge.pointofsale.merchant.domain.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMerchantDocumentRequest {

    @NotNull(message = "Merchant ID wajib diisi")
    private Integer merchantId;

    @NotBlank(message = "Document type wajib diisi")
    private String documentType;

    @NotBlank(message = "Document URL wajib diisi")
    private String documentUrl;
}
