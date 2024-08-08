package kz.ozon.javaozonhub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private Long id;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private Set<RoleDto> roles;
}
