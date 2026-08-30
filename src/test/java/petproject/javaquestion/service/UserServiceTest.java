package petproject.javaquestion.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import petproject.javaquestion.model.User;
import petproject.javaquestion.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    @Test
    void findUserByUsername_shouldReturnUser_whenUserExists(){
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));

        //when
        User found = userService.findUserByUsername("testuser");

        assertThat(found.getUsername()).isEqualTo("testuser");
    }

    @Test
    void findUserByUsername_shouldThrowException_whenUserNotFound() {
        // given
        when(userRepository.findUserByUsername("nonexistent")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.findUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User with name: nonexistent not found");
    }

    @Test
    void createNewUser_shouldEncodePasswordAndSave() {
        // given
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("rawPassword");

        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        User saved = userService.createNewUser(user);

        // then
        assertThat(saved.getPassword()).isEqualTo("encodedPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }
}
