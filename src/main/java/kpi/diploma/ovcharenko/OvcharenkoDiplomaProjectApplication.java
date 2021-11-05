package kpi.diploma.ovcharenko;

import kpi.diploma.ovcharenko.service.updloader.IStorageService;
import kpi.diploma.ovcharenko.service.updloader.StorageProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class OvcharenkoDiplomaProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(OvcharenkoDiplomaProjectApplication.class, args);
    }

    @Bean
    CommandLineRunner init(IStorageService storageService) {
        return args -> {
            storageService.deleteAll();
            storageService.init();
        };
    }

}
