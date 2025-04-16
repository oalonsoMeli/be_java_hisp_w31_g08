package com.mercadolibre.socialmeli.controller;


import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
