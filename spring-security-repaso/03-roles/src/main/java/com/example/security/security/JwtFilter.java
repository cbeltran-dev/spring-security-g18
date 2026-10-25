package com.example.security.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * <h1>JwtAuthenticationFilter · EL PORTERO QUE REVISA LA PULSERA (y sus permisos)</h1>
 *
 * <h2>QUE CAMBIO RESPECTO AL PROYECTO 2</h2>
 *
 * Antes, en cada peticion, ibamos a la base de datos a cargar el usuario para
 * conocer sus roles. Ahora NO hace falta: los roles viajan DENTRO del token.
 * Asi que aqui:
 *
 * <ul>
 *   <li>leemos el username y los roles directamente del token,</li>
 *   <li>construimos las autoridades con esos roles,</li>
 *   <li>y marcamos la peticion como autenticada.</li>
 * </ul>
 *
 * <p>Esto es ser "stateless": no tocamos la base de datos en cada llamada.</p>
 *
 * <p>Detalle clave: extraerUsername(token) PARSEA el token, y al parsear jjwt ya
 * verifica la firma y la expiracion. Si el token es invalido o vencio, lanza
 * excepcion y caemos al catch (la peticion seguira sin autenticar -> 401).</p>
 */


@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {



    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Sin token Bearer: dejamos pasar (si la ruta es protegida -> 401).
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        String username;
        List<String> roles;
        try {
            // Estas dos lecturas verifican la firma y la expiracion del token.
             username = jwtService.extractUsername(token);
             roles = jwtService.extractRoles(token);
        }catch (Exception e){
            // Token corrupto, alterado o vencido -> no autenticamos. Seguimos.
            filterChain.doFilter(request,response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Construimos las autoridades a partir de los roles obtenidos DEL TOKEN.
            // (No vamos a la base de datos: el token ya trae todo lo necesario.)
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,               // el principal: el nombre del usuario
                            null,                   // credenciales: ya no hacen falta
                            authorities);           // sus permisos, sacados del token

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
