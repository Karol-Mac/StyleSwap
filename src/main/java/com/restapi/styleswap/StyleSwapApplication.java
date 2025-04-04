package com.restapi.styleswap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
@EnableTransactionManagement
@EnableAspectJAutoProxy
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
}
