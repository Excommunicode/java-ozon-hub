package kz.ozon.javaozonhub.config;

import io.jsonwebtoken.Claims;
import kz.ozon.javaozonhub.model.Role;
import kz.ozon.javaozonhub.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public final class JwtUtils {
    private final RoleRepository roleRepository;

    public JwtAuthentication generate(Claims claims) {
        JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setFirstName(claims.get("firstName", String.class));
        jwtInfoToken.setUsername(claims.getSubject());
        return jwtInfoToken;
    }

    private Set<Role> getRoles(Claims claims) {
        List<String> roles = claims.get("roles", List.class);
        List<Role> all = roleRepository.findAll();
        return new HashSet<>(all);
    }

}