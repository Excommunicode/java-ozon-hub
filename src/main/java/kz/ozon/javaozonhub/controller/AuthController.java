package kz.ozon.javaozonhub.controller;

import jakarta.security.auth.message.AuthException;
import kz.ozon.javaozonhub.dto.JwtRequest;
import kz.ozon.javaozonhub.dto.JwtResponse;
import kz.ozon.javaozonhub.dto.RefreshJwtRequest;
import kz.ozon.javaozonhub.dto.RegisterRequest;
import kz.ozon.javaozonhub.service.AuthServiceImpl;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("login")
    public ResponseEntity<JwtResponse> login(@RequestBody @NonNull JwtRequest authRequest) throws AuthException {
        final JwtResponse token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody @NonNull RefreshJwtRequest request) throws AuthException {
        final JwtResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody @NonNull RefreshJwtRequest request) throws AuthException {
        final JwtResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("register")
    public ResponseEntity<JwtResponse> register(@RequestBody @NonNull RegisterRequest request) throws AuthException {
        final JwtResponse token = authService.register(request);
        return ResponseEntity.ok(token);
    }
}
