package com.mercadolibre.socialmeli.service;

import com.mercadolibre.socialmeli.dto.PostsDto;
import com.mercadolibre.socialmeli.dto.ValorationDTO;
import com.mercadolibre.socialmeli.exception.BadRequestException;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IProductRepository;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import com.mercadolibre.socialmeli.utilities.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

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

    @Test
        // US006 - Ordenamiento por fecha Desc Order
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

    @Test
        // US006 - Ordenamiento por fecha Asc Order
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

    @Test
        // US008 - Excepción no hay publicaciones de quienes siguen
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

    @Test
        // US008 - Lanza excepción si el usuario no existe
    void getListOfPublicationsByUser_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.getUserById(DEFAULT_USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadRequestException.class, () ->
                productService.getListOfPublicationsByUser(DEFAULT_USER_ID, OrderType.ORDER_DATE_DESC.getValue())
        );
    }

    @Test
        // US0014.2 - Devuelve solo las valoraciones que coinciden con el número filtrado
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

    @Test
        // US0014.2 - Si ninguna valoración coincide con el filtro, devuelve lista vacía
    void getValorationsByPost_shouldReturnEmptyWhenNoMatchForValorationNumber() {
        // Arrange
        Post post = TestFactory.createPostWithValoration(30, 1, 3);
        when(productRepository.getPostsByPostId(30)).thenReturn(Optional.of(post));

        // Act
        List<ValorationDTO> result = productService.getValorationsByPost(30, 5);

        // Assert
        assertTrue(result.isEmpty());
    }
}
