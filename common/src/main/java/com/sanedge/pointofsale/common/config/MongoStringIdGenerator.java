package com.sanedge.pointofsale.common.config;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Mengisi field @Id bertipe String dengan id numerik (string) sebelum dokumen
 * dikonversi, sehingga id tetap kompatibel dengan kontrak gRPC (int32) —
 * bukan ObjectId hex bawaan MongoDB.
 *
 * Sequence per-JVM cukup unik karena tiap service hanya meng-generate id
 * untuk koleksi miliknya sendiri.
 */
@Slf4j
@Component
public class MongoStringIdGenerator extends AbstractMongoEventListener<Object> {

    private static final AtomicLong SEQUENCE =
            new AtomicLong(System.currentTimeMillis() % 2_000_000_000L);

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object source = event.getSource();
        if (source == null) {
            return;
        }

        for (Class<?> type = source.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Id.class) || field.getType() != String.class) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    if (field.get(source) == null) {
                        field.set(source, String.valueOf(nextId()));
                    }
                    return;
                } catch (IllegalAccessException e) {
                    log.warn("Failed to generate id for {}: {}", source.getClass().getSimpleName(), e.getMessage());
                    return;
                }
            }
        }
    }

    private static long nextId() {
        long floor = System.currentTimeMillis() % 2_000_000_000L;
        return SEQUENCE.updateAndGet(current -> Math.max(current + 1, floor));
    }
}
