package com.mercadolibre.socialmeli.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@JsonPropertyOrder({"user_id", "user_name", "promo_products_count"})
public class PromoProductsCountDto {
    @JsonProperty("user_id")
    @NotNull(message = "El  id no puede estar vacío.")
    @Min(value = 0, message = "El id debe ser mayor a cero")
    private Integer userId;
    @JsonProperty("user_name")
    @Size(min = 0, max = 15, message = "La longitud no puede superar los 15 caracteres")
    private String userName;
    @JsonProperty("promo_products_count")
    private Integer promoProductsCount;
}
