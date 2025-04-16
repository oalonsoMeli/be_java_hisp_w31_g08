package com.mercadolibre.socialmeli.service;

import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    private IUserRepository userRepository;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //buscar un usuario en la lista por su ID
    private User findUserById(Integer userId) {
        return userRepository.getAll().stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Usuario con ID " + userId + " no encontrado"));
    }

    //dar un unfollow de un usuario a un vendedor
    @Override
    public void unfollowUser(Integer userId, Integer userIdToUnfollow) {
        User user = findUserById(userId);
        User userToUnfollow = findUserById(userIdToUnfollow);

        if (!user.getFollows().contains(userToUnfollow)) {
            throw new NotFoundException("El usuario no sigue a este vendedor");
        }
        user.getFollows().remove(userToUnfollow);
    }


}
