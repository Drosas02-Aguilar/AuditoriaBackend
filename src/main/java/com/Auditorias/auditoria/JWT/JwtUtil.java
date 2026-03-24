package com.Auditorias.auditoria.JWT;

import com.Auditorias.auditoria.Entity.ServiceResult;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.*;
import java.security.Key;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {

    ServiceResult serviceResult = new ServiceResult();

    private static final String secret = "MiClaveSecretaSuperSeguraParaJWT2024XYZ123456789";
    private static final long expiration = 86400000L;

    private Key ObtenerKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String GenerarToken(String correo, String rol) {
        return Jwts.builder()
                .setSubject(correo)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(ObtenerKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    String ObtenerCorreo(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(ObtenerKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean ValidarToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(ObtenerKey())
                    .build()
                    .parseClaimsJws(token);
            return true;

        } catch (Exception ex) {
            serviceResult.ErrorMessage = ex.getLocalizedMessage();
            return false;

        }

    }

}
