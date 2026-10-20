package com.sanedge.pointofsale.transaction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.sanedge.pointofsale.BaseIntegrationTest;
import com.sanedge.pointofsale.common.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.common.enums.PaymentStatus;
import com.sanedge.pointofsale.pb.merchant.ApiResponseMerchant;
import com.sanedge.pointofsale.pb.merchant.MerchantQueryServiceGrpc;
import com.sanedge.pointofsale.pb.merchant.MerchantResponse;
import com.sanedge.pointofsale.pb.order.ApiResponseOrder;
import com.sanedge.pointofsale.pb.order.OrderQueryServiceGrpc;
import com.sanedge.pointofsale.pb.order.OrderResponse;
import com.sanedge.pointofsale.pb.order_item.ApiOrderItemsResponse;
import com.sanedge.pointofsale.pb.order_item.OrderItemQueryServiceGrpc;
import com.sanedge.pointofsale.pb.order_item.OrderItemResponse;
import com.sanedge.pointofsale.transaction.domain.requests.CreateTransactionRequest;
import com.sanedge.pointofsale.transaction.domain.requests.FindAllTransactionRequest;
import com.sanedge.pointofsale.transaction.domain.requests.UpdateTransactionRequest;
import com.sanedge.pointofsale.transaction.domain.responses.TransactionResponse;
import com.sanedge.pointofsale.transaction.domain.responses.TransactionResponseDeleteAt;
import com.sanedge.pointofsale.transaction.models.Transaction;
import com.sanedge.pointofsale.transaction.repository.TransactionCommandRepository;

import jakarta.validation.Validator;

public class TransactionServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TransactionQueryService queryService;

    @Autowired
    private TransactionCommandService commandService;

    @Autowired
    private TransactionCommandRepository transactionCommandRepository;

    @MockitoBean
    private Validator validator;

    @MockitoBean
    private MerchantQueryServiceGrpc.MerchantQueryServiceBlockingStub merchantQueryStub;

    @MockitoBean
    private OrderQueryServiceGrpc.OrderQueryServiceBlockingStub orderQueryStub;

    @MockitoBean
    private OrderItemQueryServiceGrpc.OrderItemQueryServiceBlockingStub orderItemQueryStub;

    @BeforeEach
    void setupMocks() {
        when(merchantQueryStub.findById(any()))
                .thenReturn(ApiResponseMerchant.newBuilder()
                        .setStatus("success")
                        .setMessage("ok")
                        .setData(MerchantResponse.newBuilder().setMerchantId(1).build())
                        .build());

        when(orderQueryStub.findById(any()))
                .thenReturn(ApiResponseOrder.newBuilder()
                        .setStatus("success")
                        .setMessage("ok")
                        .setData(OrderResponse.newBuilder()
                                .setOrderId(1)
                                .setMerchantId(1)
                                .setCashierId(1)
                                .setTotalPrice(35000L)
                                .build())
                        .build());

        when(orderItemQueryStub.findByOrder(any()))
                .thenReturn(ApiOrderItemsResponse.newBuilder()
                        .setStatus("success")
                        .setMessage("ok")
                        .addData(OrderItemResponse.newBuilder()
                                .setOrderItemId(1)
                                .setOrderId(1)
                                .setProductId(101)
                                .setQuantity(2)
                                .setPrice(15000)
                                .build())
                        .build());
    }

    @Test
    void testAllTransactionServiceMethods() {
        Transaction tx = new Transaction();
        tx.setOrderId(1);
        tx.setMerchantId(1);
        tx.setPaymentMethod("GOPAY");
        tx.setAmount(38500);
        tx.setStatus(PaymentStatus.PENDING);
        tx = transactionCommandRepository.save(tx);

        String id = tx.getTransactionId();

        // 1. Create
        CreateTransactionRequest createReq = new CreateTransactionRequest();
        createReq.setOrderID(1);
        createReq.setMerchantID(1);
        createReq.setPaymentMethod("OVO");
        createReq.setAmount(40000);
        createReq.setPaymentStatus("PENDING");

        ApiResponse<TransactionResponse> createResp = commandService.create(createReq);
        assertThat(createResp.getStatus()).isEqualTo("success");

        // 2. Find All
        FindAllTransactionRequest findReq = new FindAllTransactionRequest();
        findReq.setSearch("GOPAY");
        findReq.setPage(1);
        findReq.setPageSize(10);

        ApiResponsePagination<List<TransactionResponse>> allResp = queryService.findAllTransactions(findReq);
        assertThat(allResp.getStatus()).isEqualTo("success");

        // 3. Find By ID
        ApiResponse<TransactionResponse> byIdResp = queryService.findById(Integer.valueOf(id));
        assertThat(byIdResp.getStatus()).isEqualTo("success");

        // 4. Update
        UpdateTransactionRequest updateReq = new UpdateTransactionRequest();
        updateReq.setTransactionID(Integer.valueOf(id));
        updateReq.setOrderID(1);
        updateReq.setMerchantID(1);
        updateReq.setPaymentMethod("CREDIT_CARD");
        updateReq.setAmount(45000);
        updateReq.setPaymentStatus("SUCCESS");

        ApiResponse<TransactionResponse> updateResp = commandService.update(updateReq);
        assertThat(updateResp.getStatus()).isEqualTo("success");

        // 5. Find By Active
        ApiResponsePagination<List<TransactionResponseDeleteAt>> activeResp = queryService.findByActive(findReq);
        assertThat(activeResp.getStatus()).isEqualTo("success");

        // 6. Trash
        ApiResponse<TransactionResponseDeleteAt> trashResp = commandService.trash(Integer.valueOf(id));
        assertThat(trashResp.getStatus()).isEqualTo("success");

        // 7. Find By Trashed
        ApiResponsePagination<List<TransactionResponseDeleteAt>> trashedResp = queryService.findByTrashed(findReq);
        assertThat(trashedResp.getStatus()).isEqualTo("success");

        // 8. Restore
        ApiResponse<TransactionResponseDeleteAt> restoreResp = commandService.restore(Integer.valueOf(id));
        assertThat(restoreResp.getStatus()).isEqualTo("success");

        // Trash again before delete permanent
        ApiResponse<TransactionResponseDeleteAt> trashAgainResp = commandService.trash(Integer.valueOf(id));
        assertThat(trashAgainResp.getStatus()).isEqualTo("success");

        // 9. Delete Permanent
        ApiResponse<Boolean> delPermResp = commandService.delete(Integer.valueOf(id));
        assertThat(delPermResp.getStatus()).isEqualTo("success");

        // 10. Restore All
        ApiResponse<Boolean> restoreAllResp = commandService.restoreAll();
        assertThat(restoreAllResp.getStatus()).isEqualTo("success");

        // 11. Delete All Permanent
        ApiResponse<Boolean> delAllResp = commandService.deleteAll();
        assertThat(delAllResp.getStatus()).isEqualTo("success");
    }
}
