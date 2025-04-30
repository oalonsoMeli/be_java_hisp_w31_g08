package com.mercadolibre.socialmeli.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {

    private Integer user_id;
    private String user_name;

}
