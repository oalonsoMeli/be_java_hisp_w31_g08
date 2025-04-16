package com.mercadolibre.socialmeli.service;

import com.mercadolibre.socialmeli.dto.FollowerCountDto;

public interface IUserService {

    FollowerCountDto getFollowersCountByUserId(Integer userId);

    void unfollowUser(Integer userId, Integer userIdToUnfollow);

}
