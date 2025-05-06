package com.mercadolibre.socialmeli.integration;

import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserRepository userRepository;

    @BeforeEach
    void setupData() {
        User user1 = TestFactory.createUserWithFollow(1, 2);
        user1.setUserName("Raul");
        User user2 = TestFactory.createUserWithFollow(2, 1);
        user2.setUserName("Analia");
        User user3 = TestFactory.createUserFollowing(3, 2, 1);
        user3.setUserName("Mariela");
        User user4 = TestFactory.createUserFollowing(4, 3, 2);
        user4.setUserName("Lautaro");
        userRepository.getAll().clear();
        userRepository.getAll().add(user1);
        userRepository.getAll().add(user2);
        userRepository.getAll().add(user3);
        userRepository.getAll().add(user4);
    }

    @DisplayName("Test de integración del endpoint /products/users/{userId}/followers/list" +
                 "US-0008 - Verifica que el flujo funcione correctamente con orden ascendente.")
    @Test
    public void testGetFollowersUsers_withParamAsc_shouldOrderAsc() throws Exception {
        mockMvc.perform(get("/users/{userId}/followers/list", 3)
                        .param("order", "name_asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.followers").isNotEmpty())
                .andExpect(jsonPath("$.followers[0].user_name").value("Analia"))
                .andExpect(jsonPath("$.followers[1].user_name").value("Raul"));
    }

    @DisplayName("Test de integración del endpoint /products/users/{userId}/followers/list" +
                 "US-0008 - Verifica que el flujo funcione correctamente con orden descendente.")
    @Test
    public void testGetFollowersUsers_withParamDesc_shouldOrderDesc() throws Exception {
        mockMvc.perform(get("/users/{userId}/followers/list", 3)
                        .param("order", "name_desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.followers").isNotEmpty())
                .andExpect(jsonPath("$.followers[0].user_name").value("Raul"))
                .andExpect(jsonPath("$.followers[1].user_name").value("Analia"));
    }

    @DisplayName("Test de integración del endpoint /products/users/{userId}/followed/list" +
                 "US-0008 - Verifica que el flujo funcione correctamente con orden ascendente.")
    @Test
    public void testGetFollowedSellers_withParamAsc_shouldOrderAsc() throws Exception {
        mockMvc.perform(get("/users/{userId}/followed/list", 4)
                        .param("order", "name_asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.followed").isNotEmpty())
                .andExpect(jsonPath("$.followed[0].user_name").value("Analia"))
                .andExpect(jsonPath("$.followed[1].user_name").value("Mariela"));
    }

    @DisplayName("Test de integración del endpoint /products/users/{userId}/followed/list" +
                 "US-0008 - Verifica que el flujo funcione correctamente con orden descendente.")
    @Test
    public void testGetFollowedSellers_withParamDesc_shouldOrderDesc() throws Exception {
        mockMvc.perform(get("/users/{userId}/followed/list", 4)
                        .param("order", "name_desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.followed").isNotEmpty())
                .andExpect(jsonPath("$.followed[0].user_name").value("Mariela"))
                .andExpect(jsonPath("$.followed[1].user_name").value("Analia"));
    }

    @DisplayName("Test de integración del endpoint /products/users/{userId}/followed/list" +
                 "US-0008 - Verifica que el flujo largue una excepción si no funciona.")
    @Test
    public void testGetFollowedSellers_withIdInexistent_shouldReturn404() throws Exception {
        mockMvc.perform(get("/users/{userId}/followed/list", 4345)
                        .param("order", "name_desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @DisplayName("Test de integración del endpoint /products/users/{userId}/followers/list" +
                 "US-0008 - Verifica que el flujo funcione correctamente con orden descendente.")
    @Test
    public void testGetFollowersUsers_withIdInexistent_shouldReturn404() throws Exception {
        mockMvc.perform(get("/users/{userId}/followers/list", 3453)
                        .param("order", "name_desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @DisplayName("Test de integración del endpoint /products/users/{userId}/unfollow/{userIdToUnfollow}" +
                 "US-0007 - Verifica que el flujo real de unfollow funcione correctamente.")
    @Test
    void unfollowUser_shouldReturn200() throws Exception {
        mockMvc.perform(put("/users/1/unfollow/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Test de integración del endpoint /products/users/{userId}/unfollow/{userIdToUnfollow}" +
                 "US-0007 - Verifica que el flujo real de unfollow funcione correctamente.")
    @Test
    void unfollowUser_shouldReturn400WhenUserToUnfollowDoesNotExist() throws Exception {
        mockMvc.perform(put("/users/1/unfollow/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Test de integración del endpoint  /users/{userId}/followers/count" +
                 "US-0002 - Verifica la cantidad de seguidores de un determinado usuario.")
    @Test
    void getFollowersCountByUserId_shouldReturn200() throws Exception {
        mockMvc.perform(get("/users/3/followers/count")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(3))
                .andExpect(jsonPath("$.user_name").exists())
                .andExpect(jsonPath("$.followers_count").value(1));
    }

    @DisplayName("Test de integración del endpoint  /users/{userId}/followers/count" +
                 "US-0002 - Lanza la excepción 404 debido a que el id que le pasamos no existe.")
    @Test
    void getFollowersCountByUserId_shouldReturn404() throws Exception {
        mockMvc.perform(get("/users/999/followers/count")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Caso de éxito: El usuario 1 sigue al usuario 2.")
    @Test
    void followUser_shouldReturn200() throws Exception {
        mockMvc.perform(post("/users/1/follow/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Caso de error: Intentar seguir a un usuario que no existe.")
    @Test
    void followUser_shouldReturn400WhenUserToFollowDoesNotExist() throws Exception {
        mockMvc.perform(post("/users/1/follow/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
