package com.mercadolibre.socialmeli.repository;

import com.mercadolibre.socialmeli.model.User;

import java.util.List;

import com.mercadolibre.socialmeli.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {

    void loadDataBase();


    Optional<User> getUserById(Integer userId);

    List<User> getAll();

    List<Integer> findUserFollowers(Integer userId);

    List<User> findUsersById(List<Integer> userFollowersId);
}
