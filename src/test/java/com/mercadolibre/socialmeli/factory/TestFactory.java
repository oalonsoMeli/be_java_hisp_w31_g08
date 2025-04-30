package com.mercadolibre.socialmeli.factory;

import com.mercadolibre.socialmeli.dto.*;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.Product;
import com.mercadolibre.socialmeli.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestFactory {
    // ****************User
    public static User createUser(Integer id) {
        User user = new User();
        user.setUserId(id);
        user.setUserName("User" + id);
        user.setUserEmail("user" + id + "@example.com");
        user.setFollows(new HashSet<>());
        return user;
    }

    public static UserDto createUserDTO(Integer id) {
        UserDto user = new UserDto();
        user.setUser_id(id);
        user.setUser_name("User" + id);
        return user;
    }

    public static User createUserWithFollow(Integer id, Integer followedId) {
        User user = createUser(id);
        user.getFollows().add(followedId);
        return user;
    }

    public static User createUserFollowing(Integer userId, Integer... followedIds) {
        User user = TestFactory.createUser(userId);
        for (Integer followedId : followedIds) {
            user.getFollows().add(followedId);
        }
        return user;
    }


    //****************Product
    public static Product createProduct(Integer id) {
        return new Product(
                id,
                "Product " + id,
                "Type" + id,
                "Brand" + id,
                "Color" + id,
                "Notes for product " + id
        );
    }

    public static ProductDto createProductDto(Integer id) {
        return new ProductDto(
                id,
                "Product " + id,
                "Type" + id,
                "Brand" + id,
                "Color" + id,
                "Notes for product " + id
        );
    }

    //****************Post
    public static Post createPost(Integer postId, Integer userId) {
        return new Post(
                userId,
                LocalDate.now().minusDays(1),
                createProduct(postId),
                100,
                1500.0
        );
    }

    public static Post createPost(Integer postId, Integer userId, LocalDate createdAt) {
        return new Post( postId,
                userId,
                createdAt,
                createProduct(postId),
                100,
                1500.0, false, 0.0, new HashMap<>()
        );
    }

    public static Post createPostWithPromo(Integer postId, Integer userId, Double discount) {
        return new Post( postId,
                userId,
                LocalDate.now().minusDays(1),
                createProduct(postId),
                100,
                1500.0, true, discount, new HashMap<>()
        );
    }

    public static PostDto createPostDto(Integer userId) {
        return new PostDto(
                userId,
                LocalDate.now(),
                createProductDto(1),
                100,
                1500.0
        );
    }

    public static List<Post> createPostList(int cantidad, Integer userId) {
        return IntStream.rangeClosed(1, cantidad)
                .mapToObj(i -> createPost(i, userId))
                .collect(Collectors.toList());
    }

    public static List<PostDto> createPostDtoList(int cantidad, Integer userId) {
        return IntStream.rangeClosed(1, cantidad)
                .mapToObj(i -> new PostDto(
                        userId,
                        LocalDate.now().minusDays(i),
                        createProductDto(i),
                        100 + i,
                        1000.0 + (i * 100)
                ))
                .collect(Collectors.toList());
    }

    public static PromoPostDto createPromoPostDto(Integer userId, double discount) {
        PromoPostDto dto = new PromoPostDto();
        dto.setUserId(userId);
        dto.setDate(LocalDate.now().minusDays(1));
        dto.setProduct(createProductDto(1));
        dto.setCategory(100);
        dto.setPrice(1500.0);
        dto.setHasPromo(true);
        dto.setDiscount(discount);
        return dto;
    }

    public static List<Post> createPostsForFollowedUsers(Integer... userIds) {
        List<Post> posts = new ArrayList<>();
        int postId = 1;
        for (Integer userId : userIds) {
            posts.add(TestFactory.createPost(postId++, userId));
        }
        return posts;
    }

    //****************Valoracion
    public static ValorationDTO createValorationDTO(Integer userId, Integer postId, Integer valoration) {
        return new ValorationDTO(userId, postId, valoration);
    }

    public static Post createPostWithValoration(Integer postId, Integer userId, Integer valoration) {
        Post post = createPost(postId, userId);

        Map<Integer, Integer> valorations = new HashMap<>();
        valorations.put(userId, valoration); // el usuario valora el post

        // Asignar el mapa al post
        post.setValorations((HashMap<Integer, Integer>) valorations);
        post.setPostId(postId); // aseguro que tenga el ID seteado

        return post;
    }


}
