package com.mercadolibre.socialmeli.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import  lombok.ToString;
import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonPropertyOrder({"post_id", "user_id", "date", "product", "category", "price"})
public class Post {

    // Constructor manual sin incluir los campos opcionales
    public Post(Integer userId, LocalDate date, Product product, Integer category, Double price) {
        this.userId = userId;
        this.date = date;
        this.product = product;
        this.category = category;
        this.price = price;
    }
    @JsonProperty("post_id")
    private Integer postId;
    @JsonProperty("user_id")
    private Integer userId;
    @JsonProperty("date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    @JsonProperty("product")
    private Product product;
    @JsonProperty("category")
    private Integer category;
    @JsonProperty("price")
    private Double price;
    @JsonProperty("has_promo")
    @JsonIgnore
    private Boolean hasPromo = false; // Valor predeterminado de false
    @JsonProperty("discount")
    @JsonIgnore
    private Double discount = 0.0; // Valor predeterminado de 0.0
}
