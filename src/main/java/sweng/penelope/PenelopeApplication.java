package sweng.penelope;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.crypto.password.PasswordEncoder;

import sweng.penelope.auth.RSAUtils;
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

	@Bean
	KeyPair serverKeyPair() throws NoSuchAlgorithmException {
		return RSAUtils.generateKeys();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(BCryptVersion.$2A, 10);
	}
}
