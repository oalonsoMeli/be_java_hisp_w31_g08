package com.mercadolibre.socialmeli.service;
import com.mercadolibre.socialmeli.dto.*;

import java.util.HashMap;
import java.util.List;

public interface IProductService {
    PostsDto getListOfPublicationsByUser(Integer userId, String order);
    void createPost(PostDto postDto);
    PromoProductsCountDto getQuantityOfProducts(Integer userId);
    PromoProductsDto getPromotionalProductsFromSellers(Integer userId);
    void valorateAPost(ValorationDTO valorationDTO);
    List<ValorationDTO> getValorationsByPost(Integer postId, Integer valorationNumber);
    List<ValorationDTO> getAllValorationsByUser(Integer userId);
    ValorationAverageDto getValorationsAverageByPost(Integer postId);
}
