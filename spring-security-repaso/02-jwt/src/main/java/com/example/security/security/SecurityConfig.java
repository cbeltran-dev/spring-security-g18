package com.example.security.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
/**
 * <h1>SecurityConfig · EL REGLAMENTO DE SEGURIDAD (ahora con JWT)</h1>
 *
 * <h2>QUE CAMBIO RESPECTO AL PROYECTO 1</h2>
 *
 * En el Proyecto 1 usabamos HTTP Basic: mandar usuario y clave en CADA
 * peticion. Aqui pasamos a JWT, y eso trae cuatro cambios en la cadena:
 *
 * <ol>
 *   <li>La sesion ahora es STATELESS: el servidor NO guarda sesiones. La unica
 *       "prueba" de quien eres es el token que viaja en cada peticion.</li>
 *   <li>Quitamos httpBasic. Ya no se pide usuario/clave en cada llamada.</li>
 *   <li>Dejamos PUBLICA la ruta /api/v1/auth/login (sin ella no podrian
 *       loguearse, seria un callejon sin salida).</li>
 *   <li>Enganchamos NUESTRO filtro (JwtAuthenticationFilter) ANTES del filtro
 *       estandar, para que valide el token y marque la peticion como autenticada.</li>
 * </ol>
 *
 * Las piezas UserDetailsService y PasswordEncoder siguen igual que en el
 * Proyecto 1. Lo nuevo es ademas exponer el AuthenticationManager, que el
 * login usara para validar usuario y clave.
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * LA CADENA DE FILTROS, adaptada a JWT.
     * Recibe nuestro JwtFilter por inyeccion (es un @Component).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
                // API REST sin estado: CSRF no aplica.
                .csrf(csrf -> csrf.disable())
                /* STATELESS: no se crean ni usan sesiones HTTP. Cada peticion se
                 valída sola, por su token. Este es el corazon del enfoque con JWT.*/
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Que se protege y que es publico:
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/public/**").permitAll()
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .anyRequest().authenticated()
                )
//                .exceptionHandling(ex -> ex
//                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//                )
                /* Enganchamos nuestro filtro ANTES del de usuario/clave de Spring.
                 Asi, cuando Spring evalua si la peticion esta autenticada, nosotros
                 ya leimos el token y (si era valido) marcamos la identidad.*/
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    /**
     * El AuthenticationManager.
     * <p>
     * Es el componente que sabe validar un usuario+clave contra el
     * UserDetailsService y el PasswordEncoder. Lo exponemos como bean para
     * poder inyectarlo en el AuthService y usarlo en el login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * DE DONDE SALEN LOS USUARIOS (igual que en el Proyecto 1: en memoria).
     * En el Proyecto 3 los moveremos a una base de datos.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("user123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    /**
     * COMO SE PROTEGEN LAS CONTRASENAS (BCrypt, igual que en el Proyecto 1).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
