package com.example.security.controller;

import com.example.security.dto.UserRequest;
import com.example.security.dto.UserResponse;
import com.example.security.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<UserResponse>> listar() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.listar());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/crear")
    public ResponseEntity<UserResponse> crear(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.crear(request));
    }
}
