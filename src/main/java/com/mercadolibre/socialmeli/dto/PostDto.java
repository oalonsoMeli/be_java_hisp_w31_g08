package com.mercadolibre.socialmeli.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"post_id", "user_id", "date", "product", "category", "price"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostDto {
    @JsonProperty("post_id")
    private Integer postId;
    @JsonProperty("user_id")
    private Integer userId;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    private ProductDto product;
    private Integer category;
    private Double price;
}
