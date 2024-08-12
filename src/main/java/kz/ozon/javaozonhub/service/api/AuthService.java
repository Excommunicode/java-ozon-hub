package kz.ozon.javaozonhub.service.api;

import jakarta.security.auth.message.AuthException;
import kz.ozon.javaozonhub.dto.RefreshJwtRequest;
import kz.ozon.javaozonhub.jwt.JwtAuthentication;
import kz.ozon.javaozonhub.dto.JwtRequest;
import kz.ozon.javaozonhub.dto.JwtResponse;
import kz.ozon.javaozonhub.dto.RegisterRequest;

public interface AuthService {
    JwtResponse register(RegisterRequest request) throws AuthException;

    JwtResponse login(JwtRequest authRequest) throws AuthException;

    JwtResponse getAccessToken(String refreshToken) throws AuthException;

    JwtResponse refresh(String refreshToken) throws AuthException;

    JwtAuthentication getAuthInfo();

    void logout(RefreshJwtRequest request);
}
