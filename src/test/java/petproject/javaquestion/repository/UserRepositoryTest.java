package petproject.javaquestion.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import petproject.javaquestion.model.Role;
import petproject.javaquestion.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findUserByUsername_shouldReturnUser_whenUserExists() {
        // given — создаём пользователя
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);

        // when
        Optional<User> found = userRepository.findUserByUsername("testuser");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void findUserByUsername_shouldReturnEmpty_whenUserNotFound() {
        // when
        Optional<User> found = userRepository.findUserByUsername("nonexistent");

        // then
        assertThat(found).isEmpty();
    }
}
