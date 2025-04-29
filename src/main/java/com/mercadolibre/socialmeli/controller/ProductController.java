package com.mercadolibre.socialmeli.controller;
import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.dto.PromoPostDto;
import com.mercadolibre.socialmeli.dto.PromoProductsDto;
import com.mercadolibre.socialmeli.dto.ValorationDTO;
import com.mercadolibre.socialmeli.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/products")
public class ProductController implements IProductController {

    private IProductService productService;

    @Autowired
    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @Override
    public ResponseEntity<String> createPost(@RequestBody @Valid PostDto postDto) {
        productService.createPost(postDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getListOfPublicationsByUser(@PathVariable Integer userId,
                                                         @RequestParam(value="order",required = false,
                                                         defaultValue = "date_asc")
                                                         String order){
    return new ResponseEntity<>(this.productService.getListOfPublicationsByUser(userId,order), HttpStatus.OK);
    }


    @Override
    public ResponseEntity<String> createPromoPost(@RequestBody @Valid PromoPostDto promoPostDto) {
        productService.createPost(promoPostDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> getQuantityOfProducts(@RequestParam Integer user_id){
        return new ResponseEntity<>(productService.getQuantityOfProducts(user_id), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getPromotionalProductsFromSellers(@RequestParam(value="user_id") Integer userId){
        return new ResponseEntity<>(this.productService.getPromotionalProductsFromSellers(userId),
                HttpStatus.OK);
    }
    @Override
    public ResponseEntity<?> valorateAPost(@RequestBody ValorationDTO valorationDTO){
        this.productService.valorateAPost(valorationDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Override
    public ResponseEntity<?> getValorationsByPost(@PathVariable Integer post_id,
                                                  @RequestParam(value = "valoration_number", required = false)
                                                  Integer valorationNumber){
        return new ResponseEntity<>(this.productService.getValorationsByPost(post_id, valorationNumber),
                HttpStatus.OK);
    }
    @Override
    public ResponseEntity<?> getAllValorationsByUser(@PathVariable Integer user_id){
          return new ResponseEntity<>(this.productService.getAllValorationsByUser(user_id),
    HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getValorationsByPost(@PathVariable Integer post_id){
        return new ResponseEntity<>(this.productService.getValorationsAverageByPost(post_id),
                HttpStatus.OK);
    }

}
