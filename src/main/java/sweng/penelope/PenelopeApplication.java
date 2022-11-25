package sweng.penelope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class PenelopeApplication implements WebMvcConfigurer {

	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(PenelopeApplication.class, args);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// Get storage path
		String basePath = environment.getProperty("storage.base-folder") + "/";

		// Handle our static xml files
		registry.addResourceHandler("/xml/**").addResourceLocations("file:" + basePath);
		WebMvcConfigurer.super.addResourceHandlers(registry);
	}
}
