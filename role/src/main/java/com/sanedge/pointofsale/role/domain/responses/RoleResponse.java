package com.sanedge.pointofsale.role.domain.responses;

import com.sanedge.pointofsale.common.utils.Ids;
import com.sanedge.pointofsale.role.models.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private Integer id;
    private String name;
    private String createdAt;
    private String updatedAt;

    public static RoleResponse from(Role role) {

        Long id = Ids.toLong(role.getRoleId());
        return RoleResponse.builder()
                .id(id == null ? null : id.intValue())
                .name(role.getRoleName())
                .createdAt(role.getCreatedAt() != null ? role.getCreatedAt().toString() : null)
                .updatedAt(role.getUpdatedAt() != null ? role.getUpdatedAt().toString() : null)
                .build();
    }
}