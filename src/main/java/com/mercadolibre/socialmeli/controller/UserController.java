package com.mercadolibre.socialmeli.controller;
import com.mercadolibre.socialmeli.dto.FollowersDto;
import com.mercadolibre.socialmeli.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Seguir a un vendedor",
            description = "Permite que un usuario siga a un vendedor determinado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Follow exitoso"),
            @ApiResponse(responseCode = "400", description = "Usuario no encontrado")})
    @PostMapping("/{userId}/follow/{userIdToFollow}")
    public ResponseEntity<?> followUser(@PathVariable Integer userId, @PathVariable Integer userIdToFollow) {
        userService.followUser(userId, userIdToFollow);
        return  ResponseEntity.ok().build();

    }

    @Operation(
            summary = "Listado de todos los vendedores que sigue un usuario",
            description = "Permite obtener  un listado de todos los vendedores a los cuales sigue un determinado usuario (¿A quién sigo?)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo la lista exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")})
    @GetMapping ("/{userId}/followed/list")
    public ResponseEntity<?> getFollowed(@PathVariable Integer userId,
                                         @RequestParam(value = "order", required = false) String order){
        return new ResponseEntity<>(userService.searchFollowedSellers(userId,order), HttpStatus.OK);
    }

    @Operation(
            summary = "Cantidad de usuarios que siguen a un vendedor",
            description = "Permite obtener el resultado de la cantidad de usuarios que siguen a un determinado vendedor"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Respuesta exitosa"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<?> getFollowersCountByUserId(@PathVariable Integer userId){
        return new ResponseEntity<>(this.userService.getFollowersCountByUserId(userId), HttpStatus.OK);

    }

    @Operation(
            summary = "Dejar de seguir a un vendedor",
            description = "Permite que un usuario deje de seguir a un vendedor determinado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unfollow exitoso"),
            @ApiResponse(responseCode = "404", description = "Usuario o vendedor no encontrado"),
            @ApiResponse(responseCode = "400", description = "El usuario no seguía al vendedor")
    })
    @PutMapping("/{userId}/unfollow/{userIdToUnfollow}")
    public ResponseEntity<Void> unfollowUser(@PathVariable Integer userId,
                                             @PathVariable Integer userIdToUnfollow) {
        userService.unfollowUser(userId, userIdToUnfollow);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Listado de todos los usuarios que siguen a un vendedor",
            description = "Obtener un listado de todos los usuarios que siguen a un determinado vendedor (¿Quién me sigue?)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo la lista exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")})
    @GetMapping("/{userId}/followers/list")
    public ResponseEntity<FollowersDto> getFollowers(@PathVariable Integer userId,
                                                    @RequestParam(value = "order", required = false) String order){
        return new ResponseEntity<>(userService.getUserFollowers(userId,order), HttpStatus.OK);
    }
}
