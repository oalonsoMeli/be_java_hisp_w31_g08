package com.mercadolibre.socialmeli.controller;

import com.mercadolibre.socialmeli.dto.FollowedDto;
import com.mercadolibre.socialmeli.dto.FollowersDto;
import com.mercadolibre.socialmeli.dto.UserDto;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import com.mercadolibre.socialmeli.service.IUserService;
import com.mercadolibre.socialmeli.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    IUserService service;

    @InjectMocks
    UserController controller;

    // al probar el llamado de la lista del orden ascendente, el body no debería estar nulo, la lista debería contener
    // los seguidos de un usuario y además, no estar vacía
    @Test
    void getFollowed_withOrderAsc_shouldExists() {
        // Arrange
        UserDto user1 = TestFactory.createUserDTO(1);

        UserDto user3 = TestFactory.createUserDTO(3);

        UserDto user2 = TestFactory.createUserDTO(2);

        FollowedDto followedDto = new FollowedDto(user2.getUser_id(), user2.getUser_name(),  List.of(user1, user3));

        when(service.searchFollowedSellers(2, "name_asc")).thenReturn(followedDto);

        // Act
        ResponseEntity<FollowedDto> response = controller.getFollowed(2, "name_asc");
        FollowedDto body = response.getBody();

        // Assert
        assertNotNull(body);
        assertEquals(2, body.getFollowed().size());
        assertFalse(body.getFollowed().isEmpty());
    }

    // al probar el llamado de la lista del orden descendente, el body no debería estar nulo, la lista debería contener
    // los seguidos de un usuario y además, no estar vacía
    @Test
    void getFollowed_withOrderDesc_shouldExists() {
        // Arrange
        UserDto user1 = TestFactory.createUserDTO(1);

        UserDto user3 = TestFactory.createUserDTO(3);

        UserDto user2 = TestFactory.createUserDTO(2);

        FollowedDto followedDto = new FollowedDto(user2.getUser_id(), user2.getUser_name(),  List.of(user1, user3));

        when(service.searchFollowedSellers(2, "name_desc")).thenReturn(followedDto);

        // Act
        ResponseEntity<FollowedDto> response = controller.getFollowed(2, "name_desc");
        FollowedDto body = response.getBody();

        // Assert
        assertNotNull(body);
        assertEquals(2, body.getFollowed().size());
        assertFalse(body.getFollowed().isEmpty());
    }
    // al probar el llamado de la lista del orden ascendente, el body no debería estar nulo, la lista debería contener
    // los seguidores de un vendedor y además, no estar vacía
    @Test
    void getFollowers_withOrderAsc_shouldExists() {
        // Arrange
        UserDto user1 = TestFactory.createUserDTO(1);

        UserDto user3 = TestFactory.createUserDTO(3);

        UserDto user2 = TestFactory.createUserDTO(2);

        FollowersDto followersDto = new FollowersDto(user1.getUser_id(), user1.getUser_name(),  List.of(user2, user3));

        when(service.getUserFollowers(2, "name_asc")).thenReturn(followersDto);

        // Act
        ResponseEntity<FollowersDto> response = controller.getFollowers(2, "name_asc");
        FollowersDto body = response.getBody();

        // Assert
        assertNotNull(body);
        assertEquals(2, body.getFollowers().size());
        assertFalse(body.getFollowers().isEmpty());
    }

    // al probar el llamado de la lista del orden ascendente, el body no debería estar nulo, la lista debería contener
    // los seguidores de un vendedor y además, no estar vacía
    @Test
    void getFollowers_withOrderDesc_shouldExists() {
        // Arrange
        UserDto user1 = TestFactory.createUserDTO(1);

        UserDto user3 = TestFactory.createUserDTO(3);

        UserDto user2 = TestFactory.createUserDTO(2);

        FollowersDto followersDto = new FollowersDto(user1.getUser_id(), user1.getUser_name(),  List.of(user2, user3));

        when(service.getUserFollowers(2, "name_desc")).thenReturn(followersDto);

        // Act
        ResponseEntity<FollowersDto> response = controller.getFollowers(2, "name_desc");
        FollowersDto body = response.getBody();

        // Assert
        assertNotNull(body);
        assertEquals(2, body.getFollowers().size());
        assertFalse(body.getFollowers().isEmpty());
    }

    @Test
    void getFollowed_withInexistentId_shouldReturnException() {
        // Arrange
        when(service.searchFollowedSellers(999, "name_desc")).thenThrow(NotFoundException.class);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            controller.getFollowed(999, "name_desc");
        });
    }


    @Test
    void getFollowers_withInexistentId_shouldReturnException() {
        // Arrange
        when(service.getUserFollowers(999, "name_desc")).thenThrow(NotFoundException.class);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            controller.getFollowers(999, "name_desc");
        });
    }

}