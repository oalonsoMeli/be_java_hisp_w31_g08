package com.mercadolibre.socialmeli.service;
import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.dto.PostsDto;
import com.mercadolibre.socialmeli.exception.IllegalArgumentException;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.repository.IProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import com.mercadolibre.socialmeli.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductServiceImpl implements IProductService {

    private  IUserRepository userRepository;
    private IProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(IProductRepository productRepository, IUserRepository userRepository) {
    this.productRepository = productRepository;
    this.userRepository = userRepository;
}

    @Override
    public PostsDto getListOfPublicationsByUser(Integer userId, String order) {
        Optional<User> optionalUser = userRepository.getUserById(userId);
        if(optionalUser.isEmpty()){
            throw new NotFoundException("Este usuario no existe");
        }

        if(!order.equals(Constants.ORDER_DATE_ASC) && !order.equals(Constants.ORDER_DATE_DESC)){
            throw new IllegalArgumentException("Párametro de ordenamiento no permitido.");
        }

        User user = optionalUser.get();
        Set<Integer> followedUserIds = user.getFollows();

        List<Post> posts = this.productRepository.getPostsByUserIdsInLastTwoWeeks(followedUserIds,order);
        if(posts.isEmpty()){
            throw new NotFoundException("No hay publicaciones de quienes sigues.");
        }
        ObjectMapper mapper = new ObjectMapper();
        List<PostDto> postDtos = posts.stream()
                .map(post -> mapper.convertValue(post, PostDto.class))
                .toList();
         return new PostsDto(userId, postDtos);
    }
}
