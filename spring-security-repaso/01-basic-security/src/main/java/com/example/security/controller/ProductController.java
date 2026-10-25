package com.example.security.controller;

import com.example.security.entity.Product;
import com.example.security.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h4>Controlador de la consulta de productos. La puerta de entrada de la API.</h4>
 *
 * Algo clave para entender la seguridad: este controlador NO sabe nada de
 * autenticacion. No hay ningun "if usuarioLogueado" aqui dentro. Spring
 * Security se ocupa de eso ANTES, en la cadena de filtros. Si la peticion
 * llego hasta este metodo, es porque ya paso el control de seguridad.
 */

@RestController
@RequestMapping("/api/v1/productos")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Product>> listar() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.listar());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Product> buscar(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.buscarPorId(id));
    }

    @PostMapping("/crear")
    public ResponseEntity<Product> crear(@Valid @RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.crear(product));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productService.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
