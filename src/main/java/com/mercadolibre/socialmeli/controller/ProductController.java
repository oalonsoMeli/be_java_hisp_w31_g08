package com.mercadolibre.socialmeli.controller;


import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.dto.PromoPostDto;
import com.mercadolibre.socialmeli.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    private IProductService productService;

    @Autowired
    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/post")
    public ResponseEntity<Void> createPost(@RequestBody PostDto postDto) {
        productService.createPost(postDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/followed/{userId}/list")
    public ResponseEntity<?> getListOfPublicationsByUser(@PathVariable Integer userId){
        return new ResponseEntity<>(this.productService.getListOfPublicationsByUser(userId), HttpStatus.OK);
    }

    @PostMapping("/promo-post")
    public ResponseEntity<String> createPromoPost(@RequestBody PromoPostDto promoPostDto) {
        productService.createPost(promoPostDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }



}
