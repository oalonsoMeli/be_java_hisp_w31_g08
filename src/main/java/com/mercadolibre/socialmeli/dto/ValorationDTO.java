package com.mercadolibre.socialmeli.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValorationDTO {
    private Integer user_id;
    private Integer post_id;
    private Integer valoration;
}
