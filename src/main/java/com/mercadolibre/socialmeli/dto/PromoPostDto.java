package com.mercadolibre.socialmeli.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PromoPostDto extends PostDto {

    @JsonProperty("has_promo")
    private Boolean hasPromo;
    @JsonProperty("discount")
    private Double discount;
}