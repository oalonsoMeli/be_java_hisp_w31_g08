package com.mercadolibre.socialmeli.controller;
import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.dto.PromoPostDto;
import com.mercadolibre.socialmeli.dto.PromoProductsDto;
import com.mercadolibre.socialmeli.dto.ValorationDTO;
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
    public ResponseEntity<?> getQuantityOfProducts(@RequestParam Integer user_id){
        return new ResponseEntity<>(productService.getQuantityOfProducts(user_id), HttpStatus.OK);
    }

    @Operation(
            summary = "Lista de productos en promoción de un vendedor",
            description = "Permite obtener la lista de productos en promoción de un determinado vendedor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo una respuesta exitosa."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")})
    @GetMapping("/promo-post/list")
    public ResponseEntity<?> getPromotionalProductsFromSellers(@RequestParam(value="user_id") Integer userId){
        return new ResponseEntity<>(this.productService.getPromotionalProductsFromSellers(userId),
                HttpStatus.OK);
    }
    @Operation(
            summary = "Usuario valora posteo",
            description = "Permite a un usuario valorar un posteo del 1 al 5.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se pudo valorar el posteo."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado."),
            @ApiResponse(responseCode = "404", description = "Posteo no encontrado."),
            @ApiResponse(responseCode = "400", description = "Se permiten solo valoraciones del 1 al 5.")})
    @PostMapping("/valoration")
    public ResponseEntity<?> valorateAPost(@RequestBody ValorationDTO valorationDTO){
        this.productService.valorateAPost(valorationDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Operation(
            summary = "Obtener valoraciones de un post",
            description = "Obtiene las valoraciones de un posteo, permite filtrar por RequestParam según número")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo valoraciones."),
            @ApiResponse(responseCode = "404", description = "Posteo no encontrado")})

    @GetMapping("/{post_id}/valorations")
    public ResponseEntity<?> getValorationsByPost(@PathVariable Integer post_id,
                                                  @RequestParam(value = "valoration_number", required = false)
                                                  Integer valorationNumber){
        return new ResponseEntity<>(this.productService.getValorationsByPost(post_id, valorationNumber),
                HttpStatus.OK);
    }
    @Operation(
            summary = "Obtener valoraciones de un usuario",
            description = "Obtiene la lista de todas las valoraciones hechas por un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo la lista de valoraciones del usuario."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")})
    @GetMapping("/{user_id}/user/valorations")
    public ResponseEntity<?> getAllValorationsByUser(@PathVariable Integer user_id){
          return new ResponseEntity<>(this.productService.getAllValorationsByUser(user_id),
    HttpStatus.OK);
    }

    @Operation(
            summary = "Obtener el porcentaje de valoraciones de un posteo",
            description = "Obtiene el porcentaje de las valoraciones que se han hecho en un posteo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo el porcentaje del posteo."),
            @ApiResponse(responseCode = "404", description = "No se encontró el post a valorar.")})
    @GetMapping("/{post_id}/valorations/average")
    public ResponseEntity<?> getValorationsByPost(@PathVariable Integer post_id){
        return new ResponseEntity<>(this.productService.getValorationsAverageByPost(post_id),
                HttpStatus.OK);
    }

}
