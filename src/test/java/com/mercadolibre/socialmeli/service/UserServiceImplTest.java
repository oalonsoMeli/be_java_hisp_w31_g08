package com.mercadolibre.socialmeli.service;

import com.mercadolibre.socialmeli.dto.FollowedDto;
import com.mercadolibre.socialmeli.dto.FollowersDto;
import com.mercadolibre.socialmeli.exception.BadRequestException;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    IUserRepository repository;

    @InjectMocks
    UserServiceImpl service;


    // pruebo que al pedir la lista de seguidores de un vendedor de forma ascendente,
    // debería la lista contener usuarios y no estar vacía
    @Test
    void searchFollowersUsers_withOrderAsc_shouldExistsAndContainUsers() {
        // Arrange
        User user1 = TestFactory.createUser(1);

        User user3 = TestFactory.createUser(3);

        Integer userId = 2;
        User user2 = TestFactory.createUserFollowing(userId, 1, 3);

        when(repository.getUserById(2)).thenReturn(Optional.of(user2));
        when(repository.findUsersById(List.of(1, 3))).thenReturn(List.of(user1, user3));

        // Act
        FollowersDto result = service.getUserFollowers(2, "name_asc");

        // Assert
        assertEquals(2, result.getFollowers().size());
        assertFalse(result.getFollowers().isEmpty());
    }

    // pruebo que al pedir la lista de los seguidores de un vendedor de forma descendente,
    // debería la lista no estar vacía y tener usuarios
    @Test
    void searchFollowersUsers_withOrderDesc_shouldExistsAndContainUsers() {
        // Arrange
        User user1 = TestFactory.createUser(1);

        User user3 = TestFactory.createUser(3);

        User user2 = TestFactory.createUserFollowing(2, 1, 3);

        when(repository.getUserById(2)).thenReturn(Optional.of(user2));
        when(repository.findUsersById(List.of(1, 3))).thenReturn(List.of(user1, user3));

        // Act
        FollowersDto result = service.getUserFollowers(2, "name_desc");

        // Assert
        assertEquals(2, result.getFollowers().size());
        assertFalse(result.getFollowers().isEmpty());
    }

    // pruebo que al pedir la lista de seguidos de un usuario de forma ascendente,
    // debería la lista contener usuarios y no estar vacía
    @Test
    void searchFollowedSellers_withOrderAsc_shouldExistsAndContainUsers() {
        // Arrange
        User user1 = TestFactory.createUserFollowing(1, 2, 3);

        User user2 = TestFactory.createUser(2);

        User user3 = TestFactory.createUser(3);


        when(repository.getUserById(1)).thenReturn(Optional.of(user1));
        when(repository.findUsersById(List.of(2, 3))).thenReturn(List.of(user2, user3));

        // Act
        FollowedDto result = service.searchFollowedSellers(1, "name_asc");

        // Assert
        assertEquals(2, result.getFollowed().size());
        assertFalse(result.getFollowed().isEmpty());
    }

    // pruebo que al pedir la lista de los seguidores de forma descendente,
    // debería la lista no estar vacía y tener usuarios
    @Test
    void searchFollowedSellers_withOrderDesc_shouldExistsAndContainUsers() {
        // Arrange
        User user1 = TestFactory.createUserFollowing(1, 2, 3);

        User user2 = TestFactory.createUser(2);

        User user3 = TestFactory.createUser(3);

        when(repository.getUserById(1)).thenReturn(Optional.of(user1));
        when(repository.findUsersById(List.of(2, 3))).thenReturn(List.of(user2, user3));

        // Act
        FollowedDto result = service.searchFollowedSellers(1, "name_desc");

        // Assert
        assertEquals(2, result.getFollowed().size());
        assertFalse(result.getFollowed().isEmpty());

    }


    // pruebo que al pedir la lista de los seguidos con un id inexistente, debería devolverme 404
    @Test
    void searchFollowedSellers_withAnUserIdInexistent_shouldReturnAnException() {
        // Arrange
        when(repository.getUserById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            service.searchFollowedSellers(999, "name_desc");
        });
    }

    // pruebo que al pedir la lista de los seguidos con vendedores cuyos id son inexistentes,
    // debería devolverme 404
    @Test
    void searchFollowedSellers_withFollowedUsersIdInexistent_shouldReturnAnException() {
        // Arrange
        User user2 = TestFactory.createUserFollowing(2, 999, 3245);
        when(repository.getUserById(2)).thenReturn(Optional.of(user2));
        when(repository.findUsersById(List.of(999, 3245))).thenThrow(NotFoundException.class);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            service.searchFollowedSellers(2, "name_desc");
        });
    }


    // pruebo que al pedir la lista de los seguidos con un id inexistente, debería devolverme 404
    @Test
    void searchFollowersUsers_withAnUserIdInexistent_shouldReturnAnException() {
        // Arrange
        when(repository.getUserById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            service.getUserFollowers(999, "name_desc");
        });
    }

    // pruebo que al pedir la lista de los seguidos con usuarios cuyos id son inexistentes,
    // debería devolverme 404
    @Test
    void searchFollowersUsers_withFollowedUsersIdInexistent_shouldReturnAnException() {
        // Arrange
        User user2 = TestFactory.createUserFollowing(2, 999, 3245);
        when(repository.getUserById(2)).thenReturn(Optional.of(user2));
        when(repository.findUsersById(List.of(999, 3245))).thenThrow(NotFoundException.class);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            service.getUserFollowers(2, "name_desc");
        });
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
