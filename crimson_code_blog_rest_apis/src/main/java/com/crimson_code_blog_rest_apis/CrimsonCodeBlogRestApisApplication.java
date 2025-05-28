package com.crimson_code_blog_rest_apis;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CrimsonCodeBlogRestApisApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrimsonCodeBlogRestApisApplication.class, args);
	}

	@Bean
	ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
