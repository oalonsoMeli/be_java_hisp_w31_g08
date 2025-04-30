package com.mercadolibre.socialmeli.service;
import com.mercadolibre.socialmeli.dto.PostsDto;
import com.mercadolibre.socialmeli.dto.PromoProductsDto;
import com.mercadolibre.socialmeli.exception.BadRequestException;
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
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
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

        //Act y Assert
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

    @Test
    // US006 - Ordenamiento por fecha Desc Order
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
    // US006 - Ordenamiento por fecha  Asc Order
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
    // US008 - Excepción no hay publicaciones de quienes siguen
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
    // US008 - Lanza la exepción si el usuario no existe
    void getListOfPublicationsByUser_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        Integer userId = 1;
        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            productService.getListOfPublicationsByUser(userId, OrderType.ORDER_DATE_DESC.getValue());
        });
    }

    @Test
    // US0014.2 - Devuelve solo las valoraciones que coinciden con el numero filtrado
    void getValorationsByPost_shouldReturnOnlyMatchingValorations() {
        // Arrange - Post con valoraciones 5, 3, 5
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
    // US0014.2 - Si ninguna valoracion coincide con el filtro, devuelve una lista vacia
    void getValorationsByPost_shouldReturnEmptyWhenNoMatchForValorationNumber() {
        // Arrange
        Post post = TestFactory.createPostWithValoration(30, 1, 3);

        when(productRepository.getPostsByPostId(30)).thenReturn(Optional.of(post));

        // Act
        List<ValorationDTO> result = productService.getValorationsByPost(30, 5);

        // Assert
        assertTrue(result.isEmpty());
    }

    @DisplayName("US 0012 - Obtener un listado de todos los productos en promoción de un determinado vendedor")
    @Test
    void getPromotionalProductsFromSellers(){
        //Arrange
        User user = TestFactory.createUser(1);
        List<Post> postList = TestFactory.createPostList(5,1);
        when(this.userRepository.getUserById(anyInt())).thenReturn(Optional.of(user));
        when(this.productRepository.getPromotionalProductsFromSellers(anyInt())).
                thenReturn(postList);

        //Act
        PromoProductsDto promoProductsDto = this.productService.
                getPromotionalProductsFromSellers(user.getUserId());

        //Assert
        assertEquals(5,promoProductsDto.getPromoPostDtoList().size());
    }

    @DisplayName("US 0012 - Obtener un listado de todos los productos en promoción de un determinado vendedor" +
            "Usuario no encontrado")
    @Test
    void getPromotionalProductsFromSellersUserNotFound(){
        //Arrange
        User user = TestFactory.createUser(1);
        when(this.userRepository.getUserById(anyInt())).thenThrow(BadRequestException.class);

        //Act y Assert
        assertThrows(BadRequestException.class,()->this.productService.
                getPromotionalProductsFromSellers(user.getUserId()));
    }

    @DisplayName("US 0012 - Obtener un listado de todos los productos en promoción de un determinado vendedor. - No tiene promo")
    @Test
    void getPromotionalProductsFromSellersNotPromo() {
        //Arrange
        User user = TestFactory.createUser(5);
        Post post1 = TestFactory.createPost(10,5);
        Post post2 = TestFactory.createPost(11,5);

        this.productRepository.save(post1);
        this.productRepository.save(post2);

        //Act
        List<Post> postList = this.productRepository.
                getPromotionalProductsFromSellers(user.getUserId());

        //Assert
        Assertions.assertEquals(0,postList.size());
    }

}
