package com.mercadolibre.socialmeli.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mercadolibre.socialmeli.dto.*;
import com.mercadolibre.socialmeli.exception.BadRequestException;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.repository.IProductRepository;
import com.mercadolibre.socialmeli.repository.IUserRepository;
import com.mercadolibre.socialmeli.exception.IllegalArgumentException;
import com.mercadolibre.socialmeli.utilities.OrderType;
import com.mercadolibre.socialmeli.utilities.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
        Post post = PostMapper.toPost(postDto);
        if (isPromoActive(postDto)) {
            applyPromotion((PromoPostDto) postDto, post);
        }
        productRepository.save(post);
    }

    private boolean isPromoActive(PostDto postDto) {
        if (postDto instanceof PromoPostDto promo) {
            return Boolean.TRUE.equals(promo.getHasPromo());
        }
        return false;
    }

    private void applyPromotion(PromoPostDto promoPostDto, Post post) {
        Double discount = promoPostDto.getDiscount();
        if (discount == null || discount <= 0 || discount >= 1) {
            throw new BadRequestException("Descuento inválido.");
        }
        post.setDiscount(discount);
        post.setHasPromo(true);
    }

    // Valida que el usuario exista
    private User validationUser(Integer userId) {
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado."));
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

    // Obtiene la lista de Post de los vendedores que un usuario sigue.
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

    //Obtiene el listado de los productos que un vendedor tiene en promoción
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

    // Registra una valoración (1 a 5) de un usuario sobre un post.
    @Override
    public void valorateAPost(ValorationDTO valorationDTO) {
        Post post = getPostById(valorationDTO.getPost_id());
        User user = validationUser(valorationDTO.getUser_id());
        if(valorationDTO.getValoration() < 1 || valorationDTO.getValoration() > 5){
            throw new BadRequestException("Se permiten solo valoraciones del 1 al 5.");
        }
        this.productRepository.saveValoration(valorationDTO.getPost_id(), valorationDTO.getUser_id(), valorationDTO.getValoration());
    }

    private Post getPostById(Integer postId) {
        return productRepository.getPostsByPostId(postId)
                .orElseThrow(() -> new BadRequestException("No se encontró el post a valorar."));
    }

    @Override
    public List<ValorationDTO> getValorationsByPost(Integer postId, Integer valorationNumber) {
        Post post = getPostById(postId);
        // crea el map con las valoraciones del post
        Map<Integer, Integer> valorations = post.getValorations();


        /* genera una lista de valoration dto, filtrado:.
        - si no tiene especificación, trae todas las valoraciones.
        - si tiene especificación, trae solamente valoraciones de ese número*/
        return valorations.entrySet().stream()
                .filter(entry -> valorationNumber == null || entry.getValue().equals(valorationNumber))
                .map(entry -> new ValorationDTO(entry.getKey(), postId, entry.getValue()))
                .collect(Collectors.toList());

    }

    @Override
    public List<ValorationDTO> getAllValorationsByUser(Integer userId) {
        User user = validationUser(userId);
        return this.productRepository.getAll().stream()
                .filter(p -> p.getValorations().containsKey(userId))
                .map(p -> new ValorationDTO(
                        userId,
                        p.getPostId(),
                        p.getValorations().get(userId)))
                .collect(Collectors.toList());
    }

    @Override
    public ValorationAverageDto getValorationsAverageByPost(Integer postId) {
        Post post = getPostById(postId);
        Map<Integer, Integer> valorations = post.getValorations();
        Double average = valorations.values().stream().mapToDouble(v -> v).average().orElse(0.0);
        return new ValorationAverageDto(average);
    }


}
