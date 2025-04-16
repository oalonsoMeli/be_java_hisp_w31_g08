package com.mercadolibre.socialmeli.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mercadolibre.socialmeli.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"user_id", "date", "product", "category", "price"})
public class Post {

    @JsonProperty("user_id")
    private Integer userId;
    @JsonProperty("date")
    private LocalDate date;
    @JsonProperty("product")
    private Product product;
    @JsonProperty("category")
    private Integer category;
    @JsonProperty("price")
    private Double price;
}
