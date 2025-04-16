package com.mercadolibre.socialmeli.service;

import com.mercadolibre.socialmeli.dto.FollowersDto;

import java.util.List;

public interface IUserService {

    FollowersDto getUserFollowers(Integer userId);
}
