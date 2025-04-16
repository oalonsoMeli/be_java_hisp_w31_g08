package com.mercadolibre.socialmeli.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.socialmeli.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepositoryImpl implements IUserRepository {

    private List<User> listOfUsers = new ArrayList<>();

    public UserRepositoryImpl() throws IOException {
        loadDataBase();
    }

    @Override
    public boolean followUser(Integer userId, Integer userIdToFollow) {
        User user = listOfUsers.stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        User userToFollow = listOfUsers.stream()
                .filter(u -> u.getUserId().equals(userIdToFollow))
                .findFirst()
                .orElse(null);

        if (user == null || userToFollow == null) {
            return false;
        }

        if (userId.equals(userIdToFollow)) {
            return false;
        }

        if (user.getFollows().contains(userIdToFollow)) {
            return false;
        }

        user.getFollows().add(userIdToFollow);
        return true;
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
}
