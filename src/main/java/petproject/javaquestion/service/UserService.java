package petproject.javaquestion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import petproject.javaquestion.model.Role;
import petproject.javaquestion.model.User;
import petproject.javaquestion.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.datasource.username}")
    private String adminUsername;

    @Value("${spring.datasource.password}")
    private String adminPassword;

    public User findUserByUsername(String username){
        return userRepository
                .findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with name: " + username + " not found"));
    }

    public User createNewUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        return userRepository.save(user);
    }
}
