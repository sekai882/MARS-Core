package com.mars.core;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptTest {
    @Test
    public void generateHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("====== HASH FOR PASSWORD ======");
        System.out.println(encoder.encode("password"));
        System.out.println("===============================");
    }
}
