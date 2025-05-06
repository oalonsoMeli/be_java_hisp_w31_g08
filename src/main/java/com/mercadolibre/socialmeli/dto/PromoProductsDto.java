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

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"user_id", "user_name","promoPostDtoList"})
public class PromoProductsDto {
    @JsonProperty("user_id")
    @NotNull(message = "El  id no puede estar vacío.")
    @Min(value = 0, message = "El id debe ser mayor a cero")
    private Integer userId;
    @JsonProperty("user_name")
    @Size(min = 0, max = 15, message = "La longitud no puede superar los 15 caracteres")
    private String username;
    private List<PromoPostDto> promoPostDtoList;
}
