package com.mercadolibre.socialmeli.integration;

import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IProductRepository;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IProductRepository productRepository;

    private Integer userId;
    private User user;
    private List<Post> postsFollowedUsers;

    @BeforeEach
    void setUp() {
        userRepository.getAll().clear();
        productRepository.getAll().clear();
        userId = 1;
        user = TestFactory.createUserFollowing(userId, 2, 3);

        User followedUser2 = TestFactory.createUser(2);
        User followedUser3 = TestFactory.createUser(3);

        userRepository.getAll().add(user);
        userRepository.getAll().add(followedUser2);
        userRepository.getAll().add(followedUser3);

        Post post2 = TestFactory.createPost(1, 1, LocalDate.now().minusWeeks(1));
        productRepository.save(post2);
        post2.getProduct().setProductName("Lavadora");
        HashMap<Integer, Integer> valorations = new HashMap<>();
        valorations.put(1, 5);
        valorations.put(2, 3);
        post2.setValorations(valorations);

        postsFollowedUsers = TestFactory.createPostsForFollowedUsers(2, 3);
        for (Post post : postsFollowedUsers) {
            productRepository.save(post);

        }
    }

    /*
     * Test de integración del endpoint /products/{post_id}/valorations/average
     * T-0016 (US-0016) Verifica que devuelva el average esperado
     */
    @Test
    void getValorationsByPost_shouldReturnTheAverageExpected() throws Exception {
        mockMvc.perform(get("/products/{post_id}/valorations/average", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.average").value(4.0));
    }

    /*
     * Test de integración del endpoint /products/{post_id}/valorations/average
     * T-0016 (US-0016) Verifica que devuelva excepción si el id no existe
     */
    @Test
    void getValorationsByPost_withIdInexistent_shoulReturnNotFound() throws Exception {
        mockMvc.perform(get("/products/{post_id}/valorations/average", 32141)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
