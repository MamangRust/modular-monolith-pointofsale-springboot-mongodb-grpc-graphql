package com.sanedge.pointofsale.user.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.sanedge.pointofsale.common.models.BaseModel;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "users")
public class User extends BaseModel {
    @Id
    @Field("user_id")
    private String userId;

    @NotBlank
    @Size(max = 20)
    @Field("username")
    private String username;

    @NotBlank
    @Size(max = 100)
    @Field("firstname")
    private String firstname;

    @NotBlank
    @Size(max = 100)
    @Field("lastname")
    private String lastname;

    @Email
    @NotBlank
    @Size(max = 100)
    @Indexed(unique = true)
    @Field("email")
    private String email;

    @NotBlank
    @Size(max = 100)
    @Field("password")
    private String password;
}
