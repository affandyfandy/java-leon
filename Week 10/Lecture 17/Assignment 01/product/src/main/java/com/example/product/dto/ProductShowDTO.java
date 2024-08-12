package com.example.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductShowDTO {
    private UUID id;
    private String name;
    private Double price;
    private Integer quantity;
}