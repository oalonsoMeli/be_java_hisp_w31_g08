package com.mercadolibre.socialmeli.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostDto {

    @JsonProperty("user_id")
    private Integer userId;
    private LocalDate date;
    private ProductDto product;
    private Integer category;
    private Double price;
}
