package com.example.security.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * <h1>JwtFilter · EL PORTERO QUE REVISA LA PULSERA</h1>
 *
 * <h2>QUE ES</h2>
 *
 * Es NUESTRO propio filtro. Se ejecuta en CADA peticion, antes de llegar al
 * controlador. Su trabajo: si la peticion trae un token valido, le dice a
 * Spring "esta persona ya esta autenticada".
 *
 * <h2>POR QUE EXTIENDE OncePerRequestFilter</h2>
 *
 * Porque garantiza que se ejecute UNA sola vez por peticion (sin esa garantia,
 * en algunos casos un filtro podria correr varias veces). Solo tenemos que
 * implementar el metodo doFilterInternal.
 *
 * <h2>COMO ENCAJA EN LA CADENA</h2>
 *
 * En SecurityConfig lo enganchamos con .addFilterBefore(...), para que corra
 * ANTES del filtro estandar de usuario/clave. Asi, cuando Spring llega a
 * decidir si la peticion esta autenticada, nosotros ya pusimos (o no) la
 * identidad en el contexto.
 *
 * <h2>EL FLUJO, PASO A PASO (lo de abajo en codigo):</h2>
 *
 * <ol>
 *   <li>Busca la cabecera Authorization: Bearer &lt;token&gt;.</li>
 *   <li>Si no hay, deja pasar (si la ruta es protegida, terminara en 401).</li>
 *   <li>Si hay, saca el username del token (esto verifica la firma).</li>
 *   <li>Carga el usuario y comprueba que el token sea valido y no este vencido.</li>
 *   <li>Si todo bien, marca la peticion como autenticada en el SecurityContext.</li>
 * </ol>
 */


@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String PREFIJO_BEARER = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. Leemos la cabecera Authorization.
        String authHeader = request.getHeader("Authorization");

        /* 2. Si no viene un token tipo Bearer, no hacemos nada: pasamos al
            siguiente filtro. Si la ruta era protegida, Spring respondera 401.*/
        if (authHeader == null || !authHeader.startsWith(PREFIJO_BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Quitamos el prefijo "Bearer " (7 caracteres) y nos queda el token.
        String token = authHeader.substring(7);
        String username;


        try {
            // Esto lee el username Y de paso verifica la firma del token.
             username = jwtService.extractUsername(token);
        }catch (Exception e){
            // Token corrupto, alterado o vencido -> no autenticamos. Seguimos
            // la cadena; si la ruta es protegida, terminara en 401.
        filterChain.doFilter(request,response);
        return;
        }

        // 4. Si tenemos username y todavia nadie autentico esta peticion...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails usuario = userDetailsService.loadUserByUsername(username);

            // 5. Si el token es valido para ese usuario, lo marcamos como autenticado.
            if (jwtService.isTokenValido(token, usuario)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                usuario,    // el "principal": quien es
                                null,       // credenciales: ya no hacen falta, el token basta
                                usuario.getAuthorities());  // sus roles/permisos
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Aqui dejamos la identidad guardada para el resto de la peticion.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // Continuamos con la cadena de filtros (y eventualmente el controlador).
        filterChain.doFilter(request, response);
    }
}
