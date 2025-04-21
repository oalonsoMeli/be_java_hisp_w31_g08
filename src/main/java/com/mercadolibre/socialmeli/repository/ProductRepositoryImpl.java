package com.mercadolibre.socialmeli.repository;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.utilities.Constants;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Repository
public class ProductRepositoryImpl implements IProductRepository {

    private final List<Post> listOfPost = new ArrayList<>();

    @Override
    public void save(Post post) {
            listOfPost.add(post);
    }

    @Override
    public List<Post> getAll() {
        return listOfPost;
    }

    //Parametro Order: Indica que tipo de ordenamiento se realizará por fecha (ascendente o descendente).
    @Override
    public List<Post> getPostsByUserIdsInLastTwoWeeks(Set<Integer> userIds, String order) {
        LocalDate twoWeeksAgo = LocalDate.now().minusWeeks(2);

        return (order.equals(Constants.ORDER_DATE_DESC)) ?
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

    @Override
    public List<Post> getPostsByUserId(Integer userId) {

        return listOfPost.stream()
                        .filter(p -> userId.equals(p.getUserId()))
                        .toList();
    }
}
