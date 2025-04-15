package com.mercadolibre.socialmeli.model;

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
public class Post {

    private Integer user_id;
    private LocalDate date;
    private ProductDto product;
    private Integer category;
    private Double price;
}
