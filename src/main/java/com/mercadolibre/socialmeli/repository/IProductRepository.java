package com.mercadolibre.socialmeli.repository;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.Post;
import java.util.Set;
import java.util.List;

public interface IProductRepository {
    void save(Post post);
    List<Post> getAll();
    List<Post> getPostsByUserIdsInLastTwoWeeks(Set<Integer> userIds);
}
