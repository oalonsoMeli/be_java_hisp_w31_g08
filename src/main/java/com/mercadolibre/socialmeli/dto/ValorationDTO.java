package com.mercadolibre.socialmeli.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ValorationDTO {
    @NotNull(message = "El  id no puede estar vacío.")
    @Min(value = 0, message = "El id debe ser mayor a cero")
    private Integer user_id;
    private Integer post_id;
    private Integer valoration;
}
