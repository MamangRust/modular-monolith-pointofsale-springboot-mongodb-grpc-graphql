package com.sanedge.pointofsale.category.repository;

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
 * Menggabungkan orders → order_items → products → categories secara in-memory
 * untuk query statistik. Pergabungan dilakukan di sisi aplikasi karena tipe id
 * antar koleksi berbeda (PK String vs FK Integer) sehingga $lookup tidak bisa
 * dipakai langsung.
 */
@Component
@RequiredArgsConstructor
public class CategoryStatsSupport {

    private final MongoTemplate mongoTemplate;

    public record JoinedRow(LocalDateTime orderCreatedAt, int orderId, int productId, int quantity,
            long orderTotalPrice, Integer categoryId, String categoryName) {
    }

    public List<JoinedRow> findJoinedRows(LocalDateTime startInclusive, LocalDateTime endExclusive,
            Integer merchantId, Integer categoryId) {

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

        Map<Integer, Document> ordersById = new HashMap<>();
        Set<Integer> orderIds = new HashSet<>();
        for (Document order : orders) {
            Integer orderId = asInt(order.get("_id"));
            if (orderId == null) {
                continue;
            }
            ordersById.put(orderId, order);
            orderIds.add(orderId);
        }
        if (orderIds.isEmpty()) {
            return List.of();
        }

        List<Document> items = mongoTemplate
                .find(new Query(Criteria.where("order_id").in(orderIds).and("deleted_at").isNull()),
                        Document.class, "order_items");
        if (items.isEmpty()) {
            return List.of();
        }

        Set<Integer> productIds = new HashSet<>();
        for (Document item : items) {
            Integer productId = asInt(item.get("product_id"));
            if (productId != null) {
                productIds.add(productId);
            }
        }
        if (productIds.isEmpty()) {
            return List.of();
        }

        List<Document> products = mongoTemplate
                .find(new Query(Criteria.where("_id").in(toStrings(productIds))
                        .and("deleted_at").isNull()),
                        Document.class, "products");

        Map<Integer, Integer> productToCategory = new HashMap<>();
        Set<Integer> categoryIds = new HashSet<>();
        for (Document product : products) {
            Integer productId = asInt(product.get("_id"));
            Integer catId = asInt(product.get("category_id"));
            if (productId == null || catId == null) {
                continue;
            }
            productToCategory.put(productId, catId);
            categoryIds.add(catId);
        }

        Set<Integer> wantedCategoryIds = categoryIds;
        if (categoryId != null) {
            wantedCategoryIds = new HashSet<>();
            if (categoryIds.contains(categoryId)) {
                wantedCategoryIds.add(categoryId);
            }
        }
        if (wantedCategoryIds.isEmpty()) {
            return List.of();
        }

        List<Document> categories = mongoTemplate
                .find(new Query(Criteria.where("_id").in(toStrings(wantedCategoryIds))
                        .and("deleted_at").isNull()),
                        Document.class, "categories");

        Map<Integer, String> categoryNames = new HashMap<>();
        for (Document category : categories) {
            Integer cid = asInt(category.get("_id"));
            if (cid != null) {
                categoryNames.put(cid, category.getString("name"));
            }
        }

        List<JoinedRow> rows = new ArrayList<>();
        for (Document item : items) {
            Integer orderId = asInt(item.get("order_id"));
            Integer productId = asInt(item.get("product_id"));
            if (orderId == null || productId == null) {
                continue;
            }
            Integer catId = productToCategory.get(productId);
            if (catId == null || !wantedCategoryIds.contains(catId)) {
                continue;
            }
            String categoryName = categoryNames.get(catId);
            Document order = ordersById.get(orderId);
            if (categoryName == null || order == null) {
                continue;
            }
            Integer quantity = asInt(item.get("quantity"));
            Long totalPrice = asLong(order.get("total_price"));
            rows.add(new JoinedRow(toLocalDateTime(order.get("created_at")), orderId, productId,
                    quantity != null ? quantity : 0, totalPrice != null ? totalPrice : 0L, catId, categoryName));
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
