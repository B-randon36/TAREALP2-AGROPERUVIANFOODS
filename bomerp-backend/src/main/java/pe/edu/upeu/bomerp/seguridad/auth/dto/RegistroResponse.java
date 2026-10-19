package pe.edu.upeu.bomerp.seguridad.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistroResponse {
    private Long id;
    private String email;

    public RegistroResponse() {
    }

    public RegistroResponse(Long id, String email) {
        this.id = id;
        this.email = email;
    }
}
