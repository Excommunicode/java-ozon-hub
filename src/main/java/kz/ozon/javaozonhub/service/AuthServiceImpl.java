package kz.ozon.javaozonhub.service;

import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import kz.ozon.javaozonhub.config.JwtAuthentication;
import kz.ozon.javaozonhub.config.JwtProvider;
import kz.ozon.javaozonhub.dto.JwtRequest;
import kz.ozon.javaozonhub.dto.JwtResponse;
import kz.ozon.javaozonhub.dto.RegisterRequest;
import kz.ozon.javaozonhub.exception.NotFoundException;
import kz.ozon.javaozonhub.model.Role;
import kz.ozon.javaozonhub.model.RoleEnum;
import kz.ozon.javaozonhub.model.User;
import kz.ozon.javaozonhub.repository.RoleRepository;
import kz.ozon.javaozonhub.repository.UserRepository;
import kz.ozon.javaozonhub.service.api.AuthService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED)
public class AuthServiceImpl implements AuthService {
    private final Map<String, String> refreshTokenStorage = new HashMap<>();
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public JwtResponse register(RegisterRequest request) throws AuthException {
        if (isLoginExists(request.getLogin())) {
            throw new AuthException("Username is already taken");
        }

        User user = createUser(request);
        userRepository.save(user);

        return generateJwtResponseForUser(user);
    }


    @Override
    public JwtResponse login(JwtRequest authRequest) throws AuthException {
        User user = getUserByLogin(authRequest.getLogin());

        if (isPasswordCorrect(user, authRequest.getPassword())) {
            return generateJwtResponseForUser(user);
        } else {
            throw new AuthException("Incorrect password");
        }
    }

    @Override
    public JwtResponse getAccessToken(String refreshToken) throws AuthException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            String login = getLoginFromRefreshToken(refreshToken);

            if (isRefreshTokenValid(login, refreshToken)) {
                User user = getUserByLogin(login);
                String accessToken = jwtProvider.generateAccessToken(user);
                return new JwtResponse(accessToken, null);
            }
        }

        return new JwtResponse(null, null);
    }

    @Override
    public JwtResponse refresh(String refreshToken) throws AuthException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            String login = getLoginFromRefreshToken(refreshToken);

            if (isRefreshTokenValid(login, refreshToken)) {
                User user = getUserByLogin(login);
                return generateNewJwtResponseForUser(user);
            }
        }

        throw new AuthException("Invalid JWT token");
    }

    @Override
    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean isLoginExists(String login) {
        return userRepository.existsByLogin(login);
    }

    private User createUser(RegisterRequest request)  {
        Role userRole = getUserRole();

        User user = new User();
        user.setLogin(request.getLogin());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRoles(Set.of(userRole));

        return user;
    }

    private Role getUserRole()  {
        String role = RoleEnum.USER.toString();
        return roleRepository.findByName(role)
                .orElseThrow(() -> NotFoundException.builder()
                        .message(String.format("Role with name: %s not found", role))
                        .build());
    }

    private User getUserByLogin(@NonNull String login) throws AuthException {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new AuthException(String.format("User with login: %s not found", login)));
    }

    private boolean isPasswordCorrect(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    private String getLoginFromRefreshToken(String refreshToken) {
        Claims claims = jwtProvider.getRefreshClaims(refreshToken);
        return claims.getSubject();
    }

    private boolean isRefreshTokenValid(String login, String refreshToken) {
        String storedRefreshToken = refreshTokenStorage.get(login);
        return storedRefreshToken != null && storedRefreshToken.equals(refreshToken);
    }

    private JwtResponse generateJwtResponseForUser(User user) {
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        refreshTokenStorage.put(user.getLogin(), refreshToken);
        return new JwtResponse(accessToken, refreshToken);
    }

    private JwtResponse generateNewJwtResponseForUser(User user) {
        String accessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);
        refreshTokenStorage.put(user.getLogin(), newRefreshToken);
        return new JwtResponse(accessToken, newRefreshToken);
    }
}
