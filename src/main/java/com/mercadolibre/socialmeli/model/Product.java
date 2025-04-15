package com.mercadolibre.socialmeli.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {

    private Integer product_id;
    private String product_name;
    private String type;
    private String brand;
    private String color;
    private String notes;
}
