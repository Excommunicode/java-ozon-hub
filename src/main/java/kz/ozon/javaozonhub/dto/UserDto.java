package kz.ozon.javaozonhub.dto;

import kz.ozon.javaozonhub.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    private UUID id;
    private String login;
    private String password;
    private String email;
    private Set<Role> roles;
}
