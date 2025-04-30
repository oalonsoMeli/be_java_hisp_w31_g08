package com.mercadolibre.socialmeli.repository;

import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
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

    @Test
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

}