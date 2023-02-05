package sweng.penelope;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import sweng.penelope.services.StorageService;

@SpringBootApplication
@EnableCaching
public class PenelopeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PenelopeApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		// Initialise storage service
		return args -> storageService.init();
	}
}
