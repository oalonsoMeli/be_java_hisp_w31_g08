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
public class UserController implements IUserController {

    private IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<?> followUser(@PathVariable Integer userId, @PathVariable Integer userIdToFollow) {
        userService.followUser(userId, userIdToFollow);
        return  ResponseEntity.ok().build();

    }

    @Override
    public ResponseEntity<?> getFollowed(@PathVariable Integer userId,
                                         @RequestParam(value = "order", required = false) String order){
        return new ResponseEntity<>(userService.searchFollowedSellers(userId,order), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getFollowersCountByUserId(@PathVariable Integer userId){
        return new ResponseEntity<>(this.userService.getFollowersCountByUserId(userId), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Void> unfollowUser(@PathVariable Integer userId,
                                             @PathVariable Integer userIdToUnfollow) {
        userService.unfollowUser(userId, userIdToUnfollow);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<FollowersDto> getFollowers(@PathVariable Integer userId,
                                                    @RequestParam(value = "order", required = false) String order){
        return new ResponseEntity<>(userService.getUserFollowers(userId,order), HttpStatus.OK);
    }
}
