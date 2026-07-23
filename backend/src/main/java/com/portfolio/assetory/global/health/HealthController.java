package com.portfolio.assetory.global.health;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.assetory.global.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

	@GetMapping
	public ApiResponse<Map<String, String>> health() {
		return ApiResponse.success(Map.of("status", "ok"));
	}
}
