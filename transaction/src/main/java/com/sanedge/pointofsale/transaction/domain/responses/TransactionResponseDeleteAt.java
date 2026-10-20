package com.sanedge.pointofsale.transaction.domain.responses;

import com.sanedge.pointofsale.common.utils.Ids;
import com.sanedge.pointofsale.transaction.models.Transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDeleteAt {
    private Long id;
    private Integer orderId;
    private Integer merchantId;
    private String paymentMethod;
    private Integer amount;
    private String paymentStatus;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public static TransactionResponseDeleteAt from(Transaction entity) {
        return TransactionResponseDeleteAt.builder()
                .id(Ids.toLong(entity.getTransactionId()))
                .orderId(entity.getOrderId())
                .merchantId(entity.getMerchantId())
                .paymentMethod(entity.getPaymentMethod())
                .amount(entity.getAmount())
                .paymentStatus(entity.getStatus().toString())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .deletedAt(entity.getDeletedAt() != null ? entity.getDeletedAt().toString() : null)
                .build();
    }
}