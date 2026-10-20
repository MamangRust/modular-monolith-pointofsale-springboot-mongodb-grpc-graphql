package com.sanedge.pointofsale.category.repository.statsbyid;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.sanedge.pointofsale.BaseIntegrationTest;
import com.sanedge.pointofsale.category.models.Category;
import com.sanedge.pointofsale.category.models.CategoryMonthPrice;
import com.sanedge.pointofsale.category.models.CategoryYearPrice;
import com.sanedge.pointofsale.category.repository.CategoryCommandRepository;

public class CategoryPriceByIdRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CategoryPriceByIdRepository categoryPriceByIdRepository;

    @Autowired
    private CategoryCommandRepository categoryCommandRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void shouldFindMonthlyCategoryStatsById() {
        // Setup data (category via repository, cross-module docs via MongoTemplate)
        Category category = new Category();
        category.setName("Electronics Stats Monthly");
        category.setSlugCategory("electronics-monthly");
        category = categoryCommandRepository.save(category);
        String categoryId = category.getCategoryId();

        Long merchantId = 9001L;
        Long productId = 8001L;
        insertProduct(productId, merchantId, categoryId, "Smartphone Stats", 1000, 10);

        // Order in 2024-01-15
        Long order1 = 7001L;
        insertOrder(order1, merchantId, 2000L, LocalDateTime.of(2024, 1, 15, 10, 0));
        insertOrderItem(6001L, order1, productId, 2, 1000, LocalDateTime.of(2024, 1, 15, 10, 0));

        // Order in 2024-02-20
        Long order2 = 7002L;
        insertOrder(order2, merchantId, 3000L, LocalDateTime.of(2024, 2, 20, 15, 0));
        insertOrderItem(6002L, order2, productId, 3, 1000, LocalDateTime.of(2024, 2, 20, 15, 0));

        // Run query
        List<CategoryMonthPrice> results = categoryPriceByIdRepository.findMonthlyCategoryPriceById(
                Long.valueOf(categoryId), 2024);

        // Verification
        assertThat(results).hasSize(2);

        CategoryMonthPrice janStats = results.stream()
                .filter(s -> s.getMonth().equalsIgnoreCase("Jan"))
                .findFirst().orElseThrow();

        assertThat(janStats.getCategoryName()).isEqualTo("Electronics Stats Monthly");
        assertThat(janStats.getOrderCount()).isEqualTo(1);
        assertThat(janStats.getItemsSold()).isEqualTo(2);
        assertThat(janStats.getTotalRevenue()).isEqualTo(2000L);

        CategoryMonthPrice febStats = results.stream()
                .filter(s -> s.getMonth().equalsIgnoreCase("Feb"))
                .findFirst().orElseThrow();

        assertThat(febStats.getOrderCount()).isEqualTo(1);
        assertThat(febStats.getItemsSold()).isEqualTo(3);
        assertThat(febStats.getTotalRevenue()).isEqualTo(3000L);
    }

    @Test
    void shouldFindYearlyCategoryStatsById() {
        // Setup data
        Category category = new Category();
        category.setName("Books Stats Yearly");
        category.setSlugCategory("books-yearly");
        category = categoryCommandRepository.save(category);
        String categoryId = category.getCategoryId();

        Long merchantId = 9101L;
        Long productId = 8101L;
        insertProduct(productId, merchantId, categoryId, "Java Guide Stats", 500, 50);

        // Order in 2023
        Long order2023 = 7101L;
        insertOrder(order2023, merchantId, 1000L, LocalDateTime.of(2023, 6, 1, 10, 0));
        insertOrderItem(6101L, order2023, productId, 2, 500, LocalDateTime.of(2023, 6, 1, 10, 0));

        // Order in 2024
        Long order2024 = 7102L;
        insertOrder(order2024, merchantId, 1500L, LocalDateTime.of(2024, 8, 15, 12, 0));
        insertOrderItem(6102L, order2024, productId, 3, 500, LocalDateTime.of(2024, 8, 15, 12, 0));

        // Run query for 2024 (should include 2020-2024)
        List<CategoryYearPrice> results = categoryPriceByIdRepository.findYearlyCategoryPriceById(
                Long.valueOf(categoryId), 2024);

        // Verification
        assertThat(results).hasSize(2);

        CategoryYearPrice stats2023 = results.stream()
                .filter(s -> s.getYear().equals("2023"))
                .findFirst().orElseThrow();
        assertThat(stats2023.getOrderCount()).isEqualTo(1);
        assertThat(stats2023.getTotalRevenue()).isEqualTo(1000L);

        CategoryYearPrice stats2024 = results.stream()
                .filter(s -> s.getYear().equals("2024"))
                .findFirst().orElseThrow();
        assertThat(stats2024.getOrderCount()).isEqualTo(1);
        assertThat(stats2024.getTotalRevenue()).isEqualTo(1500L);
    }

    private void insertProduct(Long productId, Long merchantId, String categoryId, String name, int price,
            int countInStock) {
        mongoTemplate.insert(new org.bson.Document()
                .append("_id", String.valueOf(productId))
                .append("merchant_id", merchantId.intValue())
                .append("category_id", Integer.valueOf(categoryId))
                .append("name", name)
                .append("price", price)
                .append("count_in_stock", countInStock)
                .append("created_at", LocalDateTime.now())
                .append("updated_at", LocalDateTime.now())
                .append("deleted_at", null), "products");
    }

    private void insertOrder(Long orderId, Long merchantId, Long totalPrice, LocalDateTime createdAt) {
        mongoTemplate.insert(new org.bson.Document()
                .append("_id", String.valueOf(orderId))
                .append("cashier_id", 1)
                .append("merchant_id", merchantId.intValue())
                .append("total_price", totalPrice)
                .append("created_at", createdAt)
                .append("updated_at", createdAt)
                .append("deleted_at", null), "orders");
    }

    private void insertOrderItem(Long orderItemId, Long orderId, Long productId, int quantity, int price,
            LocalDateTime createdAt) {
        mongoTemplate.insert(new org.bson.Document()
                .append("_id", String.valueOf(orderItemId))
                .append("order_id", orderId.intValue())
                .append("product_id", productId.intValue())
                .append("quantity", quantity)
                .append("price", price)
                .append("created_at", createdAt)
                .append("updated_at", createdAt)
                .append("deleted_at", null), "order_items");
    }
}
