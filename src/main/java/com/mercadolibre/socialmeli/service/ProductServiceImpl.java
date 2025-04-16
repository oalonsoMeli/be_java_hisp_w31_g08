package com.mercadolibre.socialmeli.service;
import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.repository.IProductRepository;
import com.mercadolibre.socialmeli.utilities.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    IProductRepository iProductRepository;

    @Override
    public List<PostDto> orderByDateAscOrDesc(String order){
       List<Post> postListOrder = iProductRepository.orderByDateAscOrDesc(order);
       return postListOrder.stream().map(Mappers::postEntityToDto).toList();
    }
}
