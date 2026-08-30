package petproject.javaquestion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import petproject.javaquestion.dto.RefreshTokenRequest;
import petproject.javaquestion.dto.TokenResponse;
import petproject.javaquestion.dto.UserCredentialsRequest;
import petproject.javaquestion.security.jwt.JwtFilter;
import petproject.javaquestion.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AuthController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                ServletWebSecurityAutoConfiguration.class
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtFilter.class
                )
        }
)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createUser_shouldReturnToken_whenValidRequest() throws Exception {
        // given
        UserCredentialsRequest userDto = new UserCredentialsRequest();
        userDto.setUsername("testuser");
        userDto.setPassword("rawPassword");
        TokenResponse token = new TokenResponse("1", "2");

        when(authService.register(any())).thenReturn(token);

        // when / then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("1"))
                .andExpect(jsonPath("$.refreshToken").value("2"));
    }

    @Test
    void loginUser_shouldReturnToken_whenValidRequest() throws Exception {
        // given
        UserCredentialsRequest userDto = new UserCredentialsRequest();
        userDto.setUsername("testuser");
        userDto.setPassword("rawPassword");
        TokenResponse token = new TokenResponse("1", "2");

        when(authService.login(any())).thenReturn(token);

        // when / then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("1"))
                .andExpect(jsonPath("$.refreshToken").value("2"));
    }

    @Test
    void refreshToken_shouldReturnToken_whenValidRequest() throws Exception {
        // given
        RefreshTokenRequest refreshToken = new RefreshTokenRequest();
        refreshToken.setRefreshToken("token");
        TokenResponse token = new TokenResponse("1", "2");

        when(authService.refresh(any())).thenReturn(token);

        // when / then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("1"))
                .andExpect(jsonPath("$.refreshToken").value("2"));
    }
}
