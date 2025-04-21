package com.mercadolibre.socialmeli.repository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.Product;
import com.mercadolibre.socialmeli.utilities.OrderType;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Repository
public class ProductRepositoryImpl implements IProductRepository {

    private List<Post> listOfPost = new ArrayList<>();
    private List<Product> listOfProduct = new ArrayList<>();

    @Override
    public void save(Post post) {
            listOfPost.add(post);
    }

    @Override
    public List<Post> getAll() {
        return listOfPost;
    }

    public void loadDataBaseProduct() {
        File file;
        ObjectMapper objectMapper = new ObjectMapper();
        List<Product> products ;

        try{
            file= ResourceUtils.getFile("classpath:data/products.json");
            products= objectMapper.readValue(file, new TypeReference<>() {
            });
            listOfProduct = products;
        }catch (Exception exception){
            throw new RuntimeException("No se pudo parsear el json de productos.");
        }
    }

    public void loadDataBasePost() {
        File file;
        ObjectMapper objectMapper = new ObjectMapper();
        List<Post> posts ;

        try{
            file= ResourceUtils.getFile("classpath:data/posts.json");
            posts= objectMapper.readValue(file, new TypeReference<>() {
            });
            listOfPost = posts;
        }catch (Exception exception){
            throw new RuntimeException("No se pudo parsear el json de posts.");
        }
    }

    //Parametro Order: Indica que tipo de ordenamiento se realizará por fecha (ascendente o descendente).
    @Override
    public List<Post> getPostsByUserIdsInLastTwoWeeks(Set<Integer> userIds, String order) {
        LocalDate twoWeeksAgo = LocalDate.now().minusWeeks(2);

        return (order.equals(OrderType.ORDER_DATE_DESC.getValue())) ?
                listOfPost.stream()
                .filter(p -> userIds.contains(p.getUserId()))
                .filter(p -> !p.getDate().isBefore(twoWeeksAgo))
                .sorted(Comparator.comparing(Post::getDate).reversed())
                .toList() :
                listOfPost.stream()
                .filter(p -> userIds.contains(p.getUserId()))
                .filter(p -> !p.getDate().isBefore(twoWeeksAgo))
                .sorted(Comparator.comparing(Post::getDate))
                .toList();
    }
}
