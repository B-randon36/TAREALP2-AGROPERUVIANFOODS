package pe.edu.upeu.bomerp.seguridad.auth.service;

import org.springframework.security.oauth2.jwt.Jwt;
import pe.edu.upeu.bomerp.seguridad.auth.dto.LoginRequest;
import pe.edu.upeu.bomerp.seguridad.auth.dto.LoginResponse;
import pe.edu.upeu.bomerp.seguridad.auth.dto.PerfilResponse;
import pe.edu.upeu.bomerp.seguridad.auth.dto.RegistroRequest;
import pe.edu.upeu.bomerp.seguridad.auth.dto.RegistroResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    RegistroResponse registrar(RegistroRequest request);
    PerfilResponse obtenerPerfilDesdeJwt(Jwt jwt);
}
