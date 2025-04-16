package com.mercadolibre.socialmeli.controller;

import com.mercadolibre.socialmeli.dto.FollowersDto;
import com.mercadolibre.socialmeli.service.IUserService;
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

    @PostMapping("/{userId}/follow/{userIdToFollow}")
    public ResponseEntity<?> followUser(@PathVariable Integer userId, @PathVariable Integer userIdToFollow) {
        userService.followUser(userId, userIdToFollow);
        return  ResponseEntity.ok().build();

    }

    //Obtener  un listado de todos los vendedores a los cuales sigue un determinado usuario (¿A quién sigo?)
    @GetMapping ("/{userId}/followed/list")

    public ResponseEntity<?> getFollowed(@PathVariable Integer userId,
                                         @RequestParam(value = "order", required = false) String order){
        return new ResponseEntity<>(userService.searchFollowedSellers(userId,order), HttpStatus.OK);

    }

    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<?> getFollowersCountByUserId(@PathVariable Integer userId){
        return new ResponseEntity<>(this.userService.getFollowersCountByUserId(userId), HttpStatus.OK);

    }


    @PutMapping("/{userId}/unfollow/{userIdToUnfollow}")
    public ResponseEntity<Void> unfollowUser(@PathVariable Integer userId,
                                             @PathVariable Integer userIdToUnfollow) {
        userService.unfollowUser(userId, userIdToUnfollow);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/followers/list")
    public ResponseEntity<FollowersDto> getFollowers(@PathVariable Integer userId,
                                                    @RequestParam(value = "order", required = false) String order){
        return new ResponseEntity<>(userService.getUserFollowers(userId,order), HttpStatus.OK);
    }
}
