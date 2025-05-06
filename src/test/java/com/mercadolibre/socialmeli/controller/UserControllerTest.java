package com.mercadolibre.socialmeli.controller;

import com.mercadolibre.socialmeli.dto.FollowedDto;
import com.mercadolibre.socialmeli.dto.FollowerCountDto;
import com.mercadolibre.socialmeli.dto.FollowersDto;
import com.mercadolibre.socialmeli.dto.UserDto;
import com.mercadolibre.socialmeli.exception.BadRequestException;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.service.IUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;



import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    IUserService service;

    @InjectMocks
    UserController controller;



    @DisplayName("Test de Controller: la lista de seguidos con orden ascendente debería existir y no estar vacía.")
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

    @DisplayName("Test de Controller: la lista de seguidos con orden descendente debería existir y no estar vacía.")
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

    @DisplayName("Test de Controller: la lista de seguidores con orden ascendente debería existir y no estar vacía.")
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

    @DisplayName("Test de Controller: la lista de seguidores con orden descendente debería existir y no estar vacía.")
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

    @DisplayName("Test de Controller: el ID del usuario seguido no existe.")
    @Test
    void getFollowed_withInexistentId_shouldReturnException() {
        // Arrange
        when(service.searchFollowedSellers(999, "name_desc")).thenThrow(NotFoundException.class);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            controller.getFollowed(999, "name_desc");
        });
    }

    @DisplayName("Test de Controller: el ID del usuario seguidor no existe.")
    @Test
    void getFollowers_withInexistentId_shouldReturnException() {
        // Arrange
        when(service.getUserFollowers(999, "name_desc")).thenThrow(NotFoundException.class);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            controller.getFollowers(999, "name_desc");
        });
    }

    @DisplayName("Test de Controller: operación correcta.")
    @Test
    public void unfollowUser_shouldReturnOkWhenUnfollowSuccess() {
        // Act
        ResponseEntity<Void> response = controller.unfollowUser(1, 2);

        // Assert
        Mockito.verify(service).unfollowUser(1, 2);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @DisplayName("Test de Controller: el usuario al dejar de seguir, no existe.")
    @Test
    public void unfollowUser_shouldThrowBadRequest() {
        // Arrange
        Mockito.doThrow(new BadRequestException("Usuario no encontrado"))
                .when(service).unfollowUser(1, 2);

        // Act & Assert
        Assertions.assertThrows(BadRequestException.class, () -> {
            controller.unfollowUser(1, 2);
        });

        Mockito.verify(service).unfollowUser(1, 2);
    }

    @DisplayName("Test de Controller: Verifica que se cumpla el happypath.")
    @Test
    public void getFollowersCountByUserId_shouldReturnOk(){
        // Arrange
        Integer userId = 1;
        FollowerCountDto dto = new FollowerCountDto(userId, "Ana", 3);
        when(service.getFollowersCountByUserId(userId)).thenReturn(dto);

        // Act
        ResponseEntity<FollowerCountDto> response = controller.getFollowersCountByUserId(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().getUser_id());
        assertEquals("Ana", response.getBody().getUser_name());
        assertEquals(3, response.getBody().getFollowers_count());
    }

    @DisplayName("Test de Controller: Verifica que se cumpla el sadpath.")
    @Test
    public void getFollowersCountByUserId_shouldReturnErrorNotFound(){
        // Arrange
        Integer userId = 999;
        when(service.getFollowersCountByUserId(userId)).thenThrow(new NotFoundException("Usuario no encontrado."));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            controller.getFollowersCountByUserId(userId);
        });
    }

    @DisplayName("Test de Controller: Test de Controller: seguir usuario con éxito.")
    @Test
    void followUser_shouldReturnOkWhenFollowSuccess() throws Exception {

        // Act
        ResponseEntity<Void> response = controller.followUser(1, 2);

        // Assert
        Mockito.verify(service).followUser(1, 2);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }


    @DisplayName("Test de Controller: el usuario a seguir no existe.")
    @Test
    void followUser_shouldReturnBadRequestWhenUserToFollowDoesNotExist() throws Exception {
        // Arrange
        Mockito.doThrow(new BadRequestException("Usuario no encontrado"))
                .when(service).followUser(1, 2);

        // Act & Assert
        Assertions.assertThrows(BadRequestException.class, () -> {
            controller.followUser(1, 2);
        });

        Mockito.verify(service).followUser(1, 2);
    }

    @DisplayName("Test de controller: la lista de seguidos debería estar con orden ascendente.")
    @Test
    void getFollowed_withOrderAsc_shouldHaveOrderAsc() {
        // Arrange
        UserDto user1 = TestFactory.createUserDTO(1);
        user1.setUser_name("John");
        UserDto user3 = TestFactory.createUserDTO(3);
        user3.setUser_name("Jane");
        UserDto user2 = TestFactory.createUserDTO(2);

        FollowedDto followedDto = new FollowedDto(user2.getUser_id(), user2.getUser_name(),  List.of(user3, user1));

        when(service.searchFollowedSellers(2, "name_asc")).thenReturn(followedDto);

        // Act
        ResponseEntity<FollowedDto> response = controller.getFollowed(2, "name_asc");
        FollowedDto body = response.getBody();

        // Assert
        assertNotNull(body);
        assertEquals(user3.getUser_name(), body.getFollowed().get(0).getUser_name());
        assertEquals(user1.getUser_name(), body.getFollowed().get(1).getUser_name());
    }

    @DisplayName("Test de controller: la lista de seguidos deberia estar con orden descendente.")
    @Test
    void getFollowed_withOrderDesc_shouldHaveOrderDesc() {
        // Arrange
        UserDto user1 = TestFactory.createUserDTO(1);
        user1.setUser_name("John");
        UserDto user3 = TestFactory.createUserDTO(3);
        user3.setUser_name("Jane");
        UserDto user2 = TestFactory.createUserDTO(2);

        FollowedDto followedDto = new FollowedDto(user2.getUser_id(), user2.getUser_name(),  List.of(user1, user3));

        when(service.searchFollowedSellers(2, "name_desc")).thenReturn(followedDto);

        // Act
        ResponseEntity<FollowedDto> response = controller.getFollowed(2, "name_desc");
        FollowedDto body = response.getBody();

        // Assert
        assertNotNull(body);
        assertEquals(user1.getUser_name(), body.getFollowed().get(0).getUser_name());
        assertEquals(user3.getUser_name(), body.getFollowed().get(1).getUser_name());
    }

    @DisplayName("Test de controller: la lista de seguidores debería estar con orden ascendente.")
    @Test
    void getFollowers_withOrderAsc_shouldHaveOderderAsc() {
        // Arrange
        UserDto user1 = TestFactory.createUserDTO(1);

        UserDto user3 = TestFactory.createUserDTO(3);
        user3.setUser_name("John");
        UserDto user2 = TestFactory.createUserDTO(2);
        user2.setUser_name("Jane");
        FollowersDto followersDto = new FollowersDto(user1.getUser_id(), user1.getUser_name(),  List.of(user2, user3));

        when(service.getUserFollowers(2, "name_asc")).thenReturn(followersDto);

        // Act
        ResponseEntity<FollowersDto> response = controller.getFollowers(2, "name_asc");
        FollowersDto body = response.getBody();

        // Assert
        assertNotNull(body);
        assertEquals(user2.getUser_name(), body.getFollowers().get(0).getUser_name());
        assertEquals(user3.getUser_name(), body.getFollowers().get(1).getUser_name());
    }

    @DisplayName("Test de controller: la lista de seguidores debería estar con orden descendente.")
    @Test
    void getFollowers_withOrderDesc_shouldHaveOrderDesc() {
        // Arrange
        UserDto user1 = TestFactory.createUserDTO(1);

        UserDto user3 = TestFactory.createUserDTO(3);
        user3.setUser_name("John");
        UserDto user2 = TestFactory.createUserDTO(2);
        user2.setUser_name("Jane");
        FollowersDto followersDto = new FollowersDto(user1.getUser_id(), user1.getUser_name(),  List.of(user3, user2));

        when(service.getUserFollowers(2, "name_desc")).thenReturn(followersDto);

        // Act
        ResponseEntity<FollowersDto> response = controller.getFollowers(2, "name_desc");
        FollowersDto body = response.getBody();

        // Assert
        assertNotNull(body);
        assertEquals(user3.getUser_name(), body.getFollowers().get(0).getUser_name());
        assertEquals(user2.getUser_name(), body.getFollowers().get(1).getUser_name());
    }
}
