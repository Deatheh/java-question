package petproject.javaquestion.service;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import petproject.javaquestion.dto.RefreshTokenRequest;
import petproject.javaquestion.dto.TokenResponse;
import petproject.javaquestion.dto.UserCredentialsRequest;
import petproject.javaquestion.model.User;
import petproject.javaquestion.repository.UserRepository;
import petproject.javaquestion.security.jwt.JwtService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public TokenResponse register(UserCredentialsRequest userDto){
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        User newUser = userService.createNewUser(user);
        return jwtService.generateToken(newUser.getUsername());
    }

    public TokenResponse login(UserCredentialsRequest userDto){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.getUsername(),
                        userDto.getPassword()
                )
        );

        return jwtService.generateToken(userDto.getUsername());
    }

    public TokenResponse refresh(RefreshTokenRequest refreshDto){
        String refreshToken = refreshDto.getRefreshToken();
        String username = jwtService.extractUsername(refreshToken);

        userService.findUserByUsername(username);

        if (!jwtService.validateToken(refreshToken)){
            throw new JwtException("Invalid token");
        }

        return jwtService.generateToken(username);
    }
}
