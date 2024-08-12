package kz.ozon.javaozonhub.controller;

import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import kz.ozon.javaozonhub.dto.JwtRequest;
import kz.ozon.javaozonhub.dto.JwtResponse;
import kz.ozon.javaozonhub.dto.RefreshJwtRequest;
import kz.ozon.javaozonhub.dto.RegisterRequest;
import kz.ozon.javaozonhub.service.api.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) throws AuthException {
        JwtResponse token = authService.register(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody JwtRequest authRequest) throws AuthException {
        JwtResponse token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@Valid @RequestBody RefreshJwtRequest request) throws AuthException {
        JwtResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@Valid @RequestBody RefreshJwtRequest request) throws AuthException {
        JwtResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(@RequestBody RefreshJwtRequest request) {
        authService.logout(request);
    }
}
