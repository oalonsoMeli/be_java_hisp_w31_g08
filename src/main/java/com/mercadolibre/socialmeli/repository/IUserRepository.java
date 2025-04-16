package com.mercadolibre.socialmeli.repository;

public interface IUserRepository {

    void loadDataBase();
    boolean followUser(Integer userId, Integer userIdToFollow);

}
