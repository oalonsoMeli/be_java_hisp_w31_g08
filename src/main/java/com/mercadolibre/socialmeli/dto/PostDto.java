package com.mercadolibre.socialmeli.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "El  id no puede estar vacío.")
    @Min(value = 0, message = "El id debe ser mayor a cero")
    private Integer userId;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotNull(message = "La fecha no puede estar vacía")
    private LocalDate date;

    @Valid
    private ProductDto product;

    @NotNull(message = "El campo no puede estar vacío.")
    private Integer category;

    @NotNull(message = "El campo no puede estar vacío.")
    @Min(value = 0, message = "El producto no puede tener un precio negativo")
    @Max(value = 10000000, message = "El precio máximo por producto es de 10.000.000")
    private Double price;
}
