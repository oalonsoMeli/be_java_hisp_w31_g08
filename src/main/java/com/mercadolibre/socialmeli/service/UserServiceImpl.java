package com.mercadolibre.socialmeli.service;

import com.mercadolibre.socialmeli.dto.FollowersDto;
import com.mercadolibre.socialmeli.dto.UserDto;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public FollowersDto getUserFollowers(Integer userId) {
        User user = userRepository.findById(userId)
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
