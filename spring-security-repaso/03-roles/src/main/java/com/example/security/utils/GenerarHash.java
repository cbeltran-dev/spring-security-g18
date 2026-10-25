package com.example.security.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;

public class GenerarHash {

    public static void main(String[] args) {
        System.out.println("ADMIN: " + new BCryptPasswordEncoder().encode("admin123"));
        System.out.println("USER: " + new BCryptPasswordEncoder().encode("user123"));

//        SecretKey key = Jwts.SIG.HS256.key().build();
//        String secretBase64 = Encoders.BASE64.encode(key.getEncoded());
//        System.out.println(secretBase64);
    }

}
