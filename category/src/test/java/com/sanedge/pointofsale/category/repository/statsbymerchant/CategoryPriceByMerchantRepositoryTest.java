package com.sanedge.pointofsale.category.repository.statsbymerchant;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.sanedge.pointofsale.BaseIntegrationTest;
import com.sanedge.pointofsale.category.models.Category;
import com.sanedge.pointofsale.category.models.CategoryMonthPrice;
import com.sanedge.pointofsale.category.models.CategoryYearPrice;
import com.sanedge.pointofsale.category.repository.CategoryCommandRepository;

public class CategoryPriceByMerchantRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CategoryPriceByMerchantRepository categoryPriceByMerchantRepository;

    @Autowired
    private CategoryCommandRepository categoryRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Long merchantId;

    @BeforeEach
    void setUp() {
        merchantId = 9001L;
        Long productId = 8001L;
        Long orderId = 7001L;

        Category category = new Category();
        category.setName("Test Category");
        category.setSlugCategory("test-category");
        category.setDescription("Test Category Description");
        category = categoryRepository.save(category);

        LocalDateTime now = LocalDateTime.now();
        mongoTemplate.insert(new org.bson.Document()
                .append("_id", String.valueOf(productId))
                .append("merchant_id", merchantId.intValue())
                .append("category_id", Integer.valueOf(category.getCategoryId()))
                .append("name", "Test Product")
                .append("slug_product", "test-product")
                .append("price", 100)
                .append("count_in_stock", 10)
                .append("created_at", now)
                .append("updated_at", now)
                .append("deleted_at", null), "products");

        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 15, 12, 0);
        mongoTemplate.insert(new org.bson.Document()
                .append("_id", String.valueOf(orderId))
                .append("cashier_id", 1)
                .append("merchant_id", merchantId.intValue())
                .append("total_price", 100L)
                .append("created_at", createdAt)
                .append("updated_at", createdAt)
                .append("deleted_at", null), "orders");

        mongoTemplate.insert(new org.bson.Document()
                .append("_id", "6001")
                .append("order_id", orderId.intValue())
                .append("product_id", productId.intValue())
                .append("quantity", 1)
                .append("price", 100)
                .append("created_at", createdAt)
                .append("updated_at", createdAt)
                .append("deleted_at", null), "order_items");
    }

    @Test
    void findMonthlyCategoryStatsByMerchant_ShouldReturnCorrectStats() {
        List<CategoryMonthPrice> stats = categoryPriceByMerchantRepository.findMonthlyCategoryPriceByMerchant(
                merchantId, 2024);

        assertThat(stats).isNotEmpty();
        assertThat(stats.get(0).getMonth()).isEqualTo("Jan");
        assertThat(stats.get(0).getCategoryName()).isEqualTo("Test Category");
        assertThat(stats.get(0).getTotalRevenue()).isEqualTo(100L);
    }

    @Test
    void findYearlyCategoryStatsByMerchant_ShouldReturnCorrectStats() {
        List<CategoryYearPrice> stats = categoryPriceByMerchantRepository.findYearlyCategoryPriceByMerchant(
                merchantId, 2024);

        assertThat(stats).isNotEmpty();
        assertThat(stats.get(0).getYear()).isEqualTo("2024");
        assertThat(stats.get(0).getCategoryName()).isEqualTo("Test Category");
        assertThat(stats.get(0).getTotalRevenue()).isEqualTo(100L);
    }
}
