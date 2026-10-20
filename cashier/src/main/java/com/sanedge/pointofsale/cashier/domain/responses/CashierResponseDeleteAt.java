package com.sanedge.pointofsale.cashier.domain.responses;

import com.sanedge.pointofsale.cashier.models.Cashier;
import com.sanedge.pointofsale.common.utils.Ids;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CashierResponseDeleteAt {
    private Integer id;
    private Integer merchantId;
    private String name;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public static CashierResponseDeleteAt from(Cashier cashier) {
        Long id = Ids.toLong(cashier.getCashierId());
        return CashierResponseDeleteAt.builder()
                .id(id == null ? null : id.intValue())
                .merchantId(cashier.getMerchantId())
                .name(cashier.getName())
                .createdAt(cashier.getCreatedAt().toString())
                .updatedAt(cashier.getUpdatedAt().toString())
                .deletedAt(cashier.getDeletedAt() != null ? cashier.getDeletedAt().toString() : null)
                .build();
    }
}
