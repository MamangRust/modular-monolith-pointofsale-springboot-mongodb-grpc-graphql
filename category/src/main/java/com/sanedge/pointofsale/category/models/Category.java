package com.sanedge.pointofsale.category.models;

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
@Document(collection = "categories")
public class Category extends BaseModel {
    @Id
    @Field("category_id")
    private String categoryId;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Indexed(unique = true)
    @Field("slug_category")
    private String slugCategory;
}
