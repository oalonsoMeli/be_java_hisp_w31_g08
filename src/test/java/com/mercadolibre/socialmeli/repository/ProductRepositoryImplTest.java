package com.mercadolibre.socialmeli.repository;
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

@SpringBootTest
class ProductRepositoryImplTest {

    private IProductRepository productRepository;
    private Post post1;
    private Post post2;
    private Post post3;
    private Post post4;
    @BeforeEach
    void setUp() {
        productRepository = new ProductRepositoryImpl();
        // Arrange
        post1 = TestFactory.createPost(1, 1, LocalDate.now().minusWeeks(1));
        post1.getProduct().setProductName("Lavadora");
        post2 = TestFactory.createPost(2, 2, LocalDate.now().minusDays(5));
        post3 = TestFactory.createPost(3, 3, LocalDate.now().minusDays(3));
        post4 = TestFactory.createPost(4, 2, LocalDate.now().minusDays(5));
        productRepository.save(post1);
        productRepository.save(post2);
        productRepository.save(post3);
        productRepository.save(post4);
    }

    @Test
    // US006 - Ordenamiento en orden descendente es correcto
    void getPostsByUserIdsInLastTwoWeeks_DescOrder(){
        // Arrange
        Set<Integer> users = Set.of(1, 2, 3);
        // Act
        List<Post> posts = productRepository.getPostsByUserIdsInLastTwoWeeks(users, ORDER_DATE_DESC.getValue());
        // Assert
        assertNotNull(posts);
        assertEquals(4, posts.size());
        for (int i = 0; i < posts.size() - 1; i++) {
            LocalDate current = posts.get(i).getDate();
            LocalDate next = posts.get(i + 1).getDate();
            assertTrue(current.isAfter(next) || current.isEqual(next),
                    "Posts are not sorted in descending order: " + current + " vs " + next);
        }
    }

    @Test
    // US006 - Ordenamiento en orden ascendente es correcto
    void getPostsByUserIdsInLastTwoWeeks_AscOrder(){
        // Arrange
        Set<Integer> users = Set.of(1, 2, 3);
        // Act
        List<Post> posts = productRepository.getPostsByUserIdsInLastTwoWeeks(users, ORDER_DATE_ASC.getValue());
        // Assert
        assertNotNull(posts);
        assertEquals(4, posts.size());
        for (int i = 0; i < posts.size() - 1; i++) {
            LocalDate current = posts.get(i).getDate();
            LocalDate next = posts.get(i + 1).getDate();
            assertTrue(current.isBefore(next) || current.isEqual(next),
                    "Posts are not sorted in descending order: " + current + " vs " + next);
        }
    }


    // Este test se fija si al buscar post por el id, me devuelva el nombre del producto que contiene ese post
    @Test
    void getPostsById_shouldReturnTheNameOfTheProduct() {
        // Arrange
        String productNameExpected = "Lavadora";
        // Act
        Optional<Post> postObtained = productRepository.getPostsByPostId(1);
        // Assert
        assertEquals(postObtained.get().getProduct().getProductName(), productNameExpected);
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
        Post post1 = TestFactory.createPostWithPromo(10, 5, 5D);
        Post post2 = TestFactory.createPostWithPromo(11, 5, 5D);

        this.productRepository.save(post1);
        this.productRepository.save(post2);

        //Act
        List<Post> postList = this.productRepository.
                getPromotionalProductsFromSellers(user.getUserId());

        //Assert
        Assertions.assertEquals(2, postList.size());
    }

