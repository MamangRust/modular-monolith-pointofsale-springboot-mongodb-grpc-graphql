package com.sanedge.pointofsale;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import com.sanedge.pointofsale.merchant.MerchantApplication;

/**
 * Module-local integration test base for the merchant service. The merchant
 * module owns no user entity, so {@code adminUser} is a lightweight fixture
 * exposing the admin user id used as the {@code user_id} document value.
 */
@SpringBootTest(classes = MerchantApplication.class)
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

    protected final AdminUser adminUser = new AdminUser(1);

    public static class AdminUser {
        private final Integer userId;

        public AdminUser(Integer userId) {
            this.userId = userId;
        }

        public Integer getUserId() {
            return userId;
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
