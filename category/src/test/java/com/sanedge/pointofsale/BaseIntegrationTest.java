package com.sanedge.pointofsale;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Module-local test base for the category service.
 *
 * <p>
 * The category statistics queries join the {@code products}/{@code orders}/
 * {@code order_items} collections. Those collections are owned by other
 * modules, so the category test tree seeds them directly through
 * {@code MongoTemplate} while still exercising the real category repositories.
 */
@SpringBootTest
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
