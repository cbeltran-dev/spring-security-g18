package com.example.security.security;

import com.example.security.entity.UserEntity;
import com.example.security.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * <h1>CustomUserDetailsService · EL PUENTE ENTRE NUESTRA BD Y SPRING SECURITY</h1>
 *
 * <h2>QUÉ ES</h2>
 * <p>
 * En los proyectos anteriores usabamos un UserDetailsService "en memoria".
 * Aqui lo reemplazamos por NUESTRA version, que busca los usuarios en
 * MySQL. Es el unico cambio necesario para que toda la maquinaria de
 * Spring Security (login, validacion de clave, etc.) use la base de datos.
 * </p>
 *
 * <p>
 * Como Spring Security detecta automaticamente cualquier bean que implemente
 * UserDetailsService, con solo marcar esta clase con @Service ya queda en uso.
 * Por eso en SecurityConfig YA NO definimos usuarios a mano.
 * </p>
 *
 * <h2>QUÉ HACE loadUserByUsername</h2>
 * <ol>
 *     <li>Busca el usuario en la BD por su username.</li>
 *     <li>Si no existe, lanza UsernameNotFoundException (terminara en 401).</li>
 *     <li>Si existe, traduce nuestra entidad Usuario al UserDetails que Spring
 *         entiende, convirtiendo el texto de roles ("USER,ADMIN") en autoridades
 *         ("ROLE_USER", "ROLE_ADMIN").</li>
 * </ol>
 */


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        // 1. Buscamos el usuario en la base de datos.
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + username));

        // 2. Convertimos "USER,ADMIN" en autoridades con el prefijo ROLE_.
        //    OJO: hasRole('ADMIN') de Spring busca la autoridad "ROLE_ADMIN".
        //    Por eso aqui agregamos ese prefijo.
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        // 3. Devolvemos el UserDetails que Spring Security sabe manejar.
        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(List.of(authority))
                .build();
    }
}
