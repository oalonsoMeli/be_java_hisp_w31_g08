package com.mercadolibre.socialmeli.service;

import com.mercadolibre.socialmeli.dto.FollowedDto;
import com.mercadolibre.socialmeli.dto.UserDto;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    IUserRepository repository;

    @InjectMocks
    UserServiceImpl service;


    @BeforeEach
    void setUp() {
    }


    @Test
    void searchFollowedSellers_withOrderAsc_shouldReturnTheUsersNameWithOrderAsc() {
        // Arrange
        User user1 = new User(1, "Orne", "orne@example.com", Set.of());
        User user3 = new User(3, "Abril", "abril@example.com", Set.of());
        User user2 = new User(2, "Yoa", "yoa@example.com", Set.of(1, 3)); // Sigue a user1 y user3

        when(repository.getUserById(2)).thenReturn(Optional.of(user2));
        when(repository.findUsersById(List.of(1, 3))).thenReturn(List.of(user1, user3));

        // Act
        FollowedDto result = service.searchFollowedSellers(2, "name_asc");

        // Assert
        assertEquals(2, result.getFollowed().size());
        assertEquals("Abril", result.getFollowed().get(0).getUser_name());
        assertEquals("Orne", result.getFollowed().get(1).getUser_name());
    }

    @Test
    void searchFollowedSellers_withOrderDesc_shouldReturnTheUsersNameWithOrderDesc(){
        User user1 = new User(1, "Orne", "orne@example.com", Set.of());
        User user3 = new User(3, "Abril", "abril@example.com", Set.of());
        User user2 = new User(2, "Yoa", "yoa@example.com", Set.of(1, 3)); // Sigue a user1 y user3

        when(repository.getUserById(2)).thenReturn(Optional.of(user2));
        when(repository.findUsersById(List.of(1, 3))).thenReturn(List.of(user1, user3));

        // Act
        FollowedDto result = service.searchFollowedSellers(2, "name_desc");

        // Assert
        assertEquals(2, result.getFollowed().size());
        assertEquals("Orne", result.getFollowed().get(0).getUser_name());
        assertEquals("Abril", result.getFollowed().get(1).getUser_name());
    }
}