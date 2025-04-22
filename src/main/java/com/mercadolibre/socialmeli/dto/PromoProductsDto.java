package com.mercadolibre.socialmeli.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
    private Integer userId;
    @JsonProperty("user_name")
    private String username;
    private List<PromoPostDto> promoPostDtoList;
}
