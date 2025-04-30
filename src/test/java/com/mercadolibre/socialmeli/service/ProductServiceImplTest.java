package com.mercadolibre.socialmeli.service;

import com.mercadolibre.socialmeli.dto.PostsDto;
import com.mercadolibre.socialmeli.exception.BadRequestException;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IProductRepository;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import com.mercadolibre.socialmeli.utilities.OrderType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
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
    IUserRepository userRepository;

    @Mock
    IProductRepository productRepository;

    @InjectMocks
    ProductServiceImpl productService;


    @Test
    void getListOfPublicationsByUser_shouldSortDescOrder() {
        // Arrange
        Integer userId = 1;
        User user = TestFactory.createUserFollowing(userId, 2, 3);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        List<Post> postsFollowedUsers = TestFactory.createPostsForFollowedUsers(2, 3);
        when(productRepository.getPostsByUserIdsInLastTwoWeeks(anySet(), eq(OrderType.ORDER_DATE_DESC.getValue())))
                .thenReturn(postsFollowedUsers);
        // Act
        PostsDto result = productService.getListOfPublicationsByUser(userId, OrderType.ORDER_DATE_DESC.getValue());
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getPosts().size());
        assertTrue(result.getPosts().get(0).getDate().isAfter(result.getPosts().get(1).getDate())
                || result.getPosts().get(0).getDate().isEqual(result.getPosts().get(1).getDate()));
    }

    @Test
    void getListOfPublicationsByUser_shouldSortAscOrder() {
        // Arrange
        Integer userId = 1;
        User user = TestFactory.createUserFollowing(userId, 2, 3);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        List<Post> postsFollowedUsers = TestFactory.createPostsForFollowedUsers(2, 3);
        when(productRepository.getPostsByUserIdsInLastTwoWeeks(anySet(), eq(OrderType.ORDER_DATE_ASC.getValue())))
                .thenReturn(postsFollowedUsers);
        // Act
        PostsDto result = productService.getListOfPublicationsByUser(userId, OrderType.ORDER_DATE_ASC.getValue());
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getPosts().size());
        assertTrue(result.getPosts().get(0).getDate().isBefore(result.getPosts().get(1).getDate())
                || result.getPosts().get(0).getDate().isEqual(result.getPosts().get(1).getDate()));
    }

    @Test
    void getListOfPublicationsByUser_shouldThrowNotFoundWhenNoPosts() {
        // Arrange
        Integer userId = 1;
        User user = TestFactory.createUserFollowing(userId, 2, 3);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(productRepository.getPostsByUserIdsInLastTwoWeeks(anySet(), anyString()))
                .thenReturn(Collections.emptyList());
        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            productService.getListOfPublicationsByUser(userId, OrderType.ORDER_DATE_DESC.getValue());
        });
    }

    @Test
    void getListOfPublicationsByUser_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        Integer userId = 1;
        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            productService.getListOfPublicationsByUser(userId, OrderType.ORDER_DATE_DESC.getValue());
        });
    }

}