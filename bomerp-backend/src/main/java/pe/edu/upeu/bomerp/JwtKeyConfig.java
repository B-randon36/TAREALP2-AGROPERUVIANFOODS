package pe.edu.upeu.bomerp;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtKeyConfig {

    @Bean
    public RSAKey rsaKey() throws Exception {
        KeyPairGenerator generador = KeyPairGenerator.getInstance("RSA");
        java.security.SecureRandom random = java.security.SecureRandom.getInstance("SHA1PRNG");
        random.setSeed("bomerp-agroperuvian-jwt-secret-seed-2026".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        generador.initialize(2048, random);
        KeyPair par = generador.generateKeyPair();

        return new RSAKey.Builder((RSAPublicKey) par.getPublic())
                .privateKey((RSAPrivateKey) par.getPrivate())
                .keyID("bomerp-fixed-jwt-key-2026")
                .build();
    }

    @Bean
    public JwtEncoder jwtEncoder(RSAKey rsaKey) {
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(rsaKey)));
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey,
                                 @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:http://localhost:38080/realms/agroperuvianfoods}") String issuerUri) throws Exception {
        NimbusJwtDecoder localDecoder = NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
        try {
            JwtDecoder keycloakDecoder = JwtDecoders.fromIssuerLocation(issuerUri);
            return token -> {
                try {
                    return keycloakDecoder.decode(token);
                } catch (Exception e) {
                    return localDecoder.decode(token);
                }
            };
        } catch (Exception e) {
            return localDecoder;
        }
    }
}

