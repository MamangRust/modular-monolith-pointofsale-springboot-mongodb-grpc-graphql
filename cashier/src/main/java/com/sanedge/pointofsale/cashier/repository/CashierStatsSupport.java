package com.sanedge.pointofsale.cashier.repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Menggabungkan orders → cashiers secara in-memory untuk query statistik. Join
 * dilakukan di sisi aplikasi karena tipe id antar koleksi berbeda (PK
 * cashier_id String vs FK cashier_id Integer di orders) sehingga $lookup tidak
 * bisa dipakai langsung.
 */
@Component
@RequiredArgsConstructor
public class CashierStatsSupport {

    private final MongoTemplate mongoTemplate;

    public record CashierRow(int cashierId, String cashierName, LocalDateTime orderCreatedAt, int orderId,
            long totalPrice) {
    }

    public List<CashierRow> findJoinedRows(LocalDateTime startInclusive, LocalDateTime endExclusive,
            Integer merchantId, Integer cashierId) {

        Criteria criteria = Criteria.where("deleted_at").isNull();
        if (startInclusive != null && endExclusive != null) {
            criteria = criteria.and("created_at").gte(startInclusive).lt(endExclusive);
        }
        if (merchantId != null) {
            criteria = criteria.and("merchant_id").is(merchantId);
        }

        List<Document> orders = mongoTemplate.find(new Query(criteria), Document.class, "orders");
        if (orders.isEmpty()) {
            return List.of();
        }

        Map<Integer, Long> orderTotals = new HashMap<>();
        Set<Integer> cashierIds = new HashSet<>();
        for (Document order : orders) {
            Integer orderId = asInt(order.get("_id"));
            Integer cashier = asInt(order.get("cashier_id"));
            if (orderId == null) {
                continue;
            }
            Long totalPrice = asLong(order.get("total_price"));
            orderTotals.put(orderId, totalPrice != null ? totalPrice : 0L);
            if (cashier != null) {
                cashierIds.add(cashier);
            }
        }
        if (cashierIds.isEmpty()) {
            return List.of();
        }

        Set<Integer> wantedCashierIds = cashierIds;
        if (cashierId != null) {
            wantedCashierIds = new HashSet<>();
            if (cashierIds.contains(cashierId)) {
                wantedCashierIds.add(cashierId);
            }
        }
        if (wantedCashierIds.isEmpty()) {
            return List.of();
        }

        List<Document> cashiers = mongoTemplate
                .find(new Query(Criteria.where("_id").in(toStrings(wantedCashierIds))
                        .and("deleted_at").isNull()),
                        Document.class, "cashiers");

        Map<Integer, String> cashierNames = new HashMap<>();
        for (Document cashier : cashiers) {
            Integer cid = asInt(cashier.get("_id"));
            if (cid != null) {
                cashierNames.put(cid, cashier.getString("name"));
            }
        }

        List<CashierRow> rows = new ArrayList<>();
        for (Document order : orders) {
            Integer orderId = asInt(order.get("_id"));
            Integer cashier = asInt(order.get("cashier_id"));
            if (orderId == null || cashier == null || !wantedCashierIds.contains(cashier)) {
                continue;
            }
            String cashierName = cashierNames.get(cashier);
            if (cashierName == null) {
                continue;
            }
            Long totalPrice = orderTotals.get(orderId);
            rows.add(new CashierRow(cashier, cashierName, toLocalDateTime(order.get("created_at")), orderId,
                    totalPrice != null ? totalPrice : 0L));
        }
        return rows;
    }

    private static List<String> toStrings(Set<Integer> values) {
        List<String> result = new ArrayList<>(values.size());
        for (Integer value : values) {
            result.add(String.valueOf(value));
        }
        return result;
    }

    private static Integer asInt(Object value) {
        Long result = asLong(value);
        return result == null ? null : result.intValue();
    }

    private static Long asLong(Object value) {
        if (value instanceof Number n) {
            return n.longValue();
        }
        if (value instanceof String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
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
}
