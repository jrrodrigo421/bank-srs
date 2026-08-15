package com.teste.banco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CrudBancoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrudBancoApplication.class, args);
    }
}
