package com.mercadolibre.socialmeli.service;
import com.mercadolibre.socialmeli.dto.PostsDto;

public interface IProductService {
    PostsDto orderByDateAscOrDesc(String order);
}
