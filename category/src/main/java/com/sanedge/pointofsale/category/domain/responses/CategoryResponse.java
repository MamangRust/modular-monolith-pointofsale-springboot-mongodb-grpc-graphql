package com.sanedge.pointofsale.category.domain.responses;

import com.sanedge.pointofsale.category.models.Category;
import com.sanedge.pointofsale.common.utils.Ids;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private String slugCategory;
    private String imageCategory;
    private String createdAt;
    private String updatedAt;

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(Ids.toLong(category.getCategoryId()))
                .name(category.getName())
                .description(category.getDescription())
                .slugCategory(category.getSlugCategory())
                .createdAt(category.getCreatedAt() != null ? category.getCreatedAt().toString() : null)
                .updatedAt(category.getUpdatedAt() != null ? category.getUpdatedAt().toString() : null)
                .build();
    }
}