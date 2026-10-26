package pe.edu.upeu.bomerp.produccion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletarOrdenRequest {
    private String observaciones;
    private LocalDate fechaVencimientoPersonalizada; // opcional
}
