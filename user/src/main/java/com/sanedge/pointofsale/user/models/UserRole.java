package com.sanedge.pointofsale.user.models;

import org.springframework.data.annotation.Id;
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
@Document(collection = "user_roles")
public class UserRole extends BaseModel {

    @Id
    @Field("user_role_id")
    private String userRoleId;

    // Cross-module reference kept as a raw id (no relation mapping to the user module).
    @Field("user_id")
    private String userId;

    // Kept as a raw id so this module has no cross-module dependency.
    @Field("role_id")
    private String roleId;
}
