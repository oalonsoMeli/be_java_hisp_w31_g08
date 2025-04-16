package com.mercadolibre.socialmeli.service;

import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.dto.PostsDto;
import com.mercadolibre.socialmeli.dto.PostsDto;


public interface IProductService {
    void createPost(PostDto postDto);
    PostsDto getListOfPublicationsByUser(Integer userId);
}
