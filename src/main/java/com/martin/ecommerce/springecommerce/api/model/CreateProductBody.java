package com.martin.ecommerce.springecommerce.api.model;

import lombok.Data;

@Data
public class CreateProductBody {
    private String name;
    private String brand;
    private String shortDescription;
    private String longDescription;
    private Integer price;
    private String imageUrl;
    private String category;
    private Integer quantity;
}
