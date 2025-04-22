package com.mercadolibre.socialmeli.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mercadolibre.socialmeli.dto.*;
import com.mercadolibre.socialmeli.exception.BadRequestException;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.Product;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IProductRepository;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import com.mercadolibre.socialmeli.exception.IllegalArgumentException;
import com.mercadolibre.socialmeli.utilities.OrderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductServiceImpl implements IProductService {

    private IUserRepository userRepository;
    private IProductRepository productRepository;
    private Integer countId = 1;
    @Autowired
    public ProductServiceImpl(IUserRepository userRepository, IProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }


    @Override
    public void createPost(PostDto postDto) {
        validationUser(postDto.getUserId());
        Post post = getPost(postDto);
        if (postDto instanceof PromoPostDto promoPostDto) {
            if (promoPostDto.getHasPromo() != null && promoPostDto.getHasPromo()) {
                if (promoPostDto.getDiscount() == null || promoPostDto.getDiscount() <= 0 || promoPostDto.getDiscount() >= 1) {
                    throw new BadRequestException("Descuento inválido.");
                }
                post.setDiscount(promoPostDto.getDiscount());
                post.setHasPromo(true);
            }
        }
        productRepository.save(post);
    }

    private User validationUser(Integer userId) {
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado."));
    }

    private Post getPost(PostDto postDto) {
        ProductDto productDto = postDto.getProduct();
        Product product = new Product( productDto.getProduct_id(), productDto.getProduct_name(), productDto.getType(),
                productDto.getBrand(), productDto.getColor(), productDto.getNotes());
        Post post = new Post(postDto.getUserId(), postDto.getDate(), product,
                postDto.getCategory(), postDto.getPrice());
        post.setPostId(countId);
        countId++;
        return post;
    }

    @Override
    public PromoProductsCountDto getQuantityOfProducts(Integer userId) {
        User user = validationUser(userId);
        List<Post> posts = this.productRepository.getPostsByUserId(userId);
        Integer count = (int)posts.stream()
                .filter(Post::getHasPromo)
                .count();
        return new PromoProductsCountDto(userId, user.getUserName(), count);
    }

    @Override
    public PostsDto getListOfPublicationsByUser(Integer userId, String order) {
        User user = validationUser(userId);
        if(!order.equals(OrderType.ORDER_DATE_ASC.getValue()) &&
                !order.equals(OrderType.ORDER_DATE_DESC.getValue())){
            throw new IllegalArgumentException("Párametro de ordenamiento no permitido.");
        }
        Set<Integer> followedUserIds = user.getFollows();
        List<Post> posts = this.productRepository.getPostsByUserIdsInLastTwoWeeks(followedUserIds,order);
        if(posts.isEmpty()){
            throw new NotFoundException("No hay publicaciones de quienes sigues.");
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<PostDto> postDtos = posts.stream()
                .map(post -> mapper.convertValue(post, PostDto.class))
                .toList();
         return new PostsDto(userId, postDtos);
    }

    @Override
    public PromoProductsDto getPromotionalProductsFromSellers(Integer userId){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        User user = validationUser(userId);
        List<PromoPostDto> promoPostDtoList = this.productRepository.getPromotionalProductsFromSellers(userId)
                .stream().map(post -> mapper.convertValue(post, PromoPostDto.class)
                ).toList();
        return new PromoProductsDto(user.getUserId(),user.getUserName(),promoPostDtoList);
    }
}
