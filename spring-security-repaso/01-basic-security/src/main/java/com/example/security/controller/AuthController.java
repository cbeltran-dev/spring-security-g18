package com.example.security.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;


/**

 * <h3>AuthController  ·  PARA "VER" QUE LA AUTENTICACION FUNCIONA </h3>


 * <p>Este controlador no tiene logica de negocio: existe solo con fines
 * didacticos. Expone un endpoint /me que devuelve QUIEN sos segun Spring
 * Security. Es la forma mas clara de comprobar en vivo que la autenticacion
 * funciono: si entran sin credenciales, reciben 401; si entran con un usuario
 * valido, este endpoint les dice su nombre y sus roles.</p>
 *
 * <p>DE DONDE SALE EL OBJETO Authentication?</p>
 *
 * <p>No lo creamos nosotros. Cuando la peticion supera la cadena de filtros de
 * Spring Security, Security deja guardada la identidad del usuario en un lugar
 * llamado SecurityContext. Al poner "Authentication authentication" como
 * parametro del metodo, Spring nos lo inyecta automaticamente ya resuelto.
 * Si llegamos hasta aqui, es porque ya estamos autenticados.</p>
 *
 */



@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @GetMapping("/me")
    public Map<String, Object> quienSoy(Authentication authentication) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("usuario", authentication.getName());          // el username del que se logueo
        info.put("roles", authentication.getAuthorities());     // sus roles/permisos
        info.put("autenticado", authentication.isAuthenticated());
        return info;
    }
}
