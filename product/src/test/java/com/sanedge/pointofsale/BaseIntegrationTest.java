package com.sanedge.pointofsale;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import com.sanedge.pointofsale.product.ProductApplication;

/**
 * Module-local integration test base for the product service. The product
 * module owns no merchant entity, so {@code adminMerchant} is a lightweight
 * fixture exposing the merchant id used as the {@code merchant_id} document
 * value.
 */
@SpringBootTest(classes = ProductApplication.class)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    static final MongoDBContainer MONGO = new MongoDBContainer(DockerImageName.parse("mongo:7.0"));

    static {
        MONGO.start();
    }

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO::getReplicaSetUrl);
        registry.add("spring.data.mongodb.auto-index-creation", () -> "true");
    }

    protected final AdminMerchant adminMerchant = new AdminMerchant(1);

    public static class AdminMerchant {
        private final Integer merchantId;

        public AdminMerchant(Integer merchantId) {
            this.merchantId = merchantId;
        }

        public Integer getMerchantId() {
            return merchantId;
        }
    }

    @org.springframework.beans.factory.annotation.Autowired
    protected org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    /**
     * MongoDB has no test-rollback: clear all collections before each test so
     * fixtures seeded by @BeforeEach (or the test body) start from a clean slate,
     * mirroring the previous JPA @Transactional rollback semantics.
     */
    @org.junit.jupiter.api.BeforeEach
    void clearDatabaseBeforeEachTest() {
        for (String name : mongoTemplate.getDb().listCollectionNames()) {
            mongoTemplate.getDb().getCollection(name).deleteMany(new org.bson.Document());
        }
    }
}
