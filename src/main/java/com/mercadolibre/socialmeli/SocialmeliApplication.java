package com.mercadolibre.socialmeli;

import com.mercadolibre.socialmeli.repository.ProductRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class SocialmeliApplication {

    public static void main(String[] args){
        SpringApplication.run(SocialmeliApplication.class, args);
    }

}
