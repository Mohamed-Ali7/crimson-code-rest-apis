package com.crimson_code_blog_rest_apis;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements  WebMvcConfigurer{

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/profile_pictures/**")
		.addResourceLocations("file:uploads/profile_pictures/");
		
		registry.addResourceHandler("/images/post_image/**")
		.addResourceLocations("file:uploads/post_image/");
	}

}
