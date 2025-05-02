package com.mercadolibre.socialmeli.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ValorationDTO {
    private Integer user_id;
    private Integer post_id;
    private Integer valoration;
}
