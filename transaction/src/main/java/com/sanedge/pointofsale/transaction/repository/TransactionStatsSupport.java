package com.sanedge.pointofsale.transaction.repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Mendukung query statistik modul transaction terhadap satu koleksi
 * {@code transactions}. Semua perhitungan (group by bulan/tahun, status,
 * payment method) dilakukan di sisi aplikasi agar konsisten dengan tipe id
 * dan format tanggal MongoDB.
 */
@Component
@RequiredArgsConstructor
public class TransactionStatsSupport {

    private final MongoTemplate mongoTemplate;

    public record Range(LocalDateTime startInclusive, LocalDateTime endExclusive) {
    }

    public record TransactionRow(LocalDateTime createdAt, Integer merchantId, String paymentMethod,
            String paymentStatus, int amount) {
    }

    public static Range monthRange(Integer year, Integer month) {
        if (year == null || month == null) {
            return null;
        }
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        return new Range(start, start.plusMonths(1));
    }

    public static Range yearRange(Integer year) {
        if (year == null) {
            return null;
        }
        LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0);
        return new Range(start, start.plusYears(1));
    }

    /**
     * Distinct payment method dari transaksi aktif (deleted_at null), opsional
     * difilter per merchant.
     */
    public List<String> findDistinctPaymentMethods(Integer merchantId) {
        Criteria criteria = Criteria.where("deleted_at").isNull();
        if (merchantId != null) {
            criteria = criteria.and("merchant_id").is(merchantId);
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("payment_method"));

        return mongoTemplate.aggregate(aggregation, "transactions", Document.class).getMappedResults().stream()
                .map(doc -> doc.getString("_id"))
                .filter(value -> value != null && !value.isEmpty())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    /**
     * Transaksi aktif dalam satu atau beberapa rentang waktu (digabung dengan
     * $or), opsional difilter per merchant dan payment status (case-insensitive).
     */
    public List<TransactionRow> findTransactions(List<Range> ranges, Integer merchantId, String paymentStatus) {
        if (ranges == null || ranges.isEmpty()) {
            return List.of();
        }

        Criteria criteria = Criteria.where("deleted_at").isNull();
        if (merchantId != null) {
            criteria = criteria.and("merchant_id").is(merchantId);
        }
        if (paymentStatus != null) {
            criteria = criteria.and("payment_status").in(List.of(
                    paymentStatus.toUpperCase(Locale.ROOT),
                    paymentStatus.toLowerCase(Locale.ROOT)));
        }
        if (ranges.size() == 1) {
            Range range = ranges.get(0);
            criteria = criteria.and("created_at").gte(range.startInclusive()).lt(range.endExclusive());
        } else {
            List<Range> validRanges = ranges.stream().filter(java.util.Objects::nonNull).toList();
            if (validRanges.isEmpty()) {
                return List.of();
            }
            criteria = criteria.orOperator(validRanges.stream()
                    .map(range -> new Criteria().and("created_at").gte(range.startInclusive())
                            .lt(range.endExclusive()))
                    .toArray(Criteria[]::new));
        }

        List<Document> docs = mongoTemplate.find(new Query(criteria), Document.class, "transactions");
        List<TransactionRow> rows = new ArrayList<>(docs.size());
        for (Document doc : docs) {
            LocalDateTime createdAt = toLocalDateTime(doc.get("created_at"));
            if (createdAt == null) {
                continue;
            }
            Integer amount = asInt(doc.get("amount"));
            rows.add(new TransactionRow(createdAt, asInt(doc.get("merchant_id")),
                    doc.getString("payment_method"), asString(doc.get("payment_status")),
                    amount != null ? amount : 0));
        }
        return rows;
    }

    private static LocalDateTime toLocalDateTime(Object value) {
        if (value instanceof LocalDateTime ldt) {
            return ldt;
        }
        if (value instanceof java.util.Date date) {
            return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }
        return null;
    }

    private static Integer asInt(Object value) {
        if (value instanceof Number n) {
            return n.intValue();
        }
        if (value instanceof String s) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
