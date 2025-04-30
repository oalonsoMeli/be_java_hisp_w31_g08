package com.mercadolibre.socialmeli.integration;

import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import com.mercadolibre.socialmeli.repository.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integracion del endpoint /users/{userId}/unfollow/{userIdToUnfollow}
 * T-0002 (US-0007) - Verifica que el flujo real de unfollow funcione correctamente
 **/

@SpringBootTest
@AutoConfigureMockMvc
public class UnfollowIntegrationTest {
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

    @Test
    void unfollowUser_shouldReturn200() throws Exception {
        mockMvc.perform(put("/users/1/unfollow/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void unfollowUser_shouldReturn400WhenUserToUnfollowDoesNotExist() throws Exception {
        mockMvc.perform(put("/users/1/unfollow/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
