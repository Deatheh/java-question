package petproject.javaquestion.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCredentialsRequest {
    @NotNull(message = "Username must be not null")
    @Size(min = 1, max = 256, message = "Username must be 1-256 chars")
    private String username;

    @NotNull(message = "Password must be not null")
    @Size(min = 1, max = 256, message = "Password must be 1-256 chars")
    private String password;
}
