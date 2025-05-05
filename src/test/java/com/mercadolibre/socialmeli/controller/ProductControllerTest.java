package com.mercadolibre.socialmeli.controller;
import com.mercadolibre.socialmeli.dto.*;
import com.mercadolibre.socialmeli.dto.PostDto;
import com.mercadolibre.socialmeli.dto.PostsDto;
import com.mercadolibre.socialmeli.dto.ValorationAverageDto;
import com.mercadolibre.socialmeli.exception.BadRequestException;
import com.mercadolibre.socialmeli.exception.NotFoundException;
import com.mercadolibre.socialmeli.dto.ValorationDTO;
import com.mercadolibre.socialmeli.factory.TestFactory;
import com.mercadolibre.socialmeli.model.Post;
import com.mercadolibre.socialmeli.model.User;
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
import java.util.ArrayList;
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

    // T-00016 - US0016: Verifica que calcule el promedio de las valoraciones de un post y que el body no esté vacío.
    @Test
    void getValorationsByPost_shouldReturnValorationAverageDto() {
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

    @Test

    // US0014.1 - Controller recibe correctamente una valoración y devuelve status OK
    void valorateAPost_shouldCallServiceAndReturnOk() {
        // Arrange
        ValorationDTO valorationDTO = new ValorationDTO(1, 10, 4);

        doNothing().when(productService).valorateAPost(valorationDTO);

        // Act
        ResponseEntity<Void> response = productController.valorateAPost(valorationDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService, times(1)).valorateAPost(valorationDTO);
    }


    // US 0015 Listar las valoraciones que realizó un usuario
    void getAllValorationsByUser_ShouldReturnOnlyMatchingValorations() {
        // Assert
        Integer userId = 1;
        when(productService.getAllValorationsByUser(userId)).thenReturn(new ArrayList<ValorationDTO>());

        // Act
        ResponseEntity<List<ValorationDTO>> response = productController.getAllValorationsByUser(userId);
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService, times(1)).getAllValorationsByUser(userId);
    }

    @Test
        // US 0015 Listar las valoraciones que realizó un usuario
    void getAllValorationsByUser_shouldPropagateExceptionWhenServiceFails() {
        // Assert
        when(productService.getAllValorationsByUser(Integer.MAX_VALUE))
                .thenThrow(new BadRequestException("Usuario no encontrado."));
        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            productController.getAllValorationsByUser(Integer.MAX_VALUE);
        });
        verify(productService, times(1)).getAllValorationsByUser(Integer.MAX_VALUE);

    }

    @Test
    // US0014.1 - Controller devuelve OK con todas las valoraciones de un post por su ID
    void getValorationsByPost_shouldReturnAllValorationsForGivenPostId() {
        // Arrange
        Integer postId = 10;
        Integer valorationNumber = 3;
        List<ValorationDTO> valorations = List.of(
                new ValorationDTO(1, postId, 3),
                new ValorationDTO(2, postId, 5)
        );

        when(productService.getValorationsByPost(postId, valorationNumber)).thenReturn(valorations);

        // Act
        ResponseEntity<List<ValorationDTO>> response = productController.getValorationsByPost(postId, valorationNumber);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(productService, times(1)).getValorationsByPost(postId, valorationNumber);
    }





}