    @Test
    // US008 - Filtrado Post de las ultimas dos semanas es correcto
    void getPostsByUserIdsInLastTwoWeeks_shouldReturnOnlyPostsInLastTwoWeeks() {
        // Arrange
        Set<Integer> users = Set.of(1, 2, 3);
        Post oldPost = TestFactory.createPost(4, 1, LocalDate.now().minusWeeks(3));
        productRepository.save(oldPost);
        // Act
        List<Post> posts = productRepository.getPostsByUserIdsInLastTwoWeeks(users, ORDER_DATE_DESC.getValue());
        // Assert
        assertNotNull(posts);
        assertFalse(posts.isEmpty(), "The posts list should not be empty");
        LocalDate twoWeeksAgo = LocalDate.now().minusWeeks(2);
        for (Post post : posts) {
            assertTrue(post.getDate().isAfter(twoWeeksAgo) || post.getDate().isEqual(twoWeeksAgo),
                    "Found a post older than two weeks: " + post.getDate());
        }
    }

    @Test
    // US008 - Filtrado Post de las ultimas dos semanas exactamente es correcto
    void getPostsByUserIdsInLastTwoWeeks_shouldIncludePostExactlyTwoWeeksAgo() {
        // Arrange
        Set<Integer> users = Set.of(1, 2, 3);
        Post exactTwoWeeksPost = TestFactory.createPost(5, 1, LocalDate.now().minusWeeks(2));
        productRepository.save(exactTwoWeeksPost);
        // Act
        List<Post> posts = productRepository.getPostsByUserIdsInLastTwoWeeks(users, ORDER_DATE_DESC.getValue());
        // Assert
        assertNotNull(posts);
        assertTrue(posts.stream().anyMatch(post -> post.getDate().isEqual(LocalDate.now().minusWeeks(2))),
                "The post created exactly two weeks ago should be included.");

    }

    @Test
    // US0015 - El método retorna la lista de post
    void getAll_shouldReturnAllProducts() {
        // Act
        List<Post> posts = productRepository.getAll();
        // Assert
        assertNotNull(posts);
        assertFalse(posts.isEmpty(), "The posts list should not be empty");
    }

    @DisplayName("US 0013 - Verifica que la valoración no sea null.")
    @Test
    void saveValoration_sholdSaveValorationInPost() {
        // Arrange
        Post post = TestFactory.createPost(5, 1, LocalDate.now().minusWeeks(1));
        productRepository.save(post);
        // Act
        productRepository.saveValoration(post.getPostId(), post.getUserId(), 3);
        // Assert
        assertFalse(post.getValorations().isEmpty());
    }

<<<<<<< HEAD
    @DisplayName("US 0011 - Verifica que se obtenga la lista de post por userId.")
    @Test
    void getPostByUserId_shouldReturnThePost(){
        // Arrange
        int sizeExpected = 2;
        // Act
        List<Post> posts = productRepository.getPostsByUserId(2);
        // Asert
        assertEquals(sizeExpected, posts.size());
        assertFalse(posts.isEmpty());
        assertEquals(2, posts.get(0).getUserId());
        assertEquals(2, posts.get(1).getUserId());
    }

=======
    @DisplayName("Verifica que se retonen los post de un usuario")
    @Test
    void getPostsByUserId_shouldReturnUserPost(){
        // Arrange
        Integer userId = 1;
        // Act
        List<Post> posts = productRepository.getPostsByUserId(userId);
        // Assert
        assertFalse(posts.isEmpty());
        posts.forEach(post -> {
            assertEquals(userId, post.getUserId());
        });
    }

    @DisplayName("Verifica que se retorne una lista vacía cuando el usuario no tiene posts")
    @Test
    void getPostsByUserId_shouldReturnEmptyListWhenNoPostsExist() {
        // Arrange
        Integer userId = Integer.MAX_VALUE;
        // Act
        List<Post> posts = productRepository.getPostsByUserId(userId);
        // Assert
        assertTrue(posts.isEmpty(), "La lista debería estar vacía");
    }

    @DisplayName("Verifica que se lance la excepción cuando userId es null")
    @Test
    void getPostsByUserId_shouldHandleNullUserId() {
        // Arrange
        Integer userId = null;
        // Act && Assert
        assertThrows(NullPointerException.class, ()-> productRepository.getPostsByUserId(userId));
    }
>>>>>>> develop
}