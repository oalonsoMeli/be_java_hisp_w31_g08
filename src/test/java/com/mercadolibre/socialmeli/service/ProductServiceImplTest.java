package com.mercadolibre.socialmeli.service;
import com.mercadolibre.socialmeli.dto.*;

import com.mercadolibre.socialmeli.exception.BadRequestException;
import com.mercadolibre.socialmeli.exception.ExceptionController;
import com.mercadolibre.socialmeli.exception.IllegalArgumentException;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.dto.ValorationDTO;
import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IProductRepository;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import com.mercadolibre.socialmeli.utilities.OrderType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private static final Integer DEFAULT_USER_ID = 1;

    private User defaultUser;
    private List<Post> defaultPostsFollowedUsers;
    @Autowired
    private ExceptionController exceptionController;

    @BeforeEach
    void setUp() {
        defaultUser = TestFactory.createUserFollowing(DEFAULT_USER_ID, 2, 3);
        defaultPostsFollowedUsers = TestFactory.createPostsForFollowedUsers(2, 3);
    }

    private void mockUserAndPosts(String orderType) {
        when(userRepository.getUserById(DEFAULT_USER_ID)).thenReturn(Optional.of(defaultUser));
        when(productRepository.getPostsByUserIdsInLastTwoWeeks(anySet(), eq(orderType)))
                .thenReturn(defaultPostsFollowedUsers);
    }

    @DisplayName("US-0009 - Verificar que el tipo de ordenamiento por fecha exista de forma ascendente.")
    @Test
    void getListOfPublicationsByUser_verifyDateSortTypeExistsAsc() {
        //Arrange
        User user = TestFactory.createUser(1);
        List<Post> postList = TestFactory.createPostList(5, 1);
        when(this.productRepository.getPostsByUserIdsInLastTwoWeeks(anySet(), anyString())).thenReturn(postList);
        when(this.userRepository.getUserById(anyInt())).thenReturn(Optional.of(user));

        //Act
        PostsDto postsDto = this.productService.getListOfPublicationsByUser(user.getUserId(),
                OrderType.ORDER_DATE_ASC.getValue());

        //Assert
        Assertions.assertEquals(5, postsDto.getPosts().size());
    }

    @DisplayName("US-0009 - Verificar que el tipo de ordenamiento por fecha exista de forma descendente.")
    @Test
    void getListOfPublicationsByUser_verifyDateSortTypeExistsDesc() {
        //Arrange
        User user = TestFactory.createUser(1);
        List<Post> postList = TestFactory.createPostList(5, 1);
        when(this.productRepository.getPostsByUserIdsInLastTwoWeeks(anySet(), anyString())).thenReturn(postList);
        when(this.userRepository.getUserById(anyInt())).thenReturn(Optional.of(user));

        //Act
        PostsDto postsDto = this.productService.getListOfPublicationsByUser(user.getUserId(),
                OrderType.ORDER_DATE_DESC.getValue());

        //Assert
        Assertions.assertEquals(5, postsDto.getPosts().size());
    }

    @DisplayName("US-0009 - Verificar que el tipo de ordenamiento por fecha exista no existe.")
    @Test
    void getListOfPublicationsByUser_verifyDateSortTypeNoExist() {
        //Arrange
        User user = TestFactory.createUser(1);
        when(this.userRepository.getUserById(anyInt())).thenReturn(Optional.of(user));

        //Act y Assert
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                this.productService.getListOfPublicationsByUser(user.getUserId(), "otro"));
    }

    @DisplayName("US-0009 - Verificar que el tipo de ordenamiento por fecha exista. Lista de posts vacías.")
    @Test
    void getListOfPublicationsByUser_verifyDateSortTypeExistPostsEmpty() {
        //Arrange
        User user = TestFactory.createUser(1);
        List<Post> postList = new ArrayList<>();
        when(this.productRepository.getPostsByUserIdsInLastTwoWeeks(anySet(), anyString())).thenReturn(postList);
        when(this.userRepository.getUserById(anyInt())).thenReturn(Optional.of(user));

        //Act y Assert
        Assertions.assertThrows(NotFoundException.class, () ->
                this.productService.getListOfPublicationsByUser(user.getUserId(),
                OrderType.ORDER_DATE_DESC.getValue()));
    }

    @DisplayName("US-006 - Ordenamiento por fecha Desc Order.")
    @Test
    void getListOfPublicationsByUser_shouldSortDescOrder() {
        // Arrange
        mockUserAndPosts(OrderType.ORDER_DATE_DESC.getValue());

        // Act
        PostsDto result = productService.getListOfPublicationsByUser(DEFAULT_USER_ID, OrderType.ORDER_DATE_DESC.getValue());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getPosts().size());
        assertTrue(result.getPosts().get(0).getDate().isAfter(result.getPosts().get(1).getDate())
                || result.getPosts().get(0).getDate().isEqual(result.getPosts().get(1).getDate()));
    }

    @DisplayName("US-006 - Ordenamiento por fecha Asc Order.")
    @Test
    void getListOfPublicationsByUser_shouldSortAscOrder() {
        // Arrange
        mockUserAndPosts(OrderType.ORDER_DATE_ASC.getValue());

        // Act
        PostsDto result = productService.getListOfPublicationsByUser(DEFAULT_USER_ID, OrderType.ORDER_DATE_ASC.getValue());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getPosts().size());
        assertTrue(result.getPosts().get(0).getDate().isBefore(result.getPosts().get(1).getDate())
                || result.getPosts().get(0).getDate().isEqual(result.getPosts().get(1).getDate()));
    }


    @DisplayName("US-0016 - Verifica que calcule el promedio de las valoraciones de un post.")
    @Test
    void getValorationsAverageByPost_shouldCalculateTheAverage() {
        // Arrange
        Integer postId = 1;
        Post post = TestFactory.createPost(postId, 1, LocalDate.now().minusWeeks(1));
        HashMap<Integer, Integer> valorations = new HashMap<>();
        valorations.put(1, 5);
        valorations.put(2, 3);
        post.setValorations(valorations);
        when(productRepository.getPostsByPostId(1)).thenReturn(Optional.of(post));
        Double averageExpected = 4.0;

        // Act
        ValorationAverageDto averageObtained = productService.getValorationsAverageByPost(1);

        // Assert
        assertEquals(averageExpected, averageObtained.getAverage());
    }

    @DisplayName("US-0016 - Verifica que largue una excepción si el id del post buscado no existe.")
    @Test
    void getValorationsAverageByPost_withIdInexistent_shouldReturnException() {
        when(productRepository.getPostsByPostId(9999)).thenReturn(Optional.empty());
        assertThrows(BadRequestException.class, () -> productService.getValorationsAverageByPost(9999));
    }

    @DisplayName("US-0008 - Excepción no hay publicaciones de quienes siguen.")
    @Test
    void getListOfPublicationsByUser_shouldThrowNotFoundWhenNoPosts() {
        // Arrange
        when(userRepository.getUserById(DEFAULT_USER_ID)).thenReturn(Optional.of(defaultUser));
        when(productRepository.getPostsByUserIdsInLastTwoWeeks(anySet(), anyString()))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(NotFoundException.class, () ->
                productService.getListOfPublicationsByUser(DEFAULT_USER_ID, OrderType.ORDER_DATE_DESC.getValue())
        );
    }
    @DisplayName("US-0008 - Lanza excepción si el usuario no existe.")
    @Test
    void getListOfPublicationsByUser_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.getUserById(DEFAULT_USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadRequestException.class, () ->
                productService.getListOfPublicationsByUser(DEFAULT_USER_ID, OrderType.ORDER_DATE_DESC.getValue())
        );
    }

    @DisplayName("US-0014.2 - Devuelve solo las valoraciones que coinciden con el número filtrado.")
    @Test
    void getValorationsByPost_shouldReturnOnlyMatchingValorations() {
        // Arrange
        Post post = TestFactory.createPostWithValoration(10, 1, 5);
        post.getValorations().put(2, 3);
        post.getValorations().put(3, 5);

        when(productRepository.getPostsByPostId(10)).thenReturn(Optional.of(post));

        // Act
        List<ValorationDTO> result = productService.getValorationsByPost(10, 5);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(v -> v.getValoration() == 5));
    }

    @DisplayName("US-0014.2 -  Si ninguna valoración coincide con el filtro, devuelve lista vacía.")
    @Test
        // US0014.2 - Devuelve solo las valoraciones que coinciden con el número filtrado
    void getValorationsByPost_shouldReturnOnlyMatchingValorationsWithValorationNull() {
        // Arrange
        Post post = TestFactory.createPostWithValoration(10, 1, 5);
        post.getValorations().put(2, 3);
        post.getValorations().put(3, 5);

        when(productRepository.getPostsByPostId(10)).thenReturn(Optional.of(post));

        // Act
        List<ValorationDTO> result = productService.getValorationsByPost(10, null);

        // Assert
        assertEquals(3, result.size());
    }

    @DisplayName("US-0014.2 - Si ninguna valoración coincide con el filtro, devuelve lista vacía.")
    @Test
    void getValorationsByPost_shouldReturnEmptyWhenNoMatchForValorationNumber() {
        // Arrange
        Post post = TestFactory.createPostWithValoration(30, 1, 3);
        when(productRepository.getPostsByPostId(30)).thenReturn(Optional.of(post));

        // Act
        List<ValorationDTO> result = productService.getValorationsByPost(30, 5);

        // Assert
        assertTrue(result.isEmpty());
    }

    @DisplayName("US-0012 - Obtener un listado de todos los productos en promoción de un determinado vendedor.")
    @Test
    void getPromotionalProductsFromSellers() {
        //Arrange
        User user = TestFactory.createUser(1);
        List<Post> postList = TestFactory.createPostList(5, 1);
        when(this.userRepository.getUserById(anyInt())).thenReturn(Optional.of(user));
        when(this.productRepository.getPromotionalProductsFromSellers(anyInt())).
                thenReturn(postList);

        //Act
        PromoProductsDto promoProductsDto = this.productService.
                getPromotionalProductsFromSellers(user.getUserId());

        //Assert
        assertEquals(5, promoProductsDto.getPromoPostDtoList().size());
    }

    @DisplayName("US-0012 - Obtener un listado de todos los productos en promoción de un determinado vendedor." +
                 "Usuario no encontrado.")
    @Test
    void getPromotionalProductsFromSellersUserNotFound() {
        //Arrange
        User user = TestFactory.createUser(1);
        when(this.userRepository.getUserById(anyInt())).thenThrow(BadRequestException.class);

        //Act y Assert
        assertThrows(BadRequestException.class, () -> this.productService.
                getPromotionalProductsFromSellers(user.getUserId()));
    }

    @DisplayName("US-0012 - Obtener un listado de todos los productos en promoción de un determinado vendedor. " +
                 "- No tiene promo")
    @Test
    void getPromotionalProductsFromSellersNotPromo() {
        //Arrange
        User user = TestFactory.createUser(5);
        Post post1 = TestFactory.createPost(10, 5);
        Post post2 = TestFactory.createPost(11, 5);

        this.productRepository.save(post1);
        this.productRepository.save(post2);

        //Act
        List<Post> postList = this.productRepository.
                getPromotionalProductsFromSellers(user.getUserId());

        //Assert
        Assertions.assertEquals(0, postList.size());
    }

    @DisplayName("US-0014.1 - Verificar que la valoración es procesada correctamente cuando la valoración está entre 1 y 5.")
    @Test
            void testValoratePost_validValoration_shouldProcessWithoutException() {
        // Arrange
        ValorationDTO valorationDTO = new ValorationDTO(1, 1, 3);
        Post post = TestFactory.createPost(1, 1, LocalDate.now().minusWeeks(1));
        User user = TestFactory.createUser(1);

        when(productRepository.getPostsByPostId(1)).thenReturn(Optional.of(post));
        when(userRepository.getUserById(1)).thenReturn(Optional.of(user));

        // Act & Assert
        assertDoesNotThrow(() -> productService.valorateAPost(valorationDTO));
    }

    @DisplayName("US-0014.1 - Verificar que no se lanza ninguna excepción cuando la valoración es válida.")
    @Test
    void testValoratePost_withoutException() {
        // Arrange
        ValorationDTO valorationDTO = new ValorationDTO(1, 1, 3);
        Post post = TestFactory.createPost(1, 1, LocalDate.now().minusWeeks(1));
        User user = TestFactory.createUser(1);

        when(productRepository.getPostsByPostId(1)).thenReturn(Optional.of(post));
        when(userRepository.getUserById(1)).thenReturn(Optional.of(user));

        // Act & Assert
        assertDoesNotThrow(() -> productService.valorateAPost(valorationDTO));
    }


    @DisplayName("US-0014.1 - Verificar que se lanza una excepción cuando la valoración es menor que 1.")
    @Test
    void valorateAPost_shouldThrowExceptionWhenValorationIsLessThan1() {
        // Arrange
        ValorationDTO valorationDTO = new ValorationDTO(1, 1, 0);
        Post post = TestFactory.createPost(1, 1, LocalDate.now().minusWeeks(1));
        User user = TestFactory.createUser(1);

        when(productRepository.getPostsByPostId(1)).thenReturn(Optional.of(post));
        when(userRepository.getUserById(1)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productService.valorateAPost(valorationDTO));
    }

    @DisplayName("US-0014.1 - Verificar que se lanza una excepción cuando el post no existe.")
    @Test
    void valorateAPost_shouldThrowExceptionWhenPostNotFound() {
        // Arrange
        ValorationDTO valorationDTO = new ValorationDTO(1, 1, 3);
        when(productRepository.getPostsByPostId(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productService.valorateAPost(valorationDTO));
    }


    @DisplayName("US-0014.1 - Verificar que se lanza una excepción cuando la valoración es mayor que 5.")
    @Test
    void valorateAPost_shouldThrowExceptionWhenValorationIsGreaterThan5() {
        // Arrange
        ValorationDTO valorationDTO = new ValorationDTO(1, 1, 6);
        Post post = TestFactory.createPost(1, 1, LocalDate.now().minusWeeks(1));
        User user = TestFactory.createUser(1);

        when(productRepository.getPostsByPostId(1)).thenReturn(Optional.of(post));
        when(userRepository.getUserById(1)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productService.valorateAPost(valorationDTO));

    }
    @DisplayName("US-0015 - Listar las valoraciones que realizó un usuario.")
        @Test
        void getAllValorationsByUser_ShouldReturnOnlyMatchingValorations () {
            // Arrange
            List<Post> post = TestFactory.createPostListWithValorations();
            when(userRepository.getUserById(DEFAULT_USER_ID)).thenReturn(Optional.of(defaultUser));
            when(productRepository.getAll()).thenReturn(post);
            // Act
            List<ValorationDTO> result = productService.getAllValorationsByUser(DEFAULT_USER_ID);
            // Assert
            assertEquals(3, result.size());
            assertTrue(result.stream().anyMatch(v -> v.getPost_id() == 1));
            assertTrue(result.stream().anyMatch(v -> v.getPost_id() == 2));
            assertTrue(result.stream().anyMatch(v -> v.getPost_id() == 5));
            assertTrue(result.stream().anyMatch(v -> v.getValoration() == 3));
            assertTrue(result.stream().anyMatch(v -> v.getValoration() == 4));
            assertTrue(result.stream().anyMatch(v -> v.getValoration() == 5));
        }

        @DisplayName("US-0015 - Listar las valoraciones que realizó un usuario.")
        @Test
        void getAllValorationsByUser_ShouldReturnEmptyValorations () {
            // Arrange
            Integer userId = 4;
            User user = TestFactory.createUser(userId);
            List<Post> posts = TestFactory.createPostListWithValorations();
            when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
            when(productRepository.getAll()).thenReturn(posts);
            // Act
            List<ValorationDTO> result = productService.getAllValorationsByUser(userId);
            // Assert
            assertTrue(result.isEmpty());
        }

        @DisplayName("US-0015 - Listar las valoraciones que realizó un usuario.")
        @Test
        void getAllValorationsByUser_ShouldReturnBadRequestException () {
            // Arrange
            when(userRepository.getUserById(DEFAULT_USER_ID)).thenReturn(Optional.empty());
            // Act & Assert
            assertThrows(BadRequestException.class, () -> productService.getAllValorationsByUser(DEFAULT_USER_ID));
        }


        @DisplayName("US-0013 - Verifica que la valoración no sea null.")
        @Test
        void valorateAPost_shouldSaveTheValoration() {
            // Arrange
            ValorationDTO valorationDTO = TestFactory.createValorationDTO(1, 2, 4);
            Post post = TestFactory.createPost(2, 1);
            User user = TestFactory.createUser(1);

            when(productRepository.getPostsByPostId(2)).thenReturn(Optional.of(post));
            when(userRepository.getUserById(1)).thenReturn(Optional.of(user));

            //Act
            productService.valorateAPost(valorationDTO);

            //Assert
            verify(productRepository).saveValoration(2, 1, 4);

        }

    @DisplayName("US-0013 - Excepción BadRequest para valoraciones menores a 1.")
    @Test
    void valoratePost_shouldThrowExceptionWhenInvalidLowValoration() {
        // Arrange
        ValorationDTO valorationDTO = TestFactory.createValorationDTO(1, 2, 0);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productService.valorateAPost(valorationDTO));
        verify(productRepository, never()).saveValoration(anyInt(), anyInt(), anyInt());
    }

    @DisplayName("US-0013 - Excepción BadRequest para valoraciones mayores a 5.")
    @Test
    void valoratePost_shouldThrowExceptionWhenInvalidHighValoration() {
        // Arrange
        ValorationDTO valorationDTO = TestFactory.createValorationDTO(1, 2, 6);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productService.valorateAPost(valorationDTO));
        verify(productRepository, never()).saveValoration(anyInt(), anyInt(), anyInt());
    }


    @DisplayName("US 0011 - Obtiene la cantidad de posteos con promoción según un usuario.")
    @Test
    void getQuantityOfProducts_withUserId_shouldReturnListOfProductsWithPromo(){
        // Arrange
        User user = TestFactory.createUser(5);
        user.setUserName("Ornella");
        Post post1 = TestFactory.createPost(10, user.getUserId());
        post1.setHasPromo(true);
        Post post2 = TestFactory.createPost(11, user.getUserId());
        post2.setHasPromo(true);
        when(userRepository.getUserById(5)).thenReturn(Optional.of(user));
        when(productRepository.getPostsByUserId(user.getUserId())).thenReturn(List.of(post1, post2));
        // Act
        PromoProductsCountDto count = productService.getQuantityOfProducts(post1.getUserId());
        // Assert
        assertEquals(2, count.getPromoProductsCount());
    }

    @DisplayName("US 0011 - Si los hasPromo estan en false, la lista debería estar vacía.")
    @Test
    void getQuantityOfProducts_withHasPromoFalse_shouldReturnCero(){
        // Arrange
        User user = TestFactory.createUser(5);
        user.setUserName("Ornella");
        Post post1 = TestFactory.createPost(10, user.getUserId());
        Post post2 = TestFactory.createPost(11, user.getUserId());
        when(userRepository.getUserById(5)).thenReturn(Optional.of(user));
        when(productRepository.getPostsByUserId(user.getUserId())).thenReturn(List.of(post1, post2));
        // Act
        PromoProductsCountDto count = productService.getQuantityOfProducts(post1.getUserId());
        // Assert
        assertEquals(0, count.getPromoProductsCount());
    }

    @DisplayName("US 0011 - Si el usuario no es encontrado, debería lanzar una excepción.")
    @Test
    void getQuantityOfProducts_withFalseUser_shouldReturnException() {
        // Arrange
        when(userRepository.getUserById(9999)).thenThrow(new NotFoundException("Usuario no encontrado"));
        // Act & Assert
        assertThrows(NotFoundException.class, () ->
                productService.getQuantityOfProducts(9999));
    }
    // Test: metodo CreatePost
    // Usuario válido, post sin promoción
    @Test
    void createPost_withValidPost_shouldSavePost() {
        // Arrange
        PostDto postDto = TestFactory.createPostDto(1);
        when(userRepository.getUserById(1)).thenReturn(Optional.of(new User()));

        // Act
        productService.createPost(postDto);

        // Assert
        verify(userRepository).getUserById(1);
        verify(productRepository).save(any(Post.class));
    }

    // Usuario válido, post con promoción válida
    @Test
    void createPost_withPromoAndValidDiscount_shouldApplyPromo() {
        // Arrange
        PromoPostDto promoDto = TestFactory.createPromoPostDto(1, 0.2);
        when(userRepository.getUserById(1)).thenReturn(Optional.of(new User()));

        // Act
        productService.createPost(promoDto);

        // Assert
        verify(productRepository).save(argThat(post ->
                post.getHasPromo() && post.getDiscount() == 0.2
        ));
    }

    // Usuario no encontrado
    @Test
    void createPost_withInvalidUser_shouldThrowBadRequest() {
        // Arrange
        PostDto postDto = TestFactory.createPostDto(99);
        when(userRepository.getUserById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productService.createPost(postDto));
    }

    // Post con descuento inválido (> 1)
    @Test
    void createPost_withInvalidDiscount_shouldThrowBadRequest() {
        // Arrange
        PromoPostDto promoDto = TestFactory.createPromoPostDto(1, 1.5);
        when(userRepository.getUserById(1)).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productService.createPost(promoDto));
    }
    @Test
    void createPost_withNullDiscount_shouldThrowBadRequest() {
        // Arrange
        PromoPostDto promoDto = TestFactory.createPromoPostDto(1,null);
        when(userRepository.getUserById(1)).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productService.createPost(promoDto));
    }
    @Test
    void createPost_withZeroDiscount_shouldThrowBadRequest() {
        // Arrange
        PromoPostDto promoDto = TestFactory.createPromoPostDto(1,0.0);
        when(userRepository.getUserById(1)).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productService.createPost(promoDto));

    }
}


