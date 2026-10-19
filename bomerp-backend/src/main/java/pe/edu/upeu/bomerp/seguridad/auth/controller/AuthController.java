package pe.edu.upeu.bomerp.seguridad.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.bomerp.seguridad.auth.dto.LoginRequest;
import pe.edu.upeu.bomerp.seguridad.auth.dto.LoginResponse;
import pe.edu.upeu.bomerp.seguridad.auth.dto.PerfilResponse;
import pe.edu.upeu.bomerp.seguridad.auth.dto.RegistroRequest;
import pe.edu.upeu.bomerp.seguridad.auth.dto.RegistroResponse;
import pe.edu.upeu.bomerp.seguridad.auth.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroResponse registro(@Valid @RequestBody RegistroRequest request) {
        return authService.registrar(request);
    }

    @GetMapping("/perfil")
    public PerfilResponse perfil(@AuthenticationPrincipal Jwt jwt) {
        return authService.obtenerPerfilDesdeJwt(jwt);
    }
}
