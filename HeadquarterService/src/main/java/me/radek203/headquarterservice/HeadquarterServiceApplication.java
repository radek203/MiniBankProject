package me.radek203.headquarterservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HeadquarterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeadquarterServiceApplication.class, args);
    }

}
