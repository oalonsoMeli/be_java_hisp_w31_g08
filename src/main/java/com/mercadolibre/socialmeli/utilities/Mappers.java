package com.mercadolibre.socialmeli.utilities;

import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.model.Post;

public abstract class Mappers {

    public static PostDto postEntityToDto(Post post){
        return new PostDto(
                post.getUserId(),
                post.getDate(),
                post.getProduct(),
                post.getCategory(),
                post.getPrice()
        );
    }

    public static Post postDtoToEntity(PostDto postDto){
        return new Post(
                postDto.getUserId(),
                postDto.getDate(),
                postDto.getProduct(),
                postDto.getCategory(),
                postDto.getPrice()
        );
    }
}
