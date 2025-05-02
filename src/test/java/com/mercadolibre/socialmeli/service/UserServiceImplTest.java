package com.mercadolibre.socialmeli.service;

import com.mercadolibre.socialmeli.dto.FollowedDto;
import com.mercadolibre.socialmeli.dto.FollowerCountDto;
import com.mercadolibre.socialmeli.dto.FollowersDto;
import com.mercadolibre.socialmeli.exception.BadRequestException;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import com.mercadolibre.socialmeli.repository.UserRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UserRepositoryImpl userRepositoryImpl;
    @Autowired
    private UserServiceImpl userServiceImpl;


    // T-0003 - US0008: Verifica que exista la lista al obtener los seguidores de un vendedor de manera ASC.
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

    // T-0003 - US0008: Verifica que exista la lista al obtener los seguidores de un vendedor de manera DESC.
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

    // T-0003 - US0008: Verifica que exista la lista al obtener los seguidos de un usuario de manera ASC.
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

    // T-0003 - US0008: Verifica que exista la lista al obtener los seguidos de un usuario de manera DESC.
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


    // T-0003 - US0008: Verifica que se lance una excepcion si se busca un usuario con id inexistente.
    @Test
    void searchFollowedSellers_withAnUserIdInexistent_shouldReturnAnException() {
        // Arrange
        when(repository.getUserById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            service.searchFollowedSellers(999, "name_desc");
        });
    }

    // T-0003 - US0008: Verifica que se lance una excepcion si se busca vendedores con id inexistentes.
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


    // T-0003 - US0008: Verifica que se lance una excepcion si se busca un vendedor con id inexistente.
    @Test
    void searchFollowersUsers_withAnUserIdInexistent_shouldReturnAnException() {
        // Arrange
        when(repository.getUserById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            service.getUserFollowers(999, "name_desc");
        });
    }

    // T-0003 - US0008: Verifica que se lance una excepcion si se busca usuarios con id inexistentes.
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
    // T-0002 - US0007: Verifica que se lance una excepcion cuando el usuario a dejar de seguir no existe.
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
    // T-0002 - US0007: Verifica que se elimine el usuario a dejar de seguir cuando existe y esta en la lista.
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
    // T002 - US0007: Caso borde - el usuario existe pero no sigue a nadie.
    // Al intentar dejar de seguir, debería lanzar excepcion por no encontrar la relación.
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
    // T002 - US0007: Caso borde - IDs invalidos que deberían provocar una excepcion.
    void unfollowUser_shouldThrowWhenUserIdIsNull() {
        assertThrows(BadRequestException.class, () -> {
            service.unfollowUser(null, 2);
        });
    }

    // T-0007 - US0002: Verificar que la cantidad de seguidores de un determinado usuario sea correcta.
    @Test
    void  getFollowersCountByUserId_shouldReturnCountFollowers(){
        // Arrange
        User followedUser = TestFactory.createUser(1);
        User follower1 = TestFactory.createUserFollowing(2, 1);
        User follower2 = TestFactory.createUserFollowing(3, 1);

        List<User> allUsers = List.of(followedUser, follower1, follower2);

        when(repository.getUserById(1)).thenReturn(Optional.of(followedUser));
        when(repository.getAll()).thenReturn(allUsers);

        //Act
        FollowerCountDto result = service.getFollowersCountByUserId(1);

        //Assert
        assertNotNull(result);
        assertEquals(1, result.getUser_id());
        assertEquals(2, result.getFollowers_count());
    }

    // T-0007 - US0002: Verifica que no tenga ningún seguidor.
    @Test
    void  getFollowersCountByUserId_shouldReturnNonFollowers() {
        // Arrange
        User followedUser = TestFactory.createUser(1);

        List<User> allUsers = List.of(followedUser);

        when(repository.getUserById(1)).thenReturn(Optional.of(followedUser));
        when(repository.getAll()).thenReturn(allUsers);

        //Act
        FollowerCountDto result = service.getFollowersCountByUserId(1);

        //Assert
        assertNotNull(result);
        assertEquals(1, result.getUser_id());
        assertEquals(0, result.getFollowers_count());
    }

    @Test
    // T-0001 - US0001 : Verifica que el usuario a seguir exista y se pueda seguir correctamente
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
    // T-0001 - US0001 Verifica que se lance excepción cuando el usuario a seguir no existe
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








