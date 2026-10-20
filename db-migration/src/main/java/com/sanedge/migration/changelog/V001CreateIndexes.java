package com.sanedge.migration.changelog;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

/**
 * Creates the indexes for the POS collections.
 *
 * <p>Field names mirror the {@code @Field} mappings on the domain models (they
 * are the snake_case names actually persisted), not the Java property names.
 * Unique indexes replicate the {@code @Indexed(unique = true)} annotations so
 * the guarantees hold even when a service boots with
 * {@code spring.data.mongodb.auto-index-creation=false}.
 *
 * <p>Collections covered: users, roles, user_roles, refresh_tokens, merchants,
 * cashiers, categories, products, orders, order_items, transactions.
 */
@ChangeUnit(id = "001-create-indexes", order = "001", author = "pointofsale")
public class V001CreateIndexes {

    private static final String USERS = "users";
    private static final String ROLES = "roles";
    private static final String USER_ROLES = "user_roles";
    private static final String REFRESH_TOKENS = "refresh_tokens";
    private static final String MERCHANTS = "merchants";
    private static final String CASHIERS = "cashiers";
    private static final String CATEGORIES = "categories";
    private static final String PRODUCTS = "products";
    private static final String ORDERS = "orders";
    private static final String ORDER_ITEMS = "order_items";
    private static final String TRANSACTIONS = "transactions";

    @Execution
    public void migrate(MongoTemplate template) {
        // users: unique email; username used for lookups.
        IndexOperations users = template.indexOps(USERS);
        users.ensureIndex(new Index().on("email", Sort.Direction.ASC).unique().named("uq_users_email"));
        users.ensureIndex(new Index().on("username", Sort.Direction.ASC).named("ix_users_username"));

        // roles: unique role_name.
        template.indexOps(ROLES)
                .ensureIndex(new Index().on("role_name", Sort.Direction.ASC).unique().named("uq_roles_role_name"));

        // user_roles: fast lookup by user / role; a user may hold a role only once.
        IndexOperations userRoles = template.indexOps(USER_ROLES);
        userRoles.ensureIndex(new Index().on("user_id", Sort.Direction.ASC).named("ix_user_roles_user_id"));
        userRoles.ensureIndex(new Index().on("role_id", Sort.Direction.ASC).named("ix_user_roles_role_id"));
        userRoles.ensureIndex(new Index().on("user_id", Sort.Direction.ASC)
                .on("role_id", Sort.Direction.ASC).unique().named("uq_user_roles_user_role"));

        // refresh_tokens: unique token; per-user lookup and TTL cleanup on expiry.
        IndexOperations refreshTokens = template.indexOps(REFRESH_TOKENS);
        refreshTokens.ensureIndex(new Index().on("token", Sort.Direction.ASC).unique().named("uq_refresh_tokens_token"));
        refreshTokens.ensureIndex(new Index().on("user_id", Sort.Direction.ASC).named("ix_refresh_tokens_user_id"));
        refreshTokens.ensureIndex(new Index().on("expiration", Sort.Direction.ASC)
                .expire(0).named("ttl_refresh_tokens_expiration"));

        // merchants: owner lookup and status filter.
        IndexOperations merchants = template.indexOps(MERCHANTS);
        merchants.ensureIndex(new Index().on("user_id", Sort.Direction.ASC).named("ix_merchants_user_id"));
        merchants.ensureIndex(new Index().on("status", Sort.Direction.ASC).named("ix_merchants_status"));

        // cashiers: per-merchant and per-user lookup.
        IndexOperations cashiers = template.indexOps(CASHIERS);
        cashiers.ensureIndex(new Index().on("merchant_id", Sort.Direction.ASC).named("ix_cashiers_merchant_id"));
        cashiers.ensureIndex(new Index().on("user_id", Sort.Direction.ASC).named("ix_cashiers_user_id"));

        // categories: unique slug.
        template.indexOps(CATEGORIES)
                .ensureIndex(new Index().on("slug_category", Sort.Direction.ASC).unique().named("uq_categories_slug"));

        // products: unique slug + barcode; merchant/category traversal.
        IndexOperations products = template.indexOps(PRODUCTS);
        products.ensureIndex(new Index().on("slug_product", Sort.Direction.ASC).unique().named("uq_products_slug"));
        products.ensureIndex(new Index().on("barcode", Sort.Direction.ASC).unique().named("uq_products_barcode"));
        products.ensureIndex(new Index().on("merchant_id", Sort.Direction.ASC).named("ix_products_merchant_id"));
        products.ensureIndex(new Index().on("category_id", Sort.Direction.ASC).named("ix_products_category_id"));

        // orders: per-merchant and per-cashier listing.
        IndexOperations orders = template.indexOps(ORDERS);
        orders.ensureIndex(new Index().on("merchant_id", Sort.Direction.ASC).named("ix_orders_merchant_id"));
        orders.ensureIndex(new Index().on("cashier_id", Sort.Direction.ASC).named("ix_orders_cashier_id"));

        // order_items: items of an order; product usage.
        IndexOperations orderItems = template.indexOps(ORDER_ITEMS);
        orderItems.ensureIndex(new Index().on("order_id", Sort.Direction.ASC).named("ix_order_items_order_id"));
        orderItems.ensureIndex(new Index().on("product_id", Sort.Direction.ASC).named("ix_order_items_product_id"));

        // transactions: per-order, per-merchant and status reporting.
        IndexOperations transactions = template.indexOps(TRANSACTIONS);
        transactions.ensureIndex(new Index().on("order_id", Sort.Direction.ASC).named("ix_transactions_order_id"));
        transactions.ensureIndex(new Index().on("merchant_id", Sort.Direction.ASC).named("ix_transactions_merchant_id"));
        transactions.ensureIndex(new Index().on("payment_status", Sort.Direction.ASC)
                .named("ix_transactions_payment_status"));
    }

