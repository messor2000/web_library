package kpi.diploma.ovcharenko;

import kpi.diploma.ovcharenko.service.updloader.StorageService;
import kpi.diploma.ovcharenko.service.updloader.StorageProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@EnableSpringDataWebSupport
public class OvcharenkoDiplomaProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(OvcharenkoDiplomaProjectApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return args -> {
            storageService.deleteAll();
            storageService.init();
        };
    }
}
