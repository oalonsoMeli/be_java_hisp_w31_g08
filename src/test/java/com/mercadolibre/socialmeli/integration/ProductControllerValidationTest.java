package com.mercadolibre.socialmeli.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.dto.ProductDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // <-- Aquí
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final ObjectWriter writer = objectMapper.writer().withDefaultPrettyPrinter();

    private String asJsonString(Object obj) throws Exception {
        return writer.writeValueAsString(obj);
    }

    @DisplayName("Test de integración del endpoint /products" +
                 "Verifica que retorne error si el product_id es null.")
    @Test
    void createPost_shouldReturnErrorWhenProductIdIsNull() throws Exception {
        ProductDto productDto = new ProductDto(null, "ValidName", "ValidType",
                                               "ValidBrand", "ValidColor", "ValidNotes");
        PostDto postDto = new PostDto(1, LocalDate.now(), productDto, 1, 500.0);

        this.mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La id no puede estar vacía."));
    }

    @DisplayName("Test de integración del endpoint /products" +
                 "Verifica que retorne error si el product_name contiene caracteres especiales.")
     @Test
    void createPost_shouldReturnErrorWhenProductNameHasSpecialCharacters() throws Exception {
        ProductDto productDto = new ProductDto(1, "Invalid@Name", "ValidType",
                                               "ValidBrand", "ValidColor", "ValidNotes");
        PostDto postDto = new PostDto(1, LocalDate.now(), productDto, 1, 500.0);
        System.out.println(asJsonString(postDto));
        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El campo no permite caracteres especiales."));
    }
    @DisplayName("Test de integración del endpoint /products" +
                 "Verifica que retorne error si el type supera los 15 caracteres.")
    @Test
    void createPost_shouldReturnErrorWhenTypeIsTooLong() throws Exception {
        ProductDto productDto = new ProductDto(1, "ValidName", "TypeTooLongOverFifteen",
                                               "ValidBrand", "ValidColor", "ValidNotes");
        PostDto postDto = new PostDto(1, LocalDate.now(), productDto, 1, 500.0);

        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La longitud no puede superar los 15 caracteres."));
    }

    @DisplayName("Test de integración del endpoint /products" +
                 "Verifica que retorne error si el brand contiene caracteres especiales.")
    @Test
    void createPost_shouldReturnErrorWhenBrandHasSpecialCharacters() throws Exception {
        ProductDto productDto = new ProductDto(1, "ValidName", "ValidType",
                                                "Invalid#Brand", "ValidColor", "ValidNotes");
        PostDto postDto = new PostDto(1, LocalDate.now(), productDto, 1, 500.0);

        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El campo no permite caracteres especiales"));
    }

    @DisplayName("Test de integración del endpoint /products" +
                 "Verifica que retorne error si el notes supera los 80 caracteres.")
    @Test
    void createPost_shouldReturnErrorWhenNotesAreTooLong() throws Exception {
        String longNotes = "Este texto es muy largo, supera los ochenta caracteres permitidos para el campo notes.";
        ProductDto productDto = new ProductDto(1, "ValidName", "ValidType",
                                               "ValidBrand", "ValidColor", longNotes);
        PostDto postDto = new PostDto(1, LocalDate.now(), productDto, 1, 500.0);

        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La longitud no puede superar los 80 caracteres."));
    }

    @DisplayName("Test de integración del endpoint /products" +
                 "Verifica que cree exitosamente un post válido.")
    @Test
    void createPost_shouldCreateSuccessfullyWithValidData() throws Exception {
        ProductDto productDto = new ProductDto(1, "ValidName", "ValidType",
                                               "ValidBrand", "ValidColor", "ValidNotes");
        PostDto postDto = new PostDto(1, LocalDate.now(), productDto, 1, 500.0);

        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDto)))
                .andExpect(status().isOk());
    }

    @DisplayName("Test de integración del endpoint /products" +
                 "Verifica que retorne error si el price es nulo.")
    @Test
    void createPost_shouldReturnErrorWhenPriceIsNull() throws Exception {
        ProductDto productDto = new ProductDto(1, "ValidName", "ValidType",
                                               "ValidBrand", "ValidColor", "ValidNotes");
        PostDto postDto = new PostDto(1, LocalDate.now(), productDto, 1, null);

        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El campo no puede estar vacío."));
    }

    @DisplayName("Test de integración del endpoint /products" +
            "Verifica que retorne error si el color contiene caracteres especiales.")
    @Test
    void createPost_shouldReturnErrorWhenColorHasSpecialCharacters() throws Exception {
        ProductDto productDto = new ProductDto(1, "ValidName", "ValidType",
                                               "ValidBrand", "Invalid@Color", "ValidNotes");
        PostDto postDto = new PostDto(1, LocalDate.now(), productDto, 1, 500.0);

        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El campo no permite caracteres especiales"));
    }

    @DisplayName("Test de integración del endpoint /products" +
                 "Verifica que retorne error si el precio es negativo.")
    @Test
    void createPost_shouldReturnErrorWhenPriceIsNegative() throws Exception {
        ProductDto productDto = new ProductDto(1, "ValidName", "ValidType",
                                                "ValidBrand", "ValidColor", "ValidNotes");
        PostDto postDto = new PostDto(1, LocalDate.now(), productDto, 1, -10.0);

        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El producto no permite un precio negativo."));
    }

    @DisplayName("Test de integración del endpoint /products" +
                 "Verifica que retorne error si el price supera el máximo permitido.")
    @Test
    void createPost_shouldReturnErrorWhenPriceExceedsMaximum() throws Exception {
        ProductDto productDto = new ProductDto(1, "ValidName", "ValidType",
                                               "ValidBrand", "ValidColor", "ValidNotes");
        PostDto postDto = new PostDto(1, LocalDate.now(), productDto, 1, 10000001.0);

        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El precio máximo por producto es de 10.000.000"));
    }

    @DisplayName("Test de integración del endpoint /products" +
                 "Verifica que retorne error si el product_id es negativo.")
    @Test
    void createPost_shouldReturnErrorWhenProductIdIsNegative() throws Exception {
        ProductDto productDto = new ProductDto(-1, "ValidName", "ValidType",
                                               "ValidBrand", "ValidColor", "ValidNotes");
        PostDto postDto = new PostDto(1, LocalDate.now(), productDto, 1, 500.0);

        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El id debe ser mayor a cero."));
    }
}

