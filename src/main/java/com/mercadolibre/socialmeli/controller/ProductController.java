package com.mercadolibre.socialmeli.controller;
import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.dto.PromoPostDto;
import com.mercadolibre.socialmeli.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
public class ProductController {

    private IProductService productService;

    @Autowired
    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @Operation(
            summary = "Dar de alta una nueva publicación",
            description = "Permite al vendedor de alta una nueva publicación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publicación creada exitosamente."),
            @ApiResponse(responseCode = "400", description = "Usuario no encontrado")})
    @PostMapping("/post")
    public ResponseEntity<Void> createPost(@RequestBody PostDto postDto) {
        productService.createPost(postDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Listado de publicaciones que un usuario sigue",
            description = "Permite obtener un listado de las publicaciones realizadas por los vendedores que un usuario sigue en las últimas dos semanas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo la lista exitosamente."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Párametro de ordenamiento no permitido"),
            @ApiResponse(responseCode = "404", description = "No hay publicaciones de quienes sigues.")})
    @GetMapping("/followed/{userId}/list")
    public ResponseEntity<?> getListOfPublicationsByUser(@PathVariable Integer userId,
                                                         @RequestParam(value="order",required = false,
                                                         defaultValue = "date_asc")
                                                         String order){
    return new ResponseEntity<>(this.productService.getListOfPublicationsByUser(userId,order), HttpStatus.OK);
    }


    @Operation(
            summary = "Alta publicacion con promoción",
            description = "Permite llevar a cabo la publicación de un nuevo producto en promoción.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se creo el descuento correctamente"),
            @ApiResponse(responseCode = "400", description = "Descuento inválido.")})
    @PostMapping("/promo-post")
    public ResponseEntity<String> createPromoPost(@RequestBody PromoPostDto promoPostDto) {
        productService.createPost(promoPostDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(
            summary = "Cantidad de productos en promoción de un vendedor",
            description = "Permite obtener la cantidad de productos en promoción de un determinado vendedor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo una respuesta exitosa."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")})
    @GetMapping("/promo-post/count")
    public  ResponseEntity<?> getQuantityOfProducts(@RequestParam Integer user_id){
        return new ResponseEntity<>(productService.getQuantityOfProducts(user_id), HttpStatus.OK);
    }
}
