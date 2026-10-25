package com.example.security.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, String>> dashboard() {
        return ResponseEntity.status(HttpStatus.OK).body(
                Map.of(
                "mensaje", "Bienvenido admin, esto es informacion sensible",
                "acceso", "concedido"));
    }
}
