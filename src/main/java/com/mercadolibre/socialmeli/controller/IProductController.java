package com.mercadolibre.socialmeli.controller;
import com.mercadolibre.socialmeli.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

public interface IProductController {
    @Operation(
            summary = "Dar de alta una nueva publicación",
            description = "Permite al vendedor de alta una nueva publicación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publicación creada exitosamente."),
            @ApiResponse(responseCode = "400", description = "Usuario no encontrado")})
    @PostMapping("/post")
    ResponseEntity<Void> createPost(@RequestBody @Valid PostDto postDto);

    @Operation(
            summary = "Listado de publicaciones que un usuario sigue",
            description = "Permite obtener un listado de las publicaciones realizadas por los vendedores que un usuario sigue en las últimas dos semanas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo la lista exitosamente."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Párametro de ordenamiento no permitido"),
            @ApiResponse(responseCode = "404", description = "No hay publicaciones de quienes sigues.")})
    @GetMapping("/followed/{userId}/list")
     ResponseEntity<PostsDto> getListOfPublicationsByUser(@PathVariable Integer userId,
                                                          @RequestParam(value="order",required = false,
                                                   defaultValue = "date_asc") String order);

    @Operation(
            summary = "Alta publicacion con promoción",
            description = "Permite llevar a cabo la publicación de un nuevo producto en promoción.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se creo el descuento correctamente"),
            @ApiResponse(responseCode = "400", description = "Descuento inválido.")})
    @PostMapping("/promo-post")
     ResponseEntity<Void> createPromoPost(@RequestBody @Valid PromoPostDto promoPostDto);


    @Operation(
            summary = "Cantidad de productos en promoción de un vendedor",
            description = "Permite obtener la cantidad de productos en promoción de un determinado vendedor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo una respuesta exitosa."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")})
    @GetMapping("/promo-post/count")
    ResponseEntity<PromoProductsCountDto> getQuantityOfProducts(@RequestParam Integer user_id);

    @Operation(
            summary = "Lista de productos en promoción de un vendedor",
            description = "Permite obtener la lista de productos en promoción de un determinado vendedor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo una respuesta exitosa."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")})
    @GetMapping("/promo-post/list")
   ResponseEntity<PromoProductsDto> getPromotionalProductsFromSellers(@RequestParam(value="user_id") Integer userId);

    @Operation(
            summary = "Usuario valora posteo",
            description = "Permite a un usuario valorar un posteo del 1 al 5.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se pudo valorar el posteo."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado."),
            @ApiResponse(responseCode = "404", description = "Posteo no encontrado."),
            @ApiResponse(responseCode = "400", description = "Se permiten solo valoraciones del 1 al 5.")})
    @PostMapping("/valoration")
     ResponseEntity<Void> valorateAPost(@RequestBody ValorationDTO valorationDTO);

    @Operation(
            summary = "Obtener valoraciones de un post",
            description = "Obtiene las valoraciones de un posteo, permite filtrar por RequestParam según número")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo valoraciones."),
            @ApiResponse(responseCode = "404", description = "Posteo no encontrado")})

    @GetMapping("/{post_id}/valorations")
     ResponseEntity<List<ValorationDTO>> getValorationsByPost(@PathVariable Integer post_id,
                                                              @RequestParam(value = "valoration_number", required = false)
                                                  Integer valorationNumber);

    @Operation(
            summary = "Obtener valoraciones de un usuario",
            description = "Obtiene la lista de todas las valoraciones hechas por un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo la lista de valoraciones del usuario."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")})
    @GetMapping("/{user_id}/user/valorations")
     ResponseEntity<List<ValorationDTO>> getAllValorationsByUser(@PathVariable Integer user_id);

    @Operation(
            summary = "Obtener el porcentaje de valoraciones de un posteo",
            description = "Obtiene el porcentaje de las valoraciones que se han hecho en un posteo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo el porcentaje del posteo."),
            @ApiResponse(responseCode = "404", description = "No se encontró el post a valorar.")})
    @GetMapping("/{post_id}/valorations/average")
    ResponseEntity<ValorationAverageDto> getValorationsByPost(@PathVariable Integer post_id);
}
