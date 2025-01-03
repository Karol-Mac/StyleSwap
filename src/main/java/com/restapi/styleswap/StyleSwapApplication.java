package com.restapi.styleswap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
@EnableTransactionManagement
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL_FORMS)
public class StyleSwapApplication {
	@Autowired
	private DataSource dataSource;

	@Value("${spring.datasource.data}")
	private String fileName;

	public static void main(String[] args) {
		SpringApplication.run(StyleSwapApplication.class, args);
	}

	@Bean
	public CommandLineRunner run() {
		return args -> {
			try (Connection connection = dataSource.getConnection()) {
				Resource resource = new ClassPathResource(fileName);
				ScriptUtils.executeSqlScript(connection, resource);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**") // określ endpointy, które mają obsługiwać CORS
						.allowedOrigins("http://localhost:5173")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // określ dozwolone metody
						.allowedHeaders("*") // określ dozwolone nagłówki
						.allowCredentials(true); // pozwala na wysyłanie ciasteczek (jeśli potrzebujesz)
			}
		};
	}
}
