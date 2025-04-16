package com.mercadolibre.socialmeli.repository;
import com.mercadolibre.socialmeli.model.Post;

import java.util.List;

public interface IProductRepository {
    List<Post> orderByDateAscOrDesc(String order);
}
