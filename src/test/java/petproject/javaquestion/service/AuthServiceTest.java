package petproject.javaquestion.service;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import petproject.javaquestion.dto.RefreshTokenRequest;
import petproject.javaquestion.dto.TokenResponse;
import petproject.javaquestion.dto.UserCredentialsRequest;
import petproject.javaquestion.model.User;
import petproject.javaquestion.security.jwt.JwtService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldReturnToken_whenCreateNewUser(){
        UserCredentialsRequest userDto = new UserCredentialsRequest();
        userDto.setUsername("testuser");
        userDto.setPassword("rawPassword");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("rawPassword");

        when(userService.createNewUser(user)).thenReturn(user);
        when(jwtService.generateToken("testuser")).thenReturn(new TokenResponse("1", "2"));

        // when
        TokenResponse token = authService.register(userDto);

        // then
        assertThat(token.getAccessToken()).isEqualTo("1");
        assertThat(token.getRefreshToken()).isEqualTo("2");
    }

    @Test
    void login_shouldReturnToken_whenPasswordCorrect(){
        UserCredentialsRequest userDto = new UserCredentialsRequest();
        userDto.setUsername("testuser");
        userDto.setPassword("rawPassword");

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userDto.getUsername(),
                userDto.getPassword()
        ))).thenReturn(null);
        when(jwtService.generateToken("testuser")).thenReturn(new TokenResponse("1", "2"));

        // when
        TokenResponse token = authService.login(userDto);

        // then
        assertThat(token.getAccessToken()).isEqualTo("1");
        assertThat(token.getRefreshToken()).isEqualTo("2");
    }

    @Test
    void refresh_shouldReturnToken(){
        RefreshTokenRequest refreshDto = new RefreshTokenRequest("token");

        when(jwtService.extractUsername("token")).thenReturn("testuser");
        when(userService.findUserByUsername("testuser")).thenReturn(null);
        when(jwtService.validateToken("token")).thenReturn(true);

        when(jwtService.generateToken("testuser")).thenReturn(new TokenResponse("1", "2"));

        // when
        TokenResponse token = authService.refresh(refreshDto);

        // then
        assertThat(token.getAccessToken()).isEqualTo("1");
        assertThat(token.getRefreshToken()).isEqualTo("2");

    }

    @Test
    void refresh_shouldReturnToken_whenTokenInvalid(){
        RefreshTokenRequest refreshDto = new RefreshTokenRequest("token");

        when(jwtService.extractUsername("token")).thenReturn("testuser");
        when(userService.findUserByUsername("testuser")).thenReturn(null);
        when(jwtService.validateToken("token")).thenReturn(false);


        // when / then
        assertThatThrownBy(() -> authService.refresh(refreshDto))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Invalid token");



    }

}
