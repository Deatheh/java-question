package petproject.javaquestion.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import petproject.javaquestion.dto.RefreshTokenRequest;
import petproject.javaquestion.dto.TokenResponse;
import petproject.javaquestion.dto.UserCredentialsRequest;
import petproject.javaquestion.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> registration(
            @Valid @RequestBody UserCredentialsRequest userDto
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody UserCredentialsRequest userDto
    ){
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(userDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest refreshToken
    ){
        return ResponseEntity.status(HttpStatus.OK).body(authService.refresh(refreshToken));
    }
}
