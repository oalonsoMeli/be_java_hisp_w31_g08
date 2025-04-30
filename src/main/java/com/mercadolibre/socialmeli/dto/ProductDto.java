package com.mercadolibre.socialmeli.dto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductDto {

    @NotNull(message = "La id no puede estar vacía")
    @Min(value = 0, message = "El id debe ser mayor a cero")
    private Integer product_id;

    @NotBlank(message = "El campo no puede estar vacío.")
    @Size(min = 0, max = 40, message = "La longitud no puede superar los 40 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9ñáéíóúÁÉÍÓÚ ]*$", message = "El campo no puede poseer caracteres especiales")
    private String product_name;

    @NotBlank(message = "El campo no puede estar vacío.")
    @Size(min = 0, max = 15, message = "La longitud no puede superar los 15 caracteres.")
    @Pattern(regexp = "^[a-zA-Z0-9ñáéíóúÁÉÍÓÚ ]*$", message = "El campo no puede poseer caracteres especiales")
    private String type;

    @NotBlank(message = "El campo no puede estar vacío.")
    @Size(min = 0, max = 25, message = "La longitud no puede superar los 25 caracteres.")
    @Pattern(regexp = "^[a-zA-Z0-9ñáéíóúÁÉÍÓÚ ]*$", message = "El campo no puede poseer caracteres especiales")
    private String brand;

    @NotBlank(message = "El campo no puede estar vacío.")
    @Size(min = 0, max = 15, message = "La longitud no puede superar los 15 caracteres.")
    @Pattern(regexp = "^[a-zA-Z0-9ñáéíóúÁÉÍÓÚ ]*$", message = "El campo no puede poseer caracteres especiales")
    private String color;

    @Size(min = 0, max = 80, message = "La longitud no puede superar los 80 caracteres.")
    @Pattern(regexp = "^[a-zA-Z0-9ñáéíóúÁÉÍÓÚ ]*$", message = "El campo no puede poseer caracteres especiales")
    private String notes;
}
