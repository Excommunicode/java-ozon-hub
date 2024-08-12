package kz.ozon.javaozonhub.jwt;

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
public class JwtUtils {
    private final RoleRepository roleRepository;

    public JwtAuthentication generate(Claims claims) {
        JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(getRoles());
        jwtInfoToken.setUsername(claims.getSubject());
        return jwtInfoToken;
    }

    private Set<Role> getRoles() {
        List<Role> all = roleRepository.findAll();
        return new HashSet<>(all);
    }

}