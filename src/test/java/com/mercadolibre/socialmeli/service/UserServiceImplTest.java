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


    @Test
    // T-0001 - US0001 Caso borde: Verifica que el usuario a seguir exista y se pueda seguir correctamente
    void followUser_shouldAddFollowingSuccessfully_whenUserToFollowExists() {
        // Arrange
        User follower = TestFactory.createUser(1);
        User toFollow = TestFactory.createUser(2);

        when(repository.getUserById(1)).thenReturn(Optional.of(follower));
        when(repository.getUserById(2)).thenReturn(Optional.of(toFollow));

        // Act
        service.followUser(1, 2);

        // Assert
        assertTrue(follower.getFollows().contains(2), "El ID del usuario seguido debe estar en la lista");
    }


    @Test
    // T-0001 - US0001 Caso borde: Verifica que se lance excepción cuando el usuario a seguir no existe
    void followUser_shouldThrowException_whenUserToFollowDoesNotExist() {
        // Arrange
        User follower = TestFactory.createUser(1);

        when(repository.getUserById(1)).thenReturn(Optional.of(follower));
        when(repository.getUserById(2)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            service.followUser(1, 2);
        });
    }



    @Test
    // T-0001 - US0001 Caso borde: Verifica que se lance una excepción cuando el userId sea nulo
    void followUser_shouldThrowException_whenUserIdIsNull() {
        assertThrows(BadRequestException.class, () -> {
            service.followUser(null, 2);
        });
    }

    @Test
    // T-0001 - US0001 Caso borde: Verifica que se lance una excepción cuando el userIdToFollow sea nulo
    void followUser_shouldThrowException_whenUserToFollowIdIsNull() {

        assertThrows(BadRequestException.class, () -> {
            service.followUser(1, null);
        });
    }



    @Test
    // T-0001 - US0001 Caso borde: Verifica que no se permita seguir a un usuario que ya está en la lista de seguidos
    void followUser_shouldNotAddDuplicateFollow() {
        // Arrange
        User follower = TestFactory.createUser(1);
        User toFollow = TestFactory.createUser(2);
        follower.getFollows().add(2);

        when(repository.getUserById(1)).thenReturn(Optional.of(follower));
        when(repository.getUserById(2)).thenReturn(Optional.of(toFollow));

        // Act
        service.followUser(1, 2);

        // Assert
        assertEquals(1, follower.getFollows().size(), "El usuario no debe ser seguido más de una vez.");
    }

    @Test
    // T-0001 - US0001  Caso borde: Verifica que se lance una excepción cuando no se encuentra al usuario a seguir
    void followUser_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(repository.getUserById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            service.followUser(1, 2);
        });
    }

    @Test
    // T-0001 - US0001  Caso borde: Verifica que el usuario puede seguir correctamente a otro usuario cuando todo está bien
    void followUser_shouldAddFollowingSuccessfully_whenEverythingIsValid() {
        // Arrange
        User follower = TestFactory.createUser(1);
        User toFollow = TestFactory.createUser(2);

        when(repository.getUserById(1)).thenReturn(Optional.of(follower));
        when(repository.getUserById(2)).thenReturn(Optional.of(toFollow));

        // Act
        service.followUser(1, 2);

        // Assert
        assertTrue(follower.getFollows().contains(2), "El ID del usuario seguido debe estar en la lista");
    }







}