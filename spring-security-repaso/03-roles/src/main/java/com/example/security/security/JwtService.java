package com.example.security.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


/**
 * <h1>JwtService · LA FABRICA DE TOKENS (ahora con ROLES adentro)</h1>
 *
 * <h2>QUE CAMBIO RESPECTO AL PROYECTO 2</h2>
 *
 * Antes el token solo guardaba QUIEN eres (el username). Ahora ademas guarda
 * QUE PUEDES HACER: metemos los roles del usuario como un "claim" (un dato
 * dentro del payload del token).
 *
 * <h2>POR QUE ES UTIL</h2>
 *
 * Como los roles viajan dentro del token, en cada peticion NO necesitamos ir a
 * la base de datos para saber que permisos tiene la persona: lo leemos del
 * propio token. Eso es ser "stateless" de verdad.
 *
 * <h2>EL OTRO LADO DE LA MONEDA (para que lo sepan)</h2>
 *
 * Si a un usuario le quitas el rol ADMIN, su token VIEJO seguira diciendo
 * ADMIN hasta que venza. Por eso los tokens duran poco (aqui, 15 min). En el
 * mundo real esto se maneja con tokens cortos y "refresh tokens".
 */


@Service
public class JwtService {

    @Value("${jwt.expiration-ms}")
    private long expirationMs;
    @Value("${jwt.secret}")
    private String secretBase64;


    private SecretKey getKey(){
        byte [] keyBytes = Decoders.BASE64.decode(secretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    };

    /**
     * CREAR el token. Ademas del username, metemos un claim "roles" con las
     * autoridades del usuario (ej: ["ROLE_USER", "ROLE_ADMIN"]).
     */
    public String generateToken(UserDetails userDetails) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expirationMs);

        // Sacamos las autoridades/roles del usuario como una lista de textos.
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", roles) // <-- LO NUEVO: los roles viajan en el token
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(getKey())
                .compact();
    }

    /**
     * Leer el username del token. Al parsear, jjwt verifica la firma Y la
     * expiracion: si el token fue alterado o ya vencio, lanza una excepcion.
     */
    public String extractUsername(String token) {

        return parseClaims(token).getSubject();
    }

    /**
     * Leer los roles guardados en el token. Devuelve una lista de textos como
     * ["ROLE_USER", "ROLE_ADMIN"].
     */
    public List<String> extractRoles(String token) {
        Object raw = parseClaims(token).get("roles");
        if (raw instanceof List<?> lista) {
            return lista.stream().map(Object::toString).toList();
        }
        return List.of();
    }

    /**
     * El motor de lectura: verifica el sello (firma) con nuestra clave y
     * devuelve el contenido. Si la firma no calza o el token vencio, falla aqui.
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
