package com.mercadolibre.socialmeli.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.Product;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ProductRepositoryImpl implements IProductRepository {

    private List<Post> listOfProducts = new ArrayList<>();

    private final List<Post> posts = new ArrayList<>();

    @Override
    public void save(Post post) {
        posts.add(post);
    }

    @Override
    public List<Post> getAll() {
        return posts;
    }


    public List<Post> getPostsByUserIdsInLastTwoWeeks(Set<Integer> userIds) {
        LocalDate twoWeeksAgo = LocalDate.now().minusWeeks(2);
        return listOfProducts.stream()
                .filter(p -> userIds.contains(p.getUserId()))
                .filter(p -> !p.getDate().isBefore(twoWeeksAgo))
                .sorted(Comparator.comparing(Post::getDate).reversed())
                .collect(Collectors.toList());
    }
}
