package com.gabrielczar.uplouddownload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@SpringBootApplication
public class UploudDownloadApplication {

    public static void main(String[] args) {
        SpringApplication.run(UploudDownloadApplication.class, args);
    }

    @Bean
    public StorageService storageService() {
        return new StorageService("data");
    }

}
