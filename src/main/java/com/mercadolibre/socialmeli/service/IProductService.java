package com.mercadolibre.socialmeli.service;
import com.mercadolibre.socialmeli.dto.PostDto;
import java.util.List;

public interface IProductService {

    List<PostDto> orderByDateAscOrDesc(String order);
}
