package com.mercadolibre.socialmeli.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostsDto {
    private Integer user_id;
    private List<PostDto> posts;
}
