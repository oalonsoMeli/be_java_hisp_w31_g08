package com.mercadolibre.socialmeli.service;
import com.mercadolibre.socialmeli.exception.BadRequestException;
import com.mercadolibre.socialmeli.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.socialmeli.dto.FollowedDto;
import com.mercadolibre.socialmeli.dto.UserDto;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.dto.FollowersDto;
import com.mercadolibre.socialmeli.dto.FollowerCountDto;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements IUserService {

    private IUserRepository userRepository;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override

    // Sigue a otro usuario

    public void followUser(Integer userId, Integer userIdToFollow) {
        Optional<User> userOptional = getUser(userId, userIdToFollow);
        User user =  userOptional.get();
            user.getFollows().add(userIdToFollow);
        }

    // Obtiene los usuarios a partir de sus IDs

    private Optional<User> getUser(Integer userId, Integer userIdToFollow) {
        Optional<User> userOptional = this.userRepository.getUserById(userId);
        Optional<User> userTofollow = this.userRepository.getUserById(userIdToFollow);
        if(userOptional.isEmpty() || userTofollow.isEmpty()) {
                throw new BadRequestException("Usuario no encontrado.");
        }
        return userOptional;
    }


    // genera la lista de a quien sigue un vendedor
    public FollowedDto searchFollowedSellers(Integer userId, String order) {
        ObjectMapper mapper = new ObjectMapper();
        User user = this.userRepository.getUserById(userId).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado.")); // busca el usuario vendedor por id
        List<User> userFollowed = new ArrayList<>(userRepository.findUsersById(
                new ArrayList<>(user.getFollows()))); // busca los usuarios de getFollows del vendedor según los id
                                                      // y genera la lista de los usuarios

        orderByName(order, userFollowed); // ordenamiento alfabeticamente de nombres
        List<UserDto> userDtos = userFollowed.stream()
                .map(uf -> new UserDto(uf.getUserId(), uf.getUserName()))
                .collect(Collectors.toList());
        return new FollowedDto(user.getUserId(), user.getUserName(), userDtos);
    }


    // ordena la lista de los seguidores de un vendedor según su nombre alfabéticamente
    private static void orderByName(String order, List<User> userFollowed) {
        if ("name_asc".equalsIgnoreCase(order)) {
            userFollowed.sort(Comparator.comparing(User::getUserName));
        } else if ("name_desc".equalsIgnoreCase(order)) {
            userFollowed.sort(Comparator.comparing(User::getUserName).reversed());
        }
    }

    // Cuenta la cantidad de seguidores que tiene derteminado usuario.
    public FollowerCountDto getFollowersCountByUserId(Integer userId) {
        User user = this.userRepository.getUserById(userId).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado."));
        List<User> users = this.userRepository.getAll();
        long count = users.stream().filter(v -> v.getFollows().contains(userId)).count();
        return new FollowerCountDto(user.getUserId(), user.getUserName(), (int) count);
     }

    @Override
    public void unfollowUser(Integer userId, Integer userIdToUnfollow) {
        Optional<User> userOptional = getUser(userId, userIdToUnfollow);
        User user =  userOptional.get();
        if (!user.getFollows().contains(userIdToUnfollow)) {
            throw new NotFoundException("El usuario no sigue a este vendedor.");
        }
        user.getFollows().remove(userIdToUnfollow);
    }


    // genera la lista de los seguidores de un vendedor
    @Override
    public FollowersDto getUserFollowers(Integer userId, String order) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<User> userFollowers = new ArrayList<>(userRepository.findUsersById(
                new ArrayList<>(user.getFollows())));
        orderByName(order, userFollowers);
        List<UserDto> userDtos = userFollowers.stream()
                .map(userFollower -> new UserDto(userFollower.getUserId(), userFollower.getUserName()))
                .collect(Collectors.toList());
        return new FollowersDto(user.getUserId(), user.getUserName(), userDtos);
    }
}
