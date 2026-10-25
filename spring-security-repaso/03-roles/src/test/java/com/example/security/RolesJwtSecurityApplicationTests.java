package com.example.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "jwt.secret=una-clave-de-prueba-de-al-menos-32-caracteres")
class RolesJwtSecurityApplicationTests {

    @Test
    void contextLoads() {
    }
}