    /**
     * Drops the indexes created by {@link #migrate(MongoTemplate)}. Collections
     * and documents are intentionally left untouched — this change unit only
     * owns indexes.
     */
    @RollbackExecution
    public void rollback(MongoTemplate template) {
        dropIndexes(template, USERS, List.of("uq_users_email", "ix_users_username"));
        dropIndexes(template, ROLES, List.of("uq_roles_role_name"));
        dropIndexes(template, USER_ROLES,
                List.of("ix_user_roles_user_id", "ix_user_roles_role_id", "uq_user_roles_user_role"));
        dropIndexes(template, REFRESH_TOKENS,
                List.of("uq_refresh_tokens_token", "ix_refresh_tokens_user_id", "ttl_refresh_tokens_expiration"));
        dropIndexes(template, MERCHANTS, List.of("ix_merchants_user_id", "ix_merchants_status"));
        dropIndexes(template, CASHIERS, List.of("ix_cashiers_merchant_id", "ix_cashiers_user_id"));
        dropIndexes(template, CATEGORIES, List.of("uq_categories_slug"));
        dropIndexes(template, PRODUCTS,
                List.of("uq_products_slug", "uq_products_barcode", "ix_products_merchant_id", "ix_products_category_id"));
        dropIndexes(template, ORDERS, List.of("ix_orders_merchant_id", "ix_orders_cashier_id"));
        dropIndexes(template, ORDER_ITEMS, List.of("ix_order_items_order_id", "ix_order_items_product_id"));
        dropIndexes(template, TRANSACTIONS,
                List.of("ix_transactions_order_id", "ix_transactions_merchant_id", "ix_transactions_payment_status"));
    }

    private void dropIndexes(MongoTemplate template, String collection, List<String> names) {
        IndexOperations ops = template.indexOps(collection);
        for (IndexInfo index : ops.getIndexInfo()) {
            if (names.contains(index.getName())) {
                ops.dropIndex(index.getName());
            }
        }
    }
}
