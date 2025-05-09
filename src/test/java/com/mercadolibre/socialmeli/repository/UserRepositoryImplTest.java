package com.mercadolibre.socialmeli.repository;
import com.mercadolibre.socialmeli.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryImplTest {

    // Crear los usuarios para que se reutilicen en los test.
    private IUserRepository repository;
    private User userExpected1;
    private User userExpected2;

    // beforeEach para que cada test sea independiente del otro.
    @BeforeEach
    public void init() throws IOException {
        repository = new UserRepositoryImpl();
        repository = new UserRepositoryImpl();
        userExpected1 = repository.getUserById(1).orElseThrow();
        userExpected2 = repository.getUserById(2).orElseThrow();
    }

    @DisplayName("Al pasarle los ids de usuarios, debería devolverme aquellos usuarios con ese id.")
    @Test
    void findUsersById_shouldReturnListOfUsers() {
        // Arrange
        List<User> userById = List.of(userExpected1, userExpected2);

        // Act
        List<User> usersObtained = repository.findUsersById(List.of(2, 1));

        // Assert
        assertEquals(usersObtained.get(0).getUserName(), userById.get(0).getUserName());
        assertEquals(usersObtained.get(1).getUserName(), userById.get(1).getUserName());
    }


    @DisplayName("Buscar por id de usuario debería retornarme el usuario que busco y no estar vacío.")
    @Test
    void getUserById_shoulReturnUser() {
        // Arrange
        String nameExpected = "John Doe";
        // Act
        Optional<User> userObtained = repository.getUserById(1);
        User user = userObtained.get();
        // Assert
        assertFalse(userObtained.isEmpty());
        assertEquals(nameExpected, user.getUserName());
    }

    @DisplayName("Verifica el comportamiento esperado cuando se consulta un usuario que no existe.")
    @Test
    void getUserById_shouldReturnEmptyWhenUserDoesNotExist() {
        // Act
        Optional<User> result = repository.getUserById(9999);
        // Assert
        assertTrue(result.isEmpty(), "Debe devolver vacío si el usuario no existe.");
    }


    @DisplayName("Devuelve el cálculo correcto del total de la cantidad de seguidores que posee un usuario.")
    @Test
    void getAll_souldReturnAllUsers() {
        //Act
        List<User> result = repository.getAll();

        //Assert
        assertNotNull(result);
        assertTrue(result.contains(userExpected1));
        assertTrue(result.contains(userExpected2));
        assertEquals(100, result.size());
    }

    @DisplayName("buscar por id de usuario debería devolverme el usuario si existe.")
    @Test
    void getUserById_shouldReturnUserIfExists() {
        // Arrange
        Integer userId = 1;

        // Act
        Optional<User> result = repository.getUserById(userId);

        // Assert
        assertTrue(result.isPresent(), "El usuario debería existir.");
        assertEquals("John Doe", result.get().getUserName());
    }
}
