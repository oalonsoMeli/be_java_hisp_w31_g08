package com.mercadolibre.socialmeli.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.dto.ProductDto;
import com.mercadolibre.socialmeli.dto.PromoPostDto;
import com.mercadolibre.socialmeli.exception.BadRequestException;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.Product;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IProductRepository;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import com.mercadolibre.socialmeli.dto.PostsDto;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IProductRepository;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import com.mercadolibre.socialmeli.repository.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {
    private  IUserRepository userRepository;
    private IProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(IUserRepository userRepository, IProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }


    @Override
    public void createPost(PostDto postDto) {
        userRepository.getUserById(postDto.getUserId())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        ProductDto productDto = postDto.getProduct();
        Product product = new Product(
                productDto.getProduct_id(),
                productDto.getProduct_name(),
                productDto.getType(),
                productDto.getBrand(),
                productDto.getColor(),
                productDto.getNotes()
        );
        Post post = new Post(
                postDto.getUserId(),
                postDto.getDate(),
                product,
                postDto.getCategory(),
                postDto.getPrice()
        );
        if (postDto instanceof PromoPostDto promoPostDto) {
            if (promoPostDto.getHasPromo() != null && promoPostDto.getHasPromo()) {
                if (promoPostDto.getDiscount() == null || promoPostDto.getDiscount() <= 0 || promoPostDto.getDiscount() >= 1) {
                    throw new BadRequestException("Descuento inválido");
                }
                post.setDiscount(promoPostDto.getDiscount());
                post.setHasPromo(true);
            }
        }
        productRepository.save(post);
    }


    @Override
    public PostsDto getListOfPublicationsByUser(Integer userId) {
        Optional<User> optionalUser = userRepository.getUserById(userId);
        if(optionalUser.isEmpty()){
            throw new NotFoundException("Este usuario no existe");
        }

        User user = optionalUser.get();
        Set<Integer> followedUserIds = user.getFollows();

        List<Post> posts = this.productRepository.getPostsByUserIdsInLastTwoWeeks(followedUserIds);
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
