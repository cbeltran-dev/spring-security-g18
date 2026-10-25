package com.example.security.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


/**
 * <h1>JwtService · LA FABRICA DE TOKENS</h1>
 *
 * <h2>QUE ES</h2>
 *
 * Es la clase que sabe DOS cosas sobre los tokens JWT:
 * <ul>
 *   <li>CREARLOS (cuando alguien hace login correctamente).</li>
 *   <li>VALIDARLOS (en cada peticion que trae un token).</li>
 * </ul>
 *
 * <h2>RECORDATORIO: QUE ES UN JWT</h2>
 *
 * Un JWT (JSON Web Token) tiene tres partes separadas por puntos:
 * <pre>header . payload . signature</pre>
 *
 * El payload lleva datos (quien eres, cuando expira). La signature es el
 * "sello": se calcula con una clave secreta que solo conoce el servidor.
 *
 * <h2>OJO, MUY IMPORTANTE: el token esta FIRMADO, no ENCRIPTADO.</h2>
 *
 * Cualquiera puede LEER el payload (peguenlo en jwt.io y lo veran), pero NADIE
 * puede falsificarlo sin la clave secreta. Por eso: jamas pongan una contrasena
 * ni datos sensibles dentro del token.
 *
 * <h2>ANALOGIA</h2>
 *
 * El token es la pulsera de un concierto: muestras tu DNI una sola vez en la
 * entrada (el login) y te dan la pulsera. Despues solo enseñas la pulsera.
 * Tiene un sello especial que no se puede imitar; si la alteras, no entras.
 */


@Service
public class JwtService {

    @Value("${jwt.expiration-ms}")
    private long expirationMs;
    @Value("${jwt.secret}")
    private String secretBase64;


    /**
     * Convierte la clave de texto (base64) en el objeto SecretKey que jjwt usa
     * para firmar y para verificar. Es la MISMA clave en ambos momentos.
     */
    private SecretKey getKey(){
        byte [] keyBytes = Decoders.BASE64.decode(secretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    };

    /**
     * CREAR el token. Lo llamamos en el login, cuando el usuario ya demostro
     * quien es. Metemos su nombre como "subject", la fecha de emision y la de
     * expiracion, y firmamos con la clave secreta.
     */
    public String generateToken(UserDetails userDetails) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userDetails.getUsername()) // quien es (queda en el payload)
                .issuedAt(ahora)                    // cuando se emitio
                .expiration(expiracion)             // cuando vence
                .signWith(getKey())                 // el sello: lo firma con la clave secreta
                .compact();                         // lo arma en el string final header.payload.signature
    }

    /**
     * Leer el "subject" (el username) que viaja dentro del token.
     * De paso, al parsear, jjwt VERIFICA la firma: si el token fue alterado o
     * la firma no calza, lanza una excepcion aqui mismo.
     */
    public String extractUsername(String token) {

        return parseClaims(token).getSubject();
    }

    /**
     * Decidir si un token es valido para un usuario dado: que el nombre coincida
     * y que NO este vencido.
     */
    public boolean isTokenValido(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpirado(token);
    }


    private boolean isTokenExpirado(String token) {

        return parseClaims(token).getExpiration().before(new Date());
    }

    /**
     * El "motor" de lectura: verifica la firma con nuestra clave y devuelve el
     * contenido (claims) del token. Si la firma es invalida o el token esta
     * corrupto, lanza excepcion (la atrapamos en el filtro).
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())       // verifica el sello con la clave secreta
                .build()
                .parseSignedClaims(token)   // si algo no calza, falla aqui
                .getPayload();              //obtiene la informacion del payload
    }
}
