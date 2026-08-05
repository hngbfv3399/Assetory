package com.portfolio.assetory.global.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.portfolio.assetory.global.auth.CurrentUserIdArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

	public WebConfig(CurrentUserIdArgumentResolver currentUserIdArgumentResolver) {
		this.currentUserIdArgumentResolver = currentUserIdArgumentResolver;
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(currentUserIdArgumentResolver);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
			.allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
			.allowedMethods("GET", "POST", "PATCH", "DELETE", "OPTIONS")
			.allowedHeaders("Authorization", "Content-Type")
			.allowCredentials(true)
			.maxAge(3600);
	}
}
