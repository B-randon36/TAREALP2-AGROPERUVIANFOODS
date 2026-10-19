package pe.edu.upeu.bomerp.seguridad.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PerfilResponse {
    private String id;
    private String email;
    private List<String> roles;

    public PerfilResponse() {
    }

    public PerfilResponse(String id, String email, List<String> roles) {
        this.id = id;
        this.email = email;
        this.roles = roles;
    }
}
