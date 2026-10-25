package com.example.security.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * <h1>SecurityConfig · AUTENTICACION + AUTORIZACION</h1>
 *
 * <h2>QUE CAMBIO RESPECTO AL PROYECTO 2</h2>
 *
 * <ol>
 *   <li>Ya NO definimos usuarios en memoria aqui. Ahora salen de la base de
 *       datos a traves de CustomUserDetailsService (que Spring detecta solo por
 *       ser un @Service que implementa UserDetailsService).</li>
 *   <li>Agregamos @EnableMethodSecurity para poder usar @PreAuthorize en los
 *       metodos de los controladores (autorizacion "por metodo").</li>
 *   <li>Agregamos una regla de autorizacion "por ruta": /api/v1/admin/** solo
 *       para ADMIN.</li>
 * </ol>
 *
 * <h2>LAS DOS FORMAS DE AUTORIZAR (las dos aparecen en este proyecto)</h2>
 *
 * <ul>
 *   <li>POR RUTA -> aqui, con requestMatchers(...).hasRole("ADMIN").</li>
 *   <li>POR METODO -> en el controlador, con @PreAuthorize("hasRole('ADMIN')").</li>
 * </ul>
 *
 * <h2>RECORDATORIO 401 vs 403:</h2>
 *
 * <ul>
 *   <li>401 Unauthorized = no sabemos quien eres (fallo la AUTENTICACION).</li>
 *   <li>403 Forbidden = sabemos quien eres, pero no tienes permiso (fallo la
 *       AUTORIZACION).</li>
 * </ul>
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/public/**").permitAll()
                        // Publico: el login (para poder conseguir el token).
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        // AUTORIZACION POR RUTA: la zona de administracion, solo ADMIN.
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

//                        .requestMatchers("/api/v1/usuarios/crear").hasRole("ADMIN")
//                        .requestMatchers("api/v1/productos/eliminar/**").hasRole("ADMIN")

                        // Todo lo demas: basta con estar autenticado (USER o ADMIN).
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * EL AuthenticationManager.
     * Es el componente que sabe validar un usuario+clave contra el
     * UserDetailsService y el PasswordEncoder. Lo exponemos como bean para
     * poder inyectarlo en el AuthService y usarlo en el login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * El PasswordEncoder sigue siendo BCrypt. Lo usa el login (para comparar)
     *
     * NOTA: ya NO hay un bean de usuarios aqui. Esos los provee
     * CustomUserDetailsService leyendo de la base de datos.
     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
