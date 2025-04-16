package com.mercadolibre.socialmeli.repository;

import com.mercadolibre.socialmeli.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {

    void loadDataBase();

    Optional<User> findById(Integer userId);

    List<Integer> findUserFollowers(Integer userId);

    List<User> findUsersById(List<Integer> userFollowersId);
}
