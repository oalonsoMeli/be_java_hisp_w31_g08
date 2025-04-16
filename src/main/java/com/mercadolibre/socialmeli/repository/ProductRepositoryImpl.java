package com.mercadolibre.socialmeli.repository;
import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.dto.PostsDto;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.utilities.Constansts;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Repository
public class ProductRepositoryImpl implements IProductRepository {

    private List<Post> listOfProducts = new ArrayList<>();

    @Override
    public List<Post> orderByDateAscOrDesc(String order){
        List<Post> postList = new ArrayList<>();

        return switch (order) {
            case Constansts.ORDER_DATE_ASC -> postList.stream().sorted(
                    Comparator.comparing(Post::getDate)).toList();
            case Constansts.ORDER_DATE_DESC -> postList.stream().sorted(
                    Comparator.comparing(Post::getDate).reversed()).toList();
            default -> postList;
        };
    }


}
