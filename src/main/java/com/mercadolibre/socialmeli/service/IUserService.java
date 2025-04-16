package com.mercadolibre.socialmeli.service;

import com.mercadolibre.socialmeli.dto.FollowedDto;

import java.util.List;

public interface IUserService {
    List<FollowedDto> searchFollowedSellers(Integer userId);

}
