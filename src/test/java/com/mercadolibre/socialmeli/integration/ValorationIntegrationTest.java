package com.mercadolibre.socialmeli.integration;

import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.repository.IProductRepository;
import com.mercadolibre.socialmeli.repository.ProductRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de integracion del endpoint: GET /products/{post_id}/valorations?valoration_number={number}
 * US0014.2 (BONUS) - Lista de valoraciones filtradas por numero
 */

@SpringBootTest
@AutoConfigureMockMvc
public class ValorationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IProductRepository productRepository;

    @BeforeEach
    void setupData() {
        Post post = TestFactory.createPostWithValoration(100, 1, 5);
        post.getValorations().put(2, 3);
        post.getValorations().put(3, 5);

        productRepository.getAll().clear();
        productRepository.getAll().add(post);
    }

    @Test
    void getValorationsByPost_withValorationNumberParam_shouldReturnOnlyMatches() throws Exception {
        mockMvc.perform(get("/products/100/valorations")
                        .param("valoration_number", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].valoration").value(5))
                .andExpect(jsonPath("$[1].valoration").value(5));
    }

    @Test
    void getValorationsByPost_withNoMatches_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/products/100/valorations")
                        .param("valoration_number", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }



    @Test
    void postValoration_withValidData_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(post("/products/valoration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user_id\":1, \"post_id\":100, \"valoration\":4}"))
                .andExpect(status().isOk());
    }



    @Test
    void postValoration_withInvalidValoration_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/products/valoration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"post_id\":100, \"user_id\":4, \"valoration\":6}"))
                .andExpect(status().isBadRequest());
    }


}