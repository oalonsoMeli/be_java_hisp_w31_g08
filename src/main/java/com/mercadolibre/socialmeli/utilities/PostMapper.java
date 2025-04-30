package com.mercadolibre.socialmeli.utilities;

import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.dto.ProductDto;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.Product;

public class PostMapper {
    private static int postIdCounter = 1;

    public static Post toPost(PostDto postDto) {
        ProductDto productDto = postDto.getProduct();
        Product product = new Product(
                productDto.getProduct_id(),
                productDto.getProduct_name(),
                productDto.getType(),
                productDto.getBrand(),
                productDto.getColor(),
                productDto.getNotes()
        );

        Post post = new Post(
                postDto.getUserId(),
                postDto.getDate(),
                product,
                postDto.getCategory(),
                postDto.getPrice()
        );

        post.setPostId(postIdCounter++);
        return post;
    }
}
