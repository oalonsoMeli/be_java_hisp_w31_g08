package com.mercadolibre.socialmeli.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FollowerCountDto {

    private Integer user_id;
    private String user_name;
    private Integer followers_count;

}
