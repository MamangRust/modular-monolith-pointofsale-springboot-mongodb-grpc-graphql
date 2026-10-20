package com.sanedge.pointofsale.merchant.service.impl;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.common.adapter.model.User;
import com.sanedge.pointofsale.common.adapter.user.UserQueryPort;
import com.sanedge.pointofsale.common.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.common.exception.ResourceNotFoundException;
import com.sanedge.pointofsale.common.observability.TracingMetrics;
import com.sanedge.pointofsale.common.outbox.Outbox;
import com.sanedge.pointofsale.common.outbox.OutboxEventFactory;
import com.sanedge.pointofsale.common.outbox.OutboxRepository;
import com.sanedge.pointofsale.merchant.domain.requests.CreateMerchantDocumentRequest;
import com.sanedge.pointofsale.merchant.domain.requests.UpdateMerchantDocumentStatus;
import com.sanedge.pointofsale.merchant.domain.responses.MerchantDocumentResponse;
import com.sanedge.pointofsale.merchant.models.Merchant;
import com.sanedge.pointofsale.merchant.models.MerchantDocument;
import com.sanedge.pointofsale.merchant.repository.MerchantDocumentRepository;
import com.sanedge.pointofsale.merchant.repository.MerchantQueryRepository;
import com.sanedge.pointofsale.merchant.service.MerchantDocumentCommandService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Command side of merchant documents.
 *
 * <p>Two of the merchant email topics are produced here (document created /
 * document status updated). Events are written to the shared {@code outbox}
 * collection via {@link OutboxRepository} and relayed to Kafka by the shared
 * {@code OutboxPublisher}. Lives in the merchant module — there is no dedicated
 * merchant_document module.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantDocumentCommandImplService implements MerchantDocumentCommandService {

    private static final String TOPIC_DOCUMENT_CREATE = "email-service-topic-merchant-document-create";
    private static final String TOPIC_DOCUMENT_UPDATE_STATUS = "email-service-topic-merchant-document-update-status";

    private final MerchantQueryRepository merchantQueryRepository;
    private final MerchantDocumentRepository merchantDocumentRepository;
    private final OutboxRepository outboxRepository;
    private final UserQueryPort userQueryPort;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<MerchantDocumentResponse> create(CreateMerchantDocumentRequest req) {
        Attributes attrs = Attributes.builder()
                .put("merchant.id", String.valueOf(req.getMerchantId()))
                .build();

        return tracingMetrics.traceAndMeasure("createMerchantDocument", "create", attrs, () -> {
            Merchant merchant = merchantQueryRepository.findMerchantById(String.valueOf(req.getMerchantId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

            MerchantDocument doc = new MerchantDocument();
            doc.setMerchantId(req.getMerchantId());
            doc.setDocumentType(req.getDocumentType());
            doc.setDocumentUrl(req.getDocumentUrl());
            doc.setStatus("PENDING");

            MerchantDocument saved = merchantDocumentRepository.save(doc);
            log.info("✅ Merchant document created | id={}", saved.getDocumentId());

            sendDocumentEmail(merchant, TOPIC_DOCUMENT_CREATE,
                    "Merchant Document Submitted",
                    String.format(
                            "Hello,\n\nA new %s document has been submitted for merchant '%s' and is awaiting review.\n\nRegards,\nSupport Team",
                            saved.getDocumentType(), merchant.getName()));

            return ApiResponse.<MerchantDocumentResponse>builder()
                    .status("success")
                    .message("Merchant document created successfully")
                    .data(MerchantDocumentResponse.from(saved))
                    .build();
        });
    }

    @Override
    public ApiResponse<MerchantDocumentResponse> updateStatus(UpdateMerchantDocumentStatus req) {
        Attributes attrs = Attributes.builder()
                .put("doc.id", req.getDocumentId())
                .build();

        return tracingMetrics.traceAndMeasure("updateMerchantDocumentStatus", "update_status", attrs, () -> {
            MerchantDocument doc = merchantDocumentRepository.findById(req.getDocumentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Merchant document not found"));

            Merchant merchant = merchantQueryRepository.findMerchantById(String.valueOf(req.getMerchantId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

            doc.setStatus(req.getStatus());
            doc.setNote(req.getNote());

            MerchantDocument saved = merchantDocumentRepository.save(doc);
            log.info("✅ Merchant document status updated | id={} status={}", saved.getDocumentId(), saved.getStatus());

            sendDocumentEmail(merchant, TOPIC_DOCUMENT_UPDATE_STATUS,
                    "Merchant Document Status Updated",
                    String.format(
                            "Hello,\n\nYour %s document for merchant '%s' has been reviewed. New status: %s.\n\nRegards,\nSupport Team",
                            saved.getDocumentType(), merchant.getName(), saved.getStatus()));

            return ApiResponse.<MerchantDocumentResponse>builder()
                    .status("success")
                    .message("Merchant document status updated successfully")
                    .data(MerchantDocumentResponse.from(saved))
                    .build();
        });
    }

    /**
     * Enqueues a document email to the merchant owner's address.
     *
     * <p>Best-effort: a failed recipient lookup is logged and skipped so it never
     * aborts the document write.
     */
    private void sendDocumentEmail(Merchant merchant, String topic, String subject, String body) {
        try {
            User owner = userQueryPort.findById(merchant.getUserId().intValue());
            OutboxEventFactory.Envelope envelope = OutboxEventFactory.email(topic, owner.email(), subject, body);

            Outbox event = new Outbox();
            event.setAggregateType("MERCHANT");
            event.setAggregateId(String.valueOf(merchant.getMerchantId()));
            event.setTopic(topic);
            event.setPayload(envelope.payload());
            outboxRepository.enqueue(event);
        } catch (RuntimeException e) {
            log.warn("⚠️ Failed to enqueue document email | topic={} merchantId={}: {}",
                    topic, merchant.getMerchantId(), e.getMessage());
        }
    }
}
