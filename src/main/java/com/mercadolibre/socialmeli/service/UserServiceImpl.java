package com.mercadolibre.socialmeli.service;
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

    public void followUser(Integer userId, Integer userIdToFollow) {
        Optional<User> userOptional = this.userRepository.getUserById(userId);
        Optional<User> userTofollow = this.userRepository.getUserById(userIdToFollow);
        if(userOptional.isEmpty() || userTofollow.isEmpty()) {
                throw new NotFoundException("Usuario no encontrado");
        }
            User user =  userOptional.get();
            user.getFollows().add(userIdToFollow);
        }

    public FollowedDto searchFollowedSellers(Integer userId, String order) {
        ObjectMapper mapper = new ObjectMapper();
        User user = this.userRepository.getUserById(userId).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado")
        );

        List<User> userFollowed = userRepository.findUsersById(
               user.getFollows().stream().toList()
        );

        // ordenar
        if ("name_asc".equalsIgnoreCase(order)) {
            userFollowed.sort(Comparator.comparing(User::getUserName));
        } else if ("name_desc".equalsIgnoreCase(order)) {
            userFollowed.sort(Comparator.comparing(User::getUserName).reversed());
        }

        List<UserDto> userDtos = userFollowed.stream()
                .map(uf -> new UserDto(uf.getUserId(), uf.getUserName()))
                .collect(Collectors.toList());
        return new FollowedDto(user.getUserId(), user.getUserName(), userDtos);
    }

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
    public FollowersDto getUserFollowers(Integer userId, String order) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<User> userFollowers = userRepository.findUsersById(
                userRepository.findUserFollowers(userId)
        );
        // ordenamiento por nombre
        if ("name_asc".equalsIgnoreCase(order)) {
            userFollowers.sort(Comparator.comparing(User::getUserName));
        } else if ("name_desc".equalsIgnoreCase(order)) {
            userFollowers.sort(Comparator.comparing(User::getUserName).reversed());
        }
        List<UserDto> userDtos = userFollowers.stream()
                .map(userFollower -> new UserDto(userFollower.getUserId(), userFollower.getUserName()))
                .collect(Collectors.toList());
        return new FollowersDto(user.getUserId(), user.getUserName(), userDtos);
    }
}
