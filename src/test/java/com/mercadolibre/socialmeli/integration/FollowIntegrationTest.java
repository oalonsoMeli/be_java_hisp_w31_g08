package com.mercadolibre.socialmeli.integration;

import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integracion del endpoint /users/{userId}/follow/{userIdToFollow}
 * T-0001 (US-0001) - Verifica que el flujo real de que el usuario a seguir exista.
 **/

@SpringBootTest
@AutoConfigureMockMvc
public class FollowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserRepository userRepository;

    @BeforeEach
    void setupData() {
        User user1 = TestFactory.createUserWithFollow(1, 2);
        User user2 = TestFactory.createUser(2);

        userRepository.getAll().clear();
        userRepository.getAll().add(user1);
        userRepository.getAll().add(user2);
    }

    // Caso de éxito: El usuario 1 sigue al usuario 2
    @Test
    void followUser_shouldReturn200() throws Exception {
        mockMvc.perform(post("/users/1/follow/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Caso de error: Intentar seguir a un usuario que no existe
    @Test
    void followUser_shouldReturn400WhenUserToFollowDoesNotExist() throws Exception {
        mockMvc.perform(post("/users/1/follow/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
