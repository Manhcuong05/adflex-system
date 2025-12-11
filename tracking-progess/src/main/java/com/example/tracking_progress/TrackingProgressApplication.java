package com.example.tracking_progress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
    "com.example.tracking_progress",
    "com.adflex.leadwebhook"
})
// QUAN TRỌNG: Phải thêm dòng này để tìm Repository bên module kia
@EnableJpaRepositories(basePackages = {
    "com.example.tracking_progress",
    "com.adflex.leadwebhook"
})
// QUAN TRỌNG: Phải thêm dòng này để tìm Entity (Table) bên module kia
@EntityScan(basePackages = {
    "com.example.tracking_progress",
    "com.adflex.leadwebhook"
})
@EnableScheduling
public class TrackingProgressApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TrackingProgressApplication.class, args);
    }
}