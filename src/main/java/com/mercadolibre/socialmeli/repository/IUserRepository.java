package com.mercadolibre.socialmeli.repository;

import com.mercadolibre.socialmeli.model.User;

import java.util.List;

public interface IUserRepository {

    void loadDataBase();
    List<User> getAll();

}
