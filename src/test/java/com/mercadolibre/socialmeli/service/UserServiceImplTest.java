package com.mercadolibre.socialmeli.service;

import com.mercadolibre.socialmeli.dto.FollowedDto;
import com.mercadolibre.socialmeli.dto.UserDto;
import com.mercadolibre.socialmeli.exception.BadRequestException;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.factory.TestFactory;
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
    // T-0002 - US0007: Verifica que se lance una excepcion cuando el usuario a dejar de seguir no existe
    void unfollowUser_shouldThrowExceptionWhenUserToUnfollowDoesNotExist() {
        // Arrange
        User user = com.mercadolibre.socialmeli.factory.TestFactory.createUserWithFollow(1, 2);

        when(repository.getUserById(1)).thenReturn(Optional.of(user));
        when(repository.getUserById(2)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(com.mercadolibre.socialmeli.exception.BadRequestException.class, () -> {
            service.unfollowUser(1, 2);
        });
    }

    @Test
    // T-0002 - US0007: Verifica que se elimine el usuario a dejar de seguir cuando existe y esta en la lista
    void unfollowUser_shouldRemoveUserSuccessfully() {
        // Arrange
        User user = com.mercadolibre.socialmeli.factory.TestFactory.createUserWithFollow(1, 2);
        User toUnfollow = com.mercadolibre.socialmeli.factory.TestFactory.createUser(2);
        when(repository.getUserById(1)).thenReturn(Optional.of(user));
        when(repository.getUserById(2)).thenReturn(Optional.of(toUnfollow));
        // Act
        service.unfollowUser(1, 2);
        // Assert
        assertFalse(user.getFollows().contains(2));
    }

    @Test
    // T002 - US0007: Caso borde - el usuario existe pero no sigue a nadie
    // Al intentar dejar de seguir, deberia lanzar excepcion por no encontrar la relacion
    void unfollowUser_shouldThrowWhenUserHasNoFollowings() {
        User user = TestFactory.createUser(1);
        User target = TestFactory.createUser(2);

        when(repository.getUserById(1)).thenReturn(Optional.of(user));
        when(repository.getUserById(2)).thenReturn(Optional.of(target));

        assertThrows(NotFoundException.class, () -> {
            service.unfollowUser(1, 2);
        });
    }

    @Test
    // T002 - US0007: Caso borde - IDs invalidos que deberían provocar una excepcion
    void unfollowUser_shouldThrowWhenUserIdIsNull() {
        assertThrows(BadRequestException.class, () -> {
            service.unfollowUser(null, 2);
        });
    }

}