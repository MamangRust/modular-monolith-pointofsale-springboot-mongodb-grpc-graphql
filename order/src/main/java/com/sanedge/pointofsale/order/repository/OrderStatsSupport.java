package com.sanedge.pointofsale.order.repository;

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
 * Menggabungkan orders → order_items secara in-memory untuk query statistik.
 * Pergabungan dilakukan di sisi aplikasi karena tipe id antar koleksi berbeda
 * (PK order_id String di orders vs FK order_id Integer di order_items) sehingga
 * $lookup tidak bisa dipakai langsung.
 */
@Component
@RequiredArgsConstructor
public class OrderStatsSupport {

    private final MongoTemplate mongoTemplate;

    public record OrderRow(int orderId, Integer merchantId, Integer cashierId, long totalPrice,
            LocalDateTime createdAt) {
    }

    public record JoinedRow(OrderRow order, int productId, int quantity) {
    }

    public List<OrderRow> findOrders(LocalDateTime startInclusive, LocalDateTime endExclusive,
            Integer merchantId) {

        Criteria criteria = Criteria.where("deleted_at").isNull();
        if (startInclusive != null && endExclusive != null) {
            criteria = criteria.and("created_at").gte(startInclusive).lt(endExclusive);
        }
        if (merchantId != null) {
            criteria = criteria.and("merchant_id").is(merchantId);
        }

        List<Document> orders = mongoTemplate.find(new Query(criteria), Document.class, "orders");

        List<OrderRow> rows = new ArrayList<>(orders.size());
        for (Document order : orders) {
            Integer orderId = asInt(order.get("_id"));
            if (orderId == null) {
                continue;
            }
            Long totalPrice = asLong(order.get("total_price"));
            rows.add(new OrderRow(orderId,
                    asInt(order.get("merchant_id")),
                    asInt(order.get("cashier_id")),
                    totalPrice != null ? totalPrice : 0L,
                    toLocalDateTime(order.get("created_at"))));
        }
        return rows;
    }

    public List<JoinedRow> findJoinedRows(LocalDateTime startInclusive, LocalDateTime endExclusive,
            Integer merchantId) {

        List<OrderRow> orders = findOrders(startInclusive, endExclusive, merchantId);
        if (orders.isEmpty()) {
            return List.of();
        }

        Map<Integer, OrderRow> ordersById = new HashMap<>();
        Set<Integer> orderIds = new HashSet<>();
        for (OrderRow order : orders) {
            ordersById.put(order.orderId(), order);
            orderIds.add(order.orderId());
        }

        List<Document> items = mongoTemplate
                .find(new Query(Criteria.where("order_id").in(orderIds).and("deleted_at").isNull()),
                        Document.class, "order_items");

        List<JoinedRow> rows = new ArrayList<>();
        for (Document item : items) {
            Integer orderId = asInt(item.get("order_id"));
            Integer productId = asInt(item.get("product_id"));
            OrderRow order = orderId == null ? null : ordersById.get(orderId);
            if (order == null || productId == null) {
                continue;
            }
            Integer quantity = asInt(item.get("quantity"));
            rows.add(new JoinedRow(order, productId, quantity != null ? quantity : 0));
        }
        return rows;
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
