package com.example.security.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;


/**

 * <h1>EL CENTRO DE TODO EL PROYECTO </h1>
 *
 * <h2>QUE ES SPRING SECURITY (en una idea)</h2>
 *
 * <p>Spring Security se mete EN MEDIO de cada peticion que entra a la aplicacion,
 * ANTES de que llegue a nuestros controllers. Lo hace con una "cadena de filtros"
 * (la famosa filter chain). Piensalo como los controles de un aeropuerto: antes
 * de subir al avion (el controller) pasan por varios checkpoints: documento,
 * seguridad, puerta de embarque. Si fallan en uno, no avanzan al siguiente.</p>
 *
 * <p>Apenas agregamos la dependencia de Spring Security al pom, TODA la app queda
 * protegida por defecto y Spring genera un usuario "user" con una clave aleatoria
 * que imprime en la consola al arrancar. Nuestro trabajo en este proyecto es tomar
 * el control de esa configuracion: decir QUE se abre, QUE se protege y QUIENES
 * son los usuarios validos.</p>
 *
 * <h2>AUTENTICACION vs AUTORIZACION (no confundir)</h2>
 *
 * <ul>
 *   <li>Autenticacion = "quien eres?". Muestras tu usuario y clave (tu DNI en la puerta).</li>
 *   <li>Autorizacion = "que puedes hacer?". Tu rol abre unas puertas y otras no.</li>
 * </ul>
 *
 * <p>En ESTE proyecto solo trabajamos la AUTENTICACION: si estas logueado, pasas.</p>
 *
 * <h2>LAS TRES PIEZAS QUE DEFINIMOS AQUI</h2>
 *
 * <ol>
 *   <li>SecurityFilterChain -> las REGLAS: que rutas son publicas y cuales protegidas.</li>
 *   <li>UserDetailsService -> los USUARIOS: de donde salen (aqui, de la memoria).</li>
 *   <li>PasswordEncoder -> como se GUARDAN las claves (cifradas con BCrypt, nunca en texto plano).</li>
 * </ol>
 *
 * <p>@Configuration: le dice a Spring que esta clase define beans de configuracion.</p>
 *
 * <p>@EnableWebSecurity: enciende la seguridad web y nos deja personalizarla.</p>
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * PIEZA 1: LAS REGLAS DE ACCESO (la filter chain).
     *<p>
     * Este bean es el que decide, para cada peticion, si pasa o no y como debe
     * autenticarse. Lo configuramos con el "Lambda DSL" de Spring Security 6:
     * una forma de escribir las reglas con lambdas, encadenadas y legibles.
     */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                /* --- CSRF ---
                 CSRF es una proteccion pensada para apps web con formularios y sesiones
                 en el navegador. Nuestra API es stateless y la consumen Postman, curl,
                 apps moviles u otros servicios (no un formulario de navegador), asi que
                 la desactivamos. Si no lo hicieramos, las peticiones POST darian 403.*/
                .csrf(csrf -> csrf.disable())
                /* --- REGLAS DE AUTORIZACION ---
                Aqui decidimos que se abre y que se protege. MUY IMPORTANTE: las reglas
                se evaluan de ARRIBA hacia ABAJO y gana la PRIMERA que coincide. Por eso
                las reglas especificas van primero y la general (anyRequest) va al final.*/
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/public/**").permitAll() // ruta pública, a propósito, para comparar
                        .anyRequest().authenticated() // todo lo demás exige autenticación
                )
                /* --- COMO SE AUTENTICAN ---
                httpBasic = autenticacion "HTTP Basic": el cliente manda usuario y clave
                en la cabecera Authorization en CADA peticion. Es la forma mas simple de
                probar seguridad. En el Proyecto 2 se reemplazará por un JWT
                para no mandar usuario y clave en cada peticion.*/
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * PIEZA 2: LOS USUARIOS (de donde salen).
     *<p>
     * UserDetailsService es la interface que Spring Security usa para BUSCAR un
     * usuario por su nombre cuando alguien intenta loguearse. Aqui usamos su
     * implementacion mas simple, InMemoryUserDetailsManager, que guarda los
     * usuarios EN MEMORIA (en una lista, dentro del programa).
     *<p>
     * Esto es a proposito para esta primera clase: queremos entender Security sin
     * meter todavia una base de datos. En el Proyecto 2 o 3 sacaremos los usuarios
     * de una base de datos. Una cosa nueva a la vez.
     *<p>
     * Detalle importante: en cuanto definimos este bean, Spring DEJA de generar
     * aquel usuario "user" con clave aleatoria. A partir de ahora, los usuarios
     * validos son EXACTAMENTE estos dos.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

        // Usuario normal. Fijarse que la clave NO se guarda como "alumno123":
        // la pasamos por passwordEncoder.encode(...) para guardarla CIFRADA con BCrypt.
        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("user123"))
                .roles("USER")
                .build();

        // Un segundo usuario con rol ADMIN. Por ahora el rol NO cambia nada
        // (cualquiera logueado entra igual), pero lo dejamos listo para cuando
        // hagamos autorizacion por roles mas adelante.
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        // Los registramos en el "almacen en memoria". Spring buscara aqui cuando
        // alguien intente autenticarse.
        return new InMemoryUserDetailsManager(admin, user);
    }

    /**
     * PIEZA 3: EL CIFRADOR DE CLAVES.
     *<p>
     * REGLA DE ORO DE SEGURIDAD: las contrasenas JAMAS se guardan en texto plano.
     * Se guardan "hasheadas", es decir, transformadas en un texto irreversible.
     * BCrypt es el algoritmo estandar para esto. Ademas, BCrypt agrega "sal"
     * (un valor aleatorio), por eso dos personas con la misma clave terminan con
     * hashes distintos.
     *<p>
     * Cuando alguien hace login, Spring toma la clave que escribio, le aplica el
     * MISMO BCrypt y compara el resultado con el hash guardado. Nunca "descifra"
     * la clave (no se puede): solo compara hashes.
     *<p>
     * Este mismo bean lo usa el UserDetailsService de arriba para cifrar las claves
     * al crear los usuarios.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
