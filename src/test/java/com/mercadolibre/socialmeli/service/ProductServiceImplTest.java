package com.mercadolibre.socialmeli.service;
import com.mercadolibre.socialmeli.dto.PostsDto;
import com.mercadolibre.socialmeli.exception.IllegalArgumentException;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IProductRepository;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import com.mercadolibre.socialmeli.utilities.OrderType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    void getListOfPublicationsByUser() {
    }


    @DisplayName("Verificar que el tipo de ordenamiento por fecha exista (US-0009) de forma ascendente")
    @Test
    void getListOfPublicationsByUser_verifyDateSortTypeExistsAsc() {
        //Arrange
        User user = TestFactory.createUser(1);
        List<Post> postList = TestFactory.createPostList(5,1);
        when(this.productRepository.getPostsByUserIdsInLastTwoWeeks(anySet(),anyString())).thenReturn(postList);
        when(this.userRepository.getUserById(anyInt())).thenReturn(Optional.of(user));

        //Act
        PostsDto postsDto = this.productService.getListOfPublicationsByUser(user.getUserId(),
                OrderType.ORDER_DATE_ASC.getValue());

        //Assert
        Assertions.assertEquals(5,postsDto.getPosts().size());
    }

    @DisplayName("Verificar que el tipo de ordenamiento por fecha exista (US-0009) de forma descendente")
    @Test
    void getListOfPublicationsByUser_verifyDateSortTypeExistsDesc() {
        //Arrange
        User user = TestFactory.createUser(1);
        List<Post> postList = TestFactory.createPostList(5,1);
        when(this.productRepository.getPostsByUserIdsInLastTwoWeeks(anySet(),anyString())).thenReturn(postList);
        when(this.userRepository.getUserById(anyInt())).thenReturn(Optional.of(user));

        //Act
        PostsDto postsDto = this.productService.getListOfPublicationsByUser(user.getUserId(),
                OrderType.ORDER_DATE_DESC.getValue());

        //Assert
        Assertions.assertEquals(5,postsDto.getPosts().size());
    }

    @DisplayName("Verificar que el tipo de ordenamiento por fecha exista (US-0009) - NO EXISTE")
    @Test
    void getListOfPublicationsByUser_verifyDateSortTypeNoExist() {
        //Arrange
        User user = TestFactory.createUser(1);
        when(this.userRepository.getUserById(anyInt())).thenReturn(Optional.of(user));

        //Act y Assert -- PREGUNTAR A CHATGPT
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.productService.getListOfPublicationsByUser(user.getUserId(),
                "otro"));
    }

    @DisplayName("Verificar que el tipo de ordenamiento por fecha exista (US-0009). Lista de posts vacías")
    @Test
    void getListOfPublicationsByUser_verifyDateSortTypeExistPostsEmpty() {
        //Arrange
        User user = TestFactory.createUser(1);
        List<Post> postList = new ArrayList<>();
        when(this.productRepository.getPostsByUserIdsInLastTwoWeeks(anySet(),anyString())).thenReturn(postList);
        when(this.userRepository.getUserById(anyInt())).thenReturn(Optional.of(user));

        //Act y Assert
        Assertions.assertThrows(NotFoundException.class, ()-> this.productService.getListOfPublicationsByUser(user.getUserId(),
                OrderType.ORDER_DATE_DESC.getValue()));
    }
}
