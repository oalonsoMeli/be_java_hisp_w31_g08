package com.mercadolibre.socialmeli.integration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mercadolibre.socialmeli.dto.PostsDto;
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
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private Post post1, post2, post3, post4;

    @BeforeEach
    void setUp() {
        clearRepositories();
        initializeUsers();
        initializePosts();
        System.out.println(productRepository.getAll());
    }

    private void clearRepositories() {
        userRepository.getAll().clear();
        productRepository.getAll().clear();
    }

    private void initializeUsers() {
        userId = 1;
        user = TestFactory.createUserFollowing(userId, 2, 3);
        User followedUser2 = TestFactory.createUser(2);
        User followedUser3 = TestFactory.createUser(3);
        userRepository.getAll().addAll(List.of(user, followedUser2, followedUser3));
    }

    private void initializePosts() {
        post1 = TestFactory.createPost(1, 2, LocalDate.now().minusWeeks(1));
        post2 = TestFactory.createPost(2, 2, LocalDate.now().minusDays(5));
        post3 = TestFactory.createPost(3, 3, LocalDate.now().minusDays(3));
        post4 = TestFactory.createPost(4, 1, LocalDate.now().minusWeeks(1));
        productRepository.save(post1);
        productRepository.save(post2);
        productRepository.save(post3);
        postsFollowedUsers = List.of(post1, post2, post3);
        //
        productRepository.save(post4);
        post4.getProduct().setProductName("Lavadora");
        HashMap<Integer, Integer> valorations = new HashMap<>();
        valorations.put(1, 5);
        valorations.put(2, 3);
        post4.setValorations(valorations);
    }

    private PostsDto performGetPosts(Integer userId, String order) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/products/followed/{userId}/list", userId)
                        .param("order", order))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonResponse, PostsDto.class);
    }

    private void assertPostsSorted(PostsDto postsDto, boolean ascending) {
        assertNotNull(postsDto);
        assertEquals(3, postsDto.getPosts().size());
        for (int i = 0; i < postsDto.getPosts().size() - 1; i++) {
            LocalDate currentDate = postsDto.getPosts().get(i).getDate();
            LocalDate nextDate = postsDto.getPosts().get(i + 1).getDate();
            if (ascending) {
                assertTrue(currentDate.isBefore(nextDate) || currentDate.isEqual(nextDate),
                        "Las publicaciones no están en orden ascendente: " + currentDate + " no es antes o igual que " + nextDate);
            } else {
                assertTrue(currentDate.isAfter(nextDate) || currentDate.isEqual(nextDate),
                        "Las publicaciones no están en orden descendente: " + currentDate + " no es después o igual que " + nextDate);
            }
        }
    }


    @Test
    void getListOfPublicationsByUser_shouldSortDescOrder() throws Exception {
        PostsDto postsDto = performGetPosts(userId, "date_desc");
        assertPostsSorted(postsDto, false); // false = descendente
    }

    @Test
    void getListOfPublicationsByUser_shouldSortAscOrder() throws Exception {
        PostsDto postsDto = performGetPosts(userId, "date_asc");
        assertPostsSorted(postsDto, true); // true = ascendente
    }

    @Test
    void getListOfPublicationsByUser_shouldReturn404WhenNoPosts() throws Exception {
        // No se guardan posts a propósito (para simular que no hay publicaciones)
        productRepository.getAll().clear();
        mockMvc.perform(get("/products/followed/{userId}/list", userId)
                        .param("order", "date_desc"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No hay publicaciones de quienes sigues."));
    }

    @Test
    void getListOfPublicationsByUser_shouldReturn400WhenUserNotFound() throws Exception {
        Integer userId = Integer.MAX_VALUE;
        mockMvc.perform(get("/products/followed/{userId}/list", userId)
                        .param("order", "date_desc"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Usuario no encontrado."));

    }

    @Test
    void getListOfPublicationsByUser_shouldIncludePostExactlyTwoWeeksAgo() throws Exception {
        // Arrange
        Post exactTwoWeeksPost = TestFactory.createPost(5, 2, LocalDate.now().minusWeeks(2));
        productRepository.save(exactTwoWeeksPost); // Agregamos solo el post especial
        // Act
        PostsDto postsDto = performGetPosts(userId, "date_desc");
        // Assert
        assertNotNull(postsDto);
        assertTrue(postsDto.getPosts().stream()
                        .anyMatch(post -> post.getDate().isEqual(LocalDate.now().minusWeeks(2))),
                "The post created exactly two weeks ago should be included.");
    }

    /*
     * Test de integración del endpoint /products/{post_id}/valorations/average
     * T-0016 (US-0016) Verifica que devuelva el average esperado
     */
    @Test
    void getValorationsByPost_shouldReturnTheAverageExpected() throws Exception {
        mockMvc.perform(get("/products/{post_id}/valorations/average", 4)
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
