package com.sanedge.pointofsale.common.outbox;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Widens Mongo repository scanning to the whole {@code com.sanedge.pointofsale}
 * tree.
 *
 * <p>Spring Boot's Mongo auto-configuration only scans repositories under the
 * {@code @SpringBootApplication} class's own package. Modules whose base package
 * is {@code com.sanedge.pointofsale.<service>} therefore miss both
 * {@code com.sanedge.pointofsale.common.outbox} and each other's repository
 * packages, even though {@code scanBasePackages} components them. Declaring the
 * shared root here makes {@link OutboxRepository} available to the shared
 * {@link OutboxPublisher} for every module that loads this config.
 *
 * <p>Gated on {@link MongoRepository} so non-Mongo modules are unaffected.
 */
@Configuration
@ConditionalOnClass(MongoRepository.class)
@EnableMongoRepositories(basePackages = "com.sanedge.pointofsale")
public class OutboxRepositoryConfig {
}
