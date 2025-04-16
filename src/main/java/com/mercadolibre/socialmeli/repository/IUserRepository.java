package com.mercadolibre.socialmeli.repository;

import com.mercadolibre.socialmeli.model.User;

import java.util.List;

public interface IUserRepository {

    void loadDataBase();


    User getUserById(Integer userId);
    List<User> getAll();

}
