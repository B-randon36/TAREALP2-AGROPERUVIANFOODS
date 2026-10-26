package pe.edu.upeu.bomerp.compras.service;

import pe.edu.upeu.bomerp.compras.dto.ConsultaDocumentoResponse;
import pe.edu.upeu.bomerp.compras.dto.TipoCambioResponse;

public interface SunatApiService {
    ConsultaDocumentoResponse consultarDocumento(String tipo, String numero);
    TipoCambioResponse obtenerTipoCambioActual();
}
