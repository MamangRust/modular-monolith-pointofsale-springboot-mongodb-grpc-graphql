package com.sanedge.pointofsale.role.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.sanedge.pointofsale.common.models.BaseModel;

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
@Document(collection = "roles")
public class Role extends BaseModel {
    @Id
    @Field("role_id")
    private String roleId;

    @NotBlank
    @Size(max = 50)
    @Indexed(unique = true)
    @Field("role_name")
    private String roleName;
}
