package pe.edu.upeu.bomerp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ============================================================================
 * CONFIGURACIÓN GLOBAL DE CORS (Cross-Origin Resource Sharing)
 * ============================================================================
 * ¿Por qué existe CORS?
 * CORS es una política de seguridad ejecutada por los NAVEGADORES WEB (Same-Origin Policy).
 * Impide que scripts de un origen (ej. frontend en http://localhost:5173 o app externa)
 * accedan a recursos de otro origen (ej. backend en http://localhost:8080) a menos que
 * el servidor autorice explícitamente el origen mediante cabeceras HTTP específicas.
 *
 * Peticiones Pre-flight (OPTIONS):
 * Para peticiones complejas (JSON con POST/PUT/DELETE o headers custom), el navegador
 * envía primero una solicitud OPTIONS. Esta configuración le indica a Spring MVC
 * responder automáticamente con:
 * - Access-Control-Allow-Origin: <origen>
 * - Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
 * - Access-Control-Allow-Headers: *
 * - Access-Control-Allow-Credentials: true (permite cookies/tokens autenticados)
 * - Access-Control-Max-Age: 3600 (guarda en caché la autorización por 1 hora)
 * ============================================================================
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(allowedOrigins.split(","))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

