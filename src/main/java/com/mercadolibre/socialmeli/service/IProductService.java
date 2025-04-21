package com.mercadolibre.socialmeli.service;
import com.mercadolibre.socialmeli.dto.PostsDto;

public interface IProductService {
    PostsDto getListOfPublicationsByUser(Integer userId, String order);
}
