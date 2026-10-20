package com.sanedge.pointofsale.auth.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.sanedge.pointofsale.common.models.BaseModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "refresh_tokens")
public class RefreshToken extends BaseModel {
    @Id
    @Field("refresh_token_id")
    private String refreshTokenId;

    @Field("user_id")
    private Integer userId;

    @Indexed(unique = true)
    @Field("token")
    private String token;

    @Field("expiration")
    private LocalDateTime expiration;
}
