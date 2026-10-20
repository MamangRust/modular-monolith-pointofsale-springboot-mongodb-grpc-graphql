package com.sanedge.pointofsale.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Konfigurasi MongoDB bersama untuk semua module service.
 * Mengaktifkan auditing untuk @CreatedDate/@LastModifiedDate pada BaseModel.
 *
 * Conditional pada nama class driver MongoDB (dibawa spring-boot-starter-data-mongodb
 * di tiap service) supaya modul tanpa database (mis. gateway) yang juga meng-scan
 * package common tidak gagal saat startup.
 */
@Configuration
@ConditionalOnClass(name = "com.mongodb.client.MongoClient")
@EnableMongoAuditing
public class MongoConfig {
}
