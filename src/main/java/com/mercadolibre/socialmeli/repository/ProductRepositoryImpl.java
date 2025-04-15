package com.mercadolibre.socialmeli.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.socialmeli.model.Product;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryImpl implements IProductRepository {

    private List<Product> listOfProducts = new ArrayList<>();

    public ProductRepositoryImpl() throws IOException {
        loadDataBase();
    }

    public void loadDataBase() {
        File file;
        ObjectMapper objectMapper = new ObjectMapper();
        List<Product> products ;

        try{
            file= ResourceUtils.getFile("classpath:data/products.json");
            products= objectMapper.readValue(file, new TypeReference<>() {
            });
            listOfProducts = products;
        }catch (Exception exception){
            throw new RuntimeException("No se pudo parsear el json de productos.");
        }
    }


}
