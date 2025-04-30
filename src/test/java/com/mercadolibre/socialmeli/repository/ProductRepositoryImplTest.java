package com.mercadolibre.socialmeli.repository;

import com.mercadolibre.socialmeli.dto.PostsDto;
import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.utilities.OrderType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.mercadolibre.socialmeli.utilities.OrderType.ORDER_DATE_ASC;
import static com.mercadolibre.socialmeli.utilities.OrderType.ORDER_DATE_DESC;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductRepositoryImplTest {

    private IProductRepository productRepository;
    private Post post1;
    private Post post2;
    private Post post3;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepositoryImpl();
        // Arrange
        post1 = TestFactory.createPost(1, 1, LocalDate.now().minusWeeks(1));
        post2 = TestFactory.createPost(2, 2, LocalDate.now().minusDays(5));
        post3 = TestFactory.createPost(3, 3, LocalDate.now().minusDays(3));
        productRepository.save(post1);
        productRepository.save(post2);
        productRepository.save(post3);
    }

    @Test
    void getPostsByUserIdsInLastTwoWeeks_DescOrder(){
        // Arrange
        Set<Integer> users = Set.of(1, 2, 3);
        // Act
        List<Post> posts = productRepository.getPostsByUserIdsInLastTwoWeeks(users, ORDER_DATE_DESC.getValue());
        // Assert
        assertNotNull(posts);
        assertEquals(3, posts.size());
        for (int i = 0; i < posts.size() - 1; i++) {
            LocalDate current = posts.get(i).getDate();
            LocalDate next = posts.get(i + 1).getDate();
            assertTrue(current.isAfter(next) || current.isEqual(next),
                    "Posts are not sorted in descending order: " + current + " vs " + next);
        }
    }

    @Test
    void getPostsByUserIdsInLastTwoWeeks_AscOrder(){
        // Arrange
        Set<Integer> users = Set.of(1, 2, 3);
        // Act
        List<Post> posts = productRepository.getPostsByUserIdsInLastTwoWeeks(users, ORDER_DATE_ASC.getValue());
        // Assert
        assertNotNull(posts);
        assertEquals(3, posts.size());
        for (int i = 0; i < posts.size() - 1; i++) {
            LocalDate current = posts.get(i).getDate();
            LocalDate next = posts.get(i + 1).getDate();
            assertTrue(current.isBefore(next) || current.isEqual(next),
                    "Posts are not sorted in descending order: " + current + " vs " + next);
        }
    }

    @DisplayName("Verificar que el tipo de ordenamiento por fecha exista (US-0009) de forma ascendente")
    @Test
    void getListOfPublicationsByUser_verifyDateSortTypeExistsAsc() {
        //Arrange
        User user = TestFactory.createUser(5);
        Post post1 = TestFactory.createPost(10,5);
        Post post2 = TestFactory.createPost(11,5);
        this.productRepository.save(post1);
        this.productRepository.save(post2);

        Set<Integer> listIds = Set.of(user.getUserId());

        //Act
        List<Post> posts = this.productRepository.getPostsByUserIdsInLastTwoWeeks(
                listIds, OrderType.ORDER_DATE_ASC.getValue());

        //Assert
        Assertions.assertEquals(2,posts.size());
    }

    @DisplayName("Verificar que el tipo de ordenamiento por fecha exista (US-0009) de forma descendente")
    @Test
    void getListOfPublicationsByUser_verifyDateSortTypeExistsDesc() {
        //Arrange
        User user = TestFactory.createUser(5);
        Post post1 = TestFactory.createPost(10,5);
        Post post2 = TestFactory.createPost(11,5);
        this.productRepository.save(post1);
        this.productRepository.save(post2);

        Set<Integer> listIds = Set.of(user.getUserId());

        //Act
        List<Post> posts = this.productRepository.getPostsByUserIdsInLastTwoWeeks(
                listIds, OrderType.ORDER_DATE_DESC.getValue());

        //Assert
        Assertions.assertEquals(2,posts.size());
    }

    @DisplayName("US 0012 - Obtener un listado de todos los productos en promoción de un determinado vendedor")
    @Test
    void getPromotionalProductsFromSellers() {
        //Arrange
        User user = TestFactory.createUser(5);
        Post post1 = TestFactory.createPostWithPromo(10,5,5D);
        Post post2 = TestFactory.createPostWithPromo(11,5,5D);

        this.productRepository.save(post1);
        this.productRepository.save(post2);

        //Act
        List<Post> postList = this.productRepository.
                getPromotionalProductsFromSellers(user.getUserId());

        //Assert
        Assertions.assertEquals(2,postList.size());
    }

}