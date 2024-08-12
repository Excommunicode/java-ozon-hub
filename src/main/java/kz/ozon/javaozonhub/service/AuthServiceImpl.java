package kz.ozon.javaozonhub.service;

import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import kz.ozon.javaozonhub.dto.JwtRequest;
import kz.ozon.javaozonhub.dto.JwtResponse;
import kz.ozon.javaozonhub.dto.RefreshJwtRequest;
import kz.ozon.javaozonhub.dto.RegisterRequest;
import kz.ozon.javaozonhub.exception.BadRequestException;
import kz.ozon.javaozonhub.exception.DataConflictException;
import kz.ozon.javaozonhub.exception.NotFoundException;
import kz.ozon.javaozonhub.jwt.JwtAuthentication;
import kz.ozon.javaozonhub.jwt.JwtProvider;
import kz.ozon.javaozonhub.mapper.RefreshTokenMapper;
import kz.ozon.javaozonhub.mapper.RoleMapper;
import kz.ozon.javaozonhub.mapper.UserMapper;
import kz.ozon.javaozonhub.model.RefreshToken;
import kz.ozon.javaozonhub.model.Role;
import kz.ozon.javaozonhub.model.User;
import kz.ozon.javaozonhub.repository.RefreshTokenRepository;
import kz.ozon.javaozonhub.repository.RoleRepository;
import kz.ozon.javaozonhub.repository.UserRepository;
import kz.ozon.javaozonhub.service.api.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import static kz.ozon.javaozonhub.constant.Constant.REFRESH_TOKEN_EXPIRATION_DAYS;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED)
public class AuthServiceImpl implements AuthService, UserDetailsService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleMapper roleMapper;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    @Override
    @Transactional
    public JwtResponse register(RegisterRequest request) throws AuthException {
        log.info("Registering user with login: {}", request.getLogin());

        if (isLoginExists(request.getLogin())) {
            log.warn("Username {} is already taken", request.getLogin());
            throw new AuthException("Username is already taken");
        }

        isEmailExists(request.getEmail());

        User user = userMapper.toUserEntity(request, passwordEncoder, Set.of(roleMapper.toRoleDto(getUserRole())));

        User savedUser = userRepository.save(user);
        log.info("User {} registered successfully", savedUser.getLogin());

        return generateJwtResponseForUser(savedUser);
    }

    @Override
    @Transactional
    public JwtResponse login(JwtRequest authRequest) throws AuthException {
        log.info("User {} attempting to log in", authRequest.getLogin());

        User user = (User) loadUserByUsername(authRequest.getLogin());

        if (!isPasswordCorrect(user, authRequest.getPassword())) {
            log.warn("Incorrect password for user {}", authRequest.getLogin());
            throw new AuthException("Incorrect password");
        }

        log.info("User {} logged in successfully", user.getLogin());
        return generateJwtResponseForUser(user);
    }

    @Override
    public JwtResponse getAccessToken(String refreshToken) {
        log.info("Generating access token for refresh token");

        if (jwtProvider.validateRefreshToken(refreshToken)) {
            String login = getLoginFromRefreshToken(refreshToken);

            if (isRefreshTokenValid(login, refreshToken)) {
                User user = (User) loadUserByUsername(login);
                String accessToken = jwtProvider.generateAccessToken(user);
                log.info("Access token generated for user {}", login);
                return new JwtResponse(accessToken, null);
            }
        }

        log.warn("Failed to generate access token: Invalid refresh token");
        return new JwtResponse(null, null);
    }

    @Override
    public JwtResponse refresh(String refreshToken) throws AuthException {
        log.info("Refreshing JWT token");

        if (jwtProvider.validateRefreshToken(refreshToken)) {
            String login = getLoginFromRefreshToken(refreshToken);

            if (isRefreshTokenValid(login, refreshToken)) {
                User user = (User) loadUserByUsername(login);
                log.info("JWT token refreshed for user {}", login);
                return generateNewJwtResponseForUser(user);
            }
        }

        log.warn("Failed to refresh JWT token: Invalid refresh token");
        throw new AuthException("Invalid JWT token");
    }

    @Override
    public JwtAuthentication getAuthInfo() {
        JwtAuthentication authInfo = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        log.info("Retrieved authentication info for user {}", authInfo.getName());
        return authInfo;
    }

    @Override
    @Transactional
    public void logout(RefreshJwtRequest request) {
        String refreshToken = request.getRefreshToken();
        int removed = refreshTokenRepository.deleteByToken(refreshToken);

        if (removed == 0) {
            log.error("Attempt to logout with invalid token: {}", refreshToken);
            throw BadRequestException.builder()
                    .message(String.format("Token: %s not valid",refreshToken))
                    .build();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        return userRepository.findByLogin(username)
                .orElseThrow(() -> {
                    log.warn("User with login: {} not found", username);
                    return new UsernameNotFoundException(String.format("User with login: %s not found", username));
                });
    }

    private boolean isLoginExists(String login) {
        return userRepository.existsByLogin(login);
    }

    private void isEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw DataConflictException.builder()
                    .message(String.format("Email: %s already be taken", email))
                    .build();
        }

    }

    private Role getUserRole() {
        String role = "USER";
        return roleRepository.findByName(role)
                .orElseThrow(() -> NotFoundException.builder()
                        .message(String.format("Role with name: %s not found", role))
                        .build());
    }

    private boolean isPasswordCorrect(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    private String getLoginFromRefreshToken(String refreshToken) {
        Claims claims = jwtProvider.getRefreshClaims(refreshToken);
        return claims.getSubject();
    }

    private boolean isRefreshTokenValid(String login, String refreshToken) {
        RefreshToken refreshToken1 = refreshTokenRepository.findByUserLogin(login).orElse(null);
        String token = Objects.requireNonNull(refreshToken1).getToken();
        return token != null && token.equals(refreshToken);
    }

    private JwtResponse generateJwtResponseForUser(User user) {
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireDate = now.plusDays(REFRESH_TOKEN_EXPIRATION_DAYS);


        RefreshToken mapperRefreshToken = refreshTokenMapper.createRefreshToken(user.getId(), refreshToken, expireDate, now, false);

        refreshTokenRepository.save(mapperRefreshToken);
        return new JwtResponse(accessToken, refreshToken);
    }

    private JwtResponse generateNewJwtResponseForUser(User user) {
        String newAccessToken = jwtProvider.generateAccessToken(user);

        String newRefreshToken = jwtProvider.generateRefreshToken(user);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireDate = now.plusDays(REFRESH_TOKEN_EXPIRATION_DAYS);

        RefreshToken mapperRefreshToken = refreshTokenMapper.createRefreshToken(user.getId()    , newRefreshToken, expireDate, now, false);

        refreshTokenRepository.save(mapperRefreshToken);
        return new JwtResponse(newAccessToken, newRefreshToken);
    }
}
