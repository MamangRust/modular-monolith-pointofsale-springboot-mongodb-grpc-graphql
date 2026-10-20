package com.sanedge.pointofsale.auth.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Password-reset token stored in MongoDB: one per user, short-lived (15 min).
 * A unique index on {@code token} lets lookups resolve it in O(1).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reset_tokens")
public class ResetToken {

    @Id
    @Field("reset_token_id")
    private String resetTokenId;

    @Field("user_id")
    private String userId;

    @Indexed(unique = true)
    @Field("token")
    private String token;

    @Field("expiration")
    private LocalDateTime expiration;
}
