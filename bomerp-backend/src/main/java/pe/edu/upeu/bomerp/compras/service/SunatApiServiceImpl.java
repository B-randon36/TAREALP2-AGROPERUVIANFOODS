package pe.edu.upeu.bomerp.compras.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pe.edu.upeu.bomerp.compras.dto.ConsultaDocumentoResponse;
import pe.edu.upeu.bomerp.compras.dto.TipoCambioResponse;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
public class SunatApiServiceImpl implements SunatApiService {

    private final RestTemplate restTemplate;

    public SunatApiServiceImpl() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(4));
        factory.setReadTimeout(Duration.ofSeconds(4));
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public ConsultaDocumentoResponse consultarDocumento(String tipo, String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            return ConsultaDocumentoResponse.builder()
                    .exito(false)
                    .mensaje("El número de documento no puede estar vacío")
                    .build();
        }

        String numLimpio = numero.trim();
        String tipoDoc = (tipo != null ? tipo.trim().toUpperCase() : (numLimpio.length() == 11 ? "RUC" : "DNI"));

        try {
            if ("RUC".equalsIgnoreCase(tipoDoc)) {
                return consultarRuc(numLimpio);
            } else if ("DNI".equalsIgnoreCase(tipoDoc)) {
                return consultarDni(numLimpio);
            } else {
                return ConsultaDocumentoResponse.builder()
                        .tipoDocumento(tipoDoc)
                        .numeroDocumento(numLimpio)
                        .exito(false)
                        .mensaje("Tipo de documento no soportado para consulta automática. Ingrese datos manualmente.")
                        .build();
            }
        } catch (Exception e) {
            log.warn("Error al consultar API externa para {} {}: {}", tipoDoc, numLimpio, e.getMessage());
            return obtenerRespuestaFallback(tipoDoc, numLimpio);
        }
    }

    private ConsultaDocumentoResponse consultarRuc(String ruc) {
        String url = "https://api.apis.net.pe/v1/ruc?numero=" + ruc;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> body = response.getBody();

        if (body != null && body.containsKey("nombre")) {
            String razonSocial = String.valueOf(body.get("nombre"));
            String direccion = body.get("direccion") != null ? String.valueOf(body.get("direccion")) : "";
            String estado = body.get("estado") != null ? String.valueOf(body.get("estado")) : "ACTIVO";
            String condicion = body.get("condicion") != null ? String.valueOf(body.get("condicion")) : "HABIDO";
            String departamento = body.get("departamento") != null ? String.valueOf(body.get("departamento")) : "";
            String provincia = body.get("provincia") != null ? String.valueOf(body.get("provincia")) : "";
            String distrito = body.get("distrito") != null ? String.valueOf(body.get("distrito")) : "";

            return ConsultaDocumentoResponse.builder()
                    .tipoDocumento("RUC")
                    .numeroDocumento(ruc)
                    .razonSocial(razonSocial)
                    .estado(estado)
                    .condicion(condicion)
                    .direccion(direccion)
                    .departamento(departamento)
                    .provincia(provincia)
                    .distrito(distrito)
                    .exito(true)
                    .mensaje("Datos de SUNAT obtenidos exitosamente")
                    .build();
        }

        return obtenerRespuestaFallback("RUC", ruc);
    }

    private ConsultaDocumentoResponse consultarDni(String dni) {
        String url = "https://api.apis.net.pe/v1/dni?numero=" + dni;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> body = response.getBody();

        if (body != null && body.containsKey("nombre")) {
            String nombre = String.valueOf(body.get("nombre"));
            return ConsultaDocumentoResponse.builder()
                    .tipoDocumento("DNI")
                    .numeroDocumento(dni)
                    .razonSocial(nombre)
                    .estado("ACTIVO")
                    .condicion("HABIDO")
                    .direccion("")
                    .exito(true)
                    .mensaje("Datos de RENIEC obtenidos exitosamente")
                    .build();
        }

        return obtenerRespuestaFallback("DNI", dni);
    }

    @Override
    public TipoCambioResponse obtenerTipoCambioActual() {
        String url = "https://api.apis.net.pe/v1/tipo-cambio-sunat";
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("compra") && body.containsKey("venta")) {
                BigDecimal compra = new BigDecimal(String.valueOf(body.get("compra")));
                BigDecimal venta = new BigDecimal(String.valueOf(body.get("venta")));
                String fecha = body.get("fecha") != null ? String.valueOf(body.get("fecha")) : LocalDate.now().toString();

                return TipoCambioResponse.builder()
                        .origen("SUNAT")
                        .moneda("USD")
                        .fecha(fecha)
                        .compra(compra)
                        .venta(venta)
                        .build();
            }
        } catch (Exception e) {
            log.warn("No se pudo obtener tipo de cambio en tiempo real: {}", e.getMessage());
        }

        // Fallback seguro en caso de corte de red externo
        return TipoCambioResponse.builder()
                .origen("SUNAT (Estimado)")
                .moneda("USD")
                .fecha(LocalDate.now().toString())
                .compra(new BigDecimal("3.7450"))
                .venta(new BigDecimal("3.7550"))
                .build();
    }

    private ConsultaDocumentoResponse obtenerRespuestaFallback(String tipo, String numero) {
        // En caso de modo offline o documento no encontrado
        return ConsultaDocumentoResponse.builder()
                .tipoDocumento(tipo)
                .numeroDocumento(numero)
                .exito(false)
                .mensaje("No se pudo obtener respuesta del servicio de SUNAT/RENIEC. Verifique el número o ingrese los datos manualmente.")
                .build();
    }
}
