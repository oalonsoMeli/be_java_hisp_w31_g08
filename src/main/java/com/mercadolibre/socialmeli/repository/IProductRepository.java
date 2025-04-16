package com.mercadolibre.socialmeli.repository;

import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.model.Post;

import java.util.List;

public interface IProductRepository {
    void save(Post post);
    List<Post> getAll();
}
