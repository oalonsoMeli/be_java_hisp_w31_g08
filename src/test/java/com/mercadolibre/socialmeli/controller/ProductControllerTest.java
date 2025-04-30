package com.mercadolibre.socialmeli.controller;

import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.dto.PostsDto;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.service.IProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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


    @Test
    void getListOfPublicationsByUser_shouldUseDefaultOrderWhenOrderParamIsMissing() {
        // Arrange
        Integer userId = 1;
        String defaultOrder = "date_asc";
        List<PostDto> postDtos = List.of(
                TestFactory.createPostDto(2),
                TestFactory.createPostDto(3)
        );
        PostsDto expectedPostsDto = new PostsDto(userId, postDtos);
        when(productService.getListOfPublicationsByUser(userId, defaultOrder)).thenReturn(expectedPostsDto);

        // Act
        ResponseEntity<PostsDto> response = productController.getListOfPublicationsByUser(userId, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService, times(1)).getListOfPublicationsByUser(userId, null);
    }

    @Test
    void getListOfPublicationsByUser_shouldPropagateExceptionWhenServiceFails() {
        // Arrange
        Integer userId = 1;
        String order = "date_desc";
        when(productService.getListOfPublicationsByUser(userId, order))
                .thenThrow(new NotFoundException("No hay publicaciones de quienes sigues."));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            productController.getListOfPublicationsByUser(userId, order);
        });

        verify(productService, times(1)).getListOfPublicationsByUser(userId, order);
    }
}
