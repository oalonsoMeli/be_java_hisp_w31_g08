package com.mercadolibre.socialmeli.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FollowersDto {

    private Integer user_id;
    private String user_name;
    List<UserDto> followers;
}
