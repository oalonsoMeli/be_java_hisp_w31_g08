package com.mercadolibre.socialmeli.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.socialmeli.dto.FollowedDto;
import com.mercadolibre.socialmeli.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Repository
public class UserRepositoryImpl implements IUserRepository {

    private List<User> listOfUsers = new ArrayList<>();

    public UserRepositoryImpl() throws IOException {
        loadDataBase();
    }


    @Override
    public List<Integer> findUserFollowers(Integer userId) {
        return listOfUsers.stream()
                .filter(user -> user.getFollows().contains(userId))
                .map(User::getUserId)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUsersById(List<Integer> userFollowersId) {
        return listOfUsers.stream().filter(user -> userFollowersId.contains(user.getUserId())).collect(Collectors.toList());
    }

    public void loadDataBase() {
        File file;
        ObjectMapper objectMapper = new ObjectMapper();
        List<User> users ;

        try{
            file= ResourceUtils.getFile("classpath:data/users.json");
            users= objectMapper.readValue(file, new TypeReference<>() {
            });
            listOfUsers = users;
        }catch (Exception exception){
            throw new RuntimeException("No se pudo parsear el json de usuarios.");
        }
    }

    @Override
    public Optional<User> getUserById(Integer userId) {
        return this.listOfUsers.stream().filter(v -> v.getUserId().equals(userId)).findFirst();
    }

    @Override
    public List<User> getAll() {
        return listOfUsers;
    }
}
