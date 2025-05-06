package com.mercadolibre.socialmeli.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FollowerCountDto {
    @NotNull(message = "El  id no puede estar vacío.")
    @Min(value = 0, message = "El id debe ser mayor a cero")
    private Integer user_id;
    @Size(min = 0, max = 15, message = "La longitud no puede superar los 15 caracteres")
    private String user_name;
    private Integer followers_count;

}
