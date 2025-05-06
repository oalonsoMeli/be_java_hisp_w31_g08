package com.mercadolibre.socialmeli.integration;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.dto.PostsDto;
import com.mercadolibre.socialmeli.dto.ProductDto;
import com.mercadolibre.socialmeli.dto.ValorationDTO;
import com.mercadolibre.socialmeli.exception.BadRequestException;
import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IProductRepository;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import com.mercadolibre.socialmeli.service.ProductServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private Post post1, post2, post3, post4;
    @Autowired
    private ProductServiceImpl productServiceImpl;

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

    /*
     * Test de integración del endpoint /products/followed/{userId}/list
     * T-006 (US-0012) Verifica que las publicaciones se ordenen de forma descendente por fecha
     */
    @Test
    void getListOfPublicationsByUser_shouldSortDescOrder() throws Exception {
        PostsDto postsDto = performGetPosts(userId, "date_desc");
        assertPostsSorted(postsDto, false); // false = descendente
    }

    /*
     * Test de integración del endpoint /products/followed/{userId}/list
     * T-006 (US-0012) Verifica que las publicaciones se ordenen de forma ascendente por fecha
     */
    @Test
    void getListOfPublicationsByUser_shouldSortAscOrder() throws Exception {
        PostsDto postsDto = performGetPosts(userId, "date_asc");
        assertPostsSorted(postsDto, true); // true = ascendente
    }

    /*
     * Test de integración del endpoint /products/followed/{userId}/list
     * T-008 (US-0012) Verifica que retorne 404 cuando no hay publicaciones
     */
    @Test
    void getListOfPublicationsByUser_shouldReturn404WhenNoPosts() throws Exception {
        productRepository.getAll().clear();
        mockMvc.perform(get("/products/followed/{userId}/list", userId)
                        .param("order", "date_desc"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No hay publicaciones de quienes sigues."));
    }

    /*
     * Test de integración del endpoint /products/followed/{userId}/list
     * T-008 (US-0012) Verifica que retorne 400 cuando el usuario no existe
     */
    @Test
    void getListOfPublicationsByUser_shouldReturn400WhenUserNotFound() throws Exception {
        Integer userId = Integer.MAX_VALUE;
        mockMvc.perform(get("/products/followed/{userId}/list", userId)
                        .param("order", "date_desc"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Usuario no encontrado."));
    }

    /*
     * Test de integración del endpoint /products/followed/{userId}/list
     * T-008 (US-0012) Verifica que incluya publicaciones creadas exactamente hace dos semanas
     */
    @Test
    void getListOfPublicationsByUser_shouldIncludePostExactlyTwoWeeksAgo() throws Exception {
        Post exactTwoWeeksPost = TestFactory.createPost(5, 2, LocalDate.now().minusWeeks(2));
        productRepository.save(exactTwoWeeksPost);
        PostsDto postsDto = performGetPosts(userId, "date_desc");
        assertNotNull(postsDto);
        assertTrue(postsDto.getPosts().stream()
                        .anyMatch(post -> post.getDate().isEqual(LocalDate.now().minusWeeks(2))),
                "The post created exactly two weeks ago should be included.");
    }

    /*
     * Test de integración del endpoint /products/{post_id}/valorations/average
     * T-0016 (US-0016) Verifica que devuelva el promedio esperado
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

    /*
     * Test de integración del endpoint /products/{user_id}/user/valorations
     * T-0015 (US-0015) Verifica que devuelva las valoraciones del usuario
     */
    @Test
    void getAllValorationsByUser_ShouldReturnOnlyMatchingValorations() throws Exception {
        mockMvc.perform(get("/products/{user_id}/user/valorations", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].user_id").value(userId))
                .andExpect(jsonPath("$[0].post_id").value(post4.getPostId()))
                .andExpect(jsonPath("$[0].valoration").value(5));
    }

    /*
     * Test de integración del endpoint /products/{user_id}/user/valorations
     * T-0015 (US-0015) Verifica que devuelva una lista vacía si el usuario no ha valorado post
     */
    @Test
    void getAllValorationsByUser_ShouldReturnEmptyValorations() throws Exception {
        Integer userWithNotValorations = 3;
        mockMvc.perform(get("/products/{user_id}/user/valorations", userWithNotValorations))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @DisplayName("Test de integración del endpoint /products/{user_id}/user/valorations" +
                 "US-0015 Verifica que devuelva excepción si el id no existe.")
    @Test
    void getAllValorationsByUser_shoulReturnNotFound() throws Exception {
        mockMvc.perform(get("/products/{user_id}/user/valorations", Integer.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @DisplayName("Test de integración del endpoint /products/promo-post/list " +
                 "US-0012 - Obtener un listado de todos los productos en promoción de un determinado vendedor")
    @Test
    public void getPromotionalProductsFromSellersIntegration() throws Exception {
        mockMvc.perform(get("/products/promo-post/list", 1)
                        .param("user_id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Test de integración del endpoint /products/promo-post/list " +
                 "US-0012 - Obtener un listado de todos los productos en promoción de un determinado vendedor" +
                 "Usuario no encontrado.")
    @Test
    public void getPromotionalProductsFromSellers_UserNotFound() throws Exception {
        mockMvc.perform(get("/products/promo-post/list", 1)
                        .param("user_id", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Usuario no encontrado."));
    }

    @DisplayName("Test de integración del endpoint /products/promo-post/count" +
            " US 0011 - Se debe obtener la cantidad de productos con promo")
    @Test
    void getQuantityOfProduct_shouldReturnCountOfPromoPost() throws Exception {
        post1.setHasPromo(true);
        post2.setHasPromo(true);
        mockMvc.perform(get("/products/promo-post/count")
                        .param("user_id", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.promo_products_count").value(2));
    }

    @DisplayName("Test de integración del endpoint /products/promo-post/count" +
            " US 0011 - caso borde: obtener excepción con usuario no existente.")
    @Test
    void getQuantityOfProduct_withFalseUserId_shouldReturn400BadRequest() throws Exception {
        mockMvc.perform(get("/products/promo-post/count")
                        .param("user_id", "453")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @DisplayName("Test de integración del endpoint /products/promo-post/count" +
            " US 0011 - caso border: usuario sin productos en promo, deberia devolver 0.")
    @Test
    void getQuantityOfProduct_withoutPromo_shouldContainCountCero() throws Exception {
        mockMvc.perform(get("/products/promo-post/count")
                        .param("user_id", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.promo_products_count").value(0));
    }

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
                .andExpect(jsonPath("$.message").value("La id no puede estar vacía"));
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
                .andExpect(jsonPath("$.message").value("El campo no puede poseer caracteres especiales"));
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
                .andExpect(jsonPath("$.message").value("El campo no puede poseer caracteres especiales"));
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
                .andExpect(jsonPath("$.message").value("El campo no puede poseer caracteres especiales"));
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
                .andExpect(jsonPath("$.message").value("El producto no puede tener un precio negativo"));
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
                .andExpect(jsonPath("$.message").value("El id debe ser mayor a cero"));
    }

    @DisplayName("Test de integración del endpoint /products/100/valorations")
    @Test
    void getValorationsByPost_withValorationNumberParam_shouldReturnOnlyMatches() throws Exception {
        mockMvc.perform(get("/products/4/valorations")
                        .param("valoration_number", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].valoration").value(5))
                .andExpect(jsonPath("$[0].user_id").value(1));
    }

    @DisplayName("Test de integración del endpoint /products/100/valorations")
    @Test
    void getValorationsByPost_withNoMatches_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/products/4/valorations")
                        .param("valoration_number", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @DisplayName("Test de integración del endpoint /products/valoration")
    @Test
    void postValoration_withValidData_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(post("/products/valoration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user_id\":1, \"post_id\":1, \"valoration\":4}"))
                .andExpect(status().isOk());
    }


    @DisplayName("Test de integración del endpoint /products/valoration")
    @Test
    void postValoration_withInvalidValoration_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/products/valoration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"post_id\":100, \"user_id\":4, \"valoration\":6}"))
                .andExpect(status().isBadRequest());
    }


}