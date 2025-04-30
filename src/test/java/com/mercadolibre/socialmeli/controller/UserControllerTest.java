package com.mercadolibre.socialmeli.controller;

import com.mercadolibre.socialmeli.exception.BadRequestException;
import com.mercadolibre.socialmeli.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;


    // T-0001 - Test de Controller: seguir usuario con éxito
    @Test
    void followUser_shouldReturnOkWhenFollowSuccess() throws Exception {

        mockMvc.perform(post("/users/1/follow/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    // T-0001 - Test de Controller: el usuario a seguir no existe
    @Test
    void followUser_shouldReturnBadRequestWhenUserToFollowDoesNotExist() throws Exception {
        doThrow(new BadRequestException("Usuario a seguir no encontrado"))
                .when(userService).followUser(1, 2);

        mockMvc.perform(post("/users/1/follow/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }



    @Test
    // T-0002 - Test de Controller: el usuario a dejar de seguir no existe
    void unfollowUser_shouldReturnBadRequestWhenUserToUnfollowDoesNotExist() throws Exception {
        doThrow(new BadRequestException("Usuario no encontrado"))
                .when(userService).unfollowUser(1, 2);

        mockMvc.perform(put("/users/1/unfollow/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    // T-0002 - Test de Controller: operacion correcta
    void unfollowUser_shouldReturnOkWhenUnfollowSuccess() throws Exception {
        mockMvc.perform(put("/users/1/unfollow/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
