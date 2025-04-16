package com.mercadolibre.socialmeli.service;

import com.mercadolibre.socialmeli.dto.FollowersDto;
import com.mercadolibre.socialmeli.dto.UserDto;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.dto.FollowerCountDto;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public FollowerCountDto getFollowersCountByUserId(Integer userId) {
        User user = this.userRepository.getUserById(userId).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado")
        );
        List<User> users = this.userRepository.getAll();
        long count = users.stream().filter(v -> v.getFollows().contains(userId)).count();
        return new FollowerCountDto(user.getUserId(), user.getUserName(), (int) count);
     }


    //dar un unfollow de un usuario a un vendedor
    @Override
    public void unfollowUser(Integer userId, Integer userIdToUnfollow) {
        Optional<User> userOptional = this.userRepository.getUserById(userId);
        Optional<User> userToUnfollow = this.userRepository.getUserById(userIdToUnfollow);
        if(userOptional.isEmpty() || userToUnfollow.isEmpty()) {
            throw new NotFoundException("Usuario no encontrado");
        }
        User user = userOptional.get();
        if (!user.getFollows().contains(userToUnfollow.get().getUserId())) {
            throw new NotFoundException("El usuario no sigue a este vendedor");
        }
        user.getFollows().remove(userToUnfollow.get().getUserId());
    }



    @Override
    public FollowersDto getUserFollowers(Integer userId) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<User> userFollowers = userRepository.findUsersById(
                userRepository.findUserFollowers(userId)
        );
        List<UserDto> userDtos = userFollowers.stream()
                .map(userFollower -> new UserDto(userFollower.getUserId(), userFollower.getUserName()))
                .collect(Collectors.toList());
        return new FollowersDto(user.getUserId(), user.getUserName(), userDtos);
    }
}
