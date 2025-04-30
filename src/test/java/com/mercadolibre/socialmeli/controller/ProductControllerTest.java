package com.mercadolibre.socialmeli.controller;

import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.dto.PostsDto;
import com.mercadolibre.socialmeli.dto.ValorationAverageDto;
import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.service.IProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ProductControllerTest {

    @Mock
    private IProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void getListOfPublicationsByUser_shouldReturnPostsDtoAndStatusOk() {
        // Arrange
        Integer userId = 1;
        String order = "date_desc";
        List<PostDto> postDtos = List.of(
                TestFactory.createPostDto(2),
                TestFactory.createPostDto(3)
        );
        PostsDto expectedPostsDto = new PostsDto(userId, postDtos);
        when(productService.getListOfPublicationsByUser(userId, order)).thenReturn(expectedPostsDto);
        // Act
        ResponseEntity<PostsDto> response = productController.getListOfPublicationsByUser(userId, order);
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPostsDto, response.getBody());
        verify(productService, times(1)).getListOfPublicationsByUser(userId, order);
    }

    // T-00016 - US0016: Verifica que calcule el promedio de las valoraciones de un post y que el body no esté vacío.
    @Test
    void getValorationsByPost_shouldReturnValorationAverageDto(){
       // Arrange
        Integer postId = 1;
        Post post = TestFactory.createPost(postId, 1, LocalDate.now().minusWeeks(1));
        HashMap<Integer, Integer> valorations = new HashMap<>();
        valorations.put(1, 5);
        valorations.put(2, 3);
        post.setValorations(valorations);
        ValorationAverageDto valorationsDTO = new ValorationAverageDto(4.0);
        Double valorationsExpected = 4.0;
        when(productService.getValorationsAverageByPost(1)).thenReturn(valorationsDTO);

        // Act
        ResponseEntity<ValorationAverageDto> response = productController.getValorationsByPost(1);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(valorationsExpected, response.getBody().getAverage());

    }
}
