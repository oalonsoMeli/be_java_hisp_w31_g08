package com.mercadolibre.socialmeli.service;
import com.mercadolibre.socialmeli.dto.FollowedDto;

import com.mercadolibre.socialmeli.dto.FollowerCountDto;

import com.mercadolibre.socialmeli.dto.FollowersDto;

import java.util.List;

public interface IUserService {
    void followUser(Integer userId, Integer userIdToFollow);

    FollowedDto searchFollowedSellers(Integer userId, String order);

    FollowerCountDto getFollowersCountByUserId(Integer userId);

    void unfollowUser(Integer userId, Integer userIdToUnfollow);

    FollowersDto getUserFollowers(Integer userId, String order);

}
