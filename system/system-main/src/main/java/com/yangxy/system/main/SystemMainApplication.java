package com.yangxy.system.main;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class SystemMainApplication implements CommandLineRunner {

    @Value("${test.value}")
    private String testValue;

    public static void main(String[] args) {
        SpringApplication.run(SystemMainApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("test.value: " + testValue);
    }
}
