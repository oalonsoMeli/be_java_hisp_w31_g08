package com.mercadolibre.socialmeli.service;
import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.dto.PostsDto;
import com.mercadolibre.socialmeli.dto.PromoProductsCountDto;
import com.mercadolibre.socialmeli.dto.PromoProductsDto;

public interface IProductService {
    PostsDto getListOfPublicationsByUser(Integer userId, String order);
    void createPost(PostDto postDto);
    PromoProductsCountDto getQuantityOfProducts(Integer userId);
    PromoProductsDto getPromotionalProductsFromSellers(Integer userId);
}
