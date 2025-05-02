package com.mercadolibre.socialmeli.controller;
import com.mercadolibre.socialmeli.dto.*;
import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.dto.PostsDto;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.dto.ValorationDTO;
import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.User;
import com.mercadolibre.socialmeli.service.IProductService;
import org.junit.jupiter.api.DisplayName;
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
    // US 008 - Controller devuelve OK PostDto
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
    // US0014.2 - Controller devuelve OK con lista filtrada por puntuacion
    void getValorationsByPost_shouldReturnOkWithFilteredResults() {
        // Arrange - service devuelve 2 valoraciones con 5
        List<ValorationDTO> valorations = List.of(
                new ValorationDTO(1, 10, 5),
                new ValorationDTO(3, 10, 5)
        );

        when(productService.getValorationsByPost(10, 5)).thenReturn(valorations);

        // Act
        ResponseEntity<List<ValorationDTO>> response = productController.getValorationsByPost(10, 5);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals(5, response.getBody().get(0).getValoration());
    }


    @DisplayName("US 0012 - Obtener un listado de todos los productos en promoción de un determinado vendedor")
    @Test
    void getPromotionalProductsFromSellers_shouldReturnPostsDtoAndStatusOk() {
        // Arrange
        User user = TestFactory.createUser(5);
        PromoPostDto promoPostDto = TestFactory.createPromoPostDto(5, 5);
        PromoProductsDto promoProductsDto = new PromoProductsDto(
                user.getUserId(), user.getUserName(), List.of(promoPostDto)
        );

        when(productService.getPromotionalProductsFromSellers(user.getUserId())).thenReturn(promoProductsDto);
        // Act
        ResponseEntity<PromoProductsDto> response = productController.
                getPromotionalProductsFromSellers(user.getUserId());
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService, times(1)).
                getPromotionalProductsFromSellers(user.getUserId());
    }

    @Test
        // US 008 - El controller toma el ordenamiento por defecto
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
    // US 008 - El controller recibe la excepción lanzada desde el service
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
