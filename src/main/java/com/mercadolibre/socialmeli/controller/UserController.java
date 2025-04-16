package com.mercadolibre.socialmeli.controller;


import com.mercadolibre.socialmeli.dto.FollowersDto;
import com.mercadolibre.socialmeli.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{userId}/followers/list")
    public ResponseEntity<List<FollowersDto>> getFollowers(@PathVariable Long userId) {
        return null;
    }

}
