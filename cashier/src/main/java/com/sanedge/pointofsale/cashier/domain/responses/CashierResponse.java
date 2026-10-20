package com.sanedge.pointofsale.cashier.domain.responses;

import com.sanedge.pointofsale.cashier.models.Cashier;
import com.sanedge.pointofsale.common.utils.Ids;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CashierResponse {
    private Integer id;
    private Integer merchantId;
    private String name;
    private String createdAt;
    private String updatedAt;

    public static CashierResponse from(Cashier cashier) {
        Long id = Ids.toLong(cashier.getCashierId());
        return CashierResponse.builder()
                .id(id == null ? null : id.intValue())
                .merchantId(cashier.getMerchantId())
                .name(cashier.getName())
                .createdAt(cashier.getCreatedAt().toString())
                .updatedAt(cashier.getUpdatedAt().toString())
                .build();
    }
}