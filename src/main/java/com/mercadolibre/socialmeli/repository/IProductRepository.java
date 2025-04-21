package com.mercadolibre.socialmeli.repository;
import com.mercadolibre.socialmeli.model.Post;
import java.util.List;
import java.util.Set;

public interface IProductRepository {
    List<Post> getPostsByUserIdsInLastTwoWeeks(Set<Integer> userIds, String order);
    void save(Post post);
    List<Post> getAll();
    List<Post> getPostsByUserId(Integer userId);
}
