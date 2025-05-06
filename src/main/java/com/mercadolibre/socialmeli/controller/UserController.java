package com.mercadolibre.socialmeli.controller;
import com.mercadolibre.socialmeli.dto.FollowedDto;
import com.mercadolibre.socialmeli.dto.FollowerCountDto;
import com.mercadolibre.socialmeli.dto.FollowersDto;
import com.mercadolibre.socialmeli.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController implements IUserController {

    private IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    // Permite que un usuario siga a otro. Recibe los IDs de ambos usuarios.
    @Override
    public ResponseEntity<Void> followUser(@PathVariable Integer userId, @PathVariable Integer userIdToFollow) {
        userService.followUser(userId, userIdToFollow);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // este metodo deberia retornarme un followedDTO con un followedDTO que contiene el id del vendedor,
    // el nombre del vendedor, y una lista de sus seguidores.
    @Override
    public ResponseEntity<FollowedDto> getFollowed(@PathVariable Integer userId,
                                                   @RequestParam(value = "order", required = false) String order){
        return new ResponseEntity<>(userService.searchFollowedSellers(userId,order), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<FollowerCountDto> getFollowersCountByUserId(@PathVariable Integer userId){
        return new ResponseEntity<>(this.userService.getFollowersCountByUserId(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> unfollowUser(@PathVariable Integer userId,
                                             @PathVariable Integer userIdToUnfollow) {
        userService.unfollowUser(userId, userIdToUnfollow);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<FollowersDto> getFollowers(@PathVariable Integer userId,
                                                     @RequestParam(value = "order", required = false) String order){
        return new ResponseEntity<>(userService.getUserFollowers(userId,order), HttpStatus.OK);
    }
}