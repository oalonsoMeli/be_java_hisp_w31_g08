package com.mercadolibre.socialmeli.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostsDto {
    @JsonProperty("user_id")
    private Integer userId;
    private List<PostDto> posts;
}
