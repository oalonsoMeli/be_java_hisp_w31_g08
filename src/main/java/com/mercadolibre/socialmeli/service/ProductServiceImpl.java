package com.mercadolibre.socialmeli.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.dto.ProductDto;
import com.mercadolibre.socialmeli.exception.BadRequestException;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.Product;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IProductRepository;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements IProductService {

    private final IUserRepository userRepository;
    private final IProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ProductServiceImpl(IUserRepository userRepository, IProductRepository productRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
    }


    @Override
    public void createPost(PostDto postDto) {
        userRepository.getUserById(postDto.getUser_id())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

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
                postDto.getUser_id(),
                postDto.getDate(),
                product,
                postDto.getCategory(),
                postDto.getPrice()
        );

        productRepository.save(post);
    }



}
