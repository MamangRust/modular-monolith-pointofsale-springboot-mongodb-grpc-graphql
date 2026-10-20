package com.sanedge.pointofsale.merchant.service;

import com.sanedge.pointofsale.common.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.merchant.domain.requests.CreateMerchantDocumentRequest;
import com.sanedge.pointofsale.merchant.domain.requests.UpdateMerchantDocumentStatus;
import com.sanedge.pointofsale.merchant.domain.responses.MerchantDocumentResponse;

public interface MerchantDocumentCommandService {

    ApiResponse<MerchantDocumentResponse> create(CreateMerchantDocumentRequest req);

    ApiResponse<MerchantDocumentResponse> updateStatus(UpdateMerchantDocumentStatus req);
}
