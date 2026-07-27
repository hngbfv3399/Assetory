package com.portfolio.assetory.category.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.assetory.category.dto.response.CategoryResponse;
import com.portfolio.assetory.category.service.CategoryService;
import com.portfolio.assetory.global.response.ApiResponse;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@GetMapping
	public ApiResponse<List<CategoryResponse>> getCategories() {
		return ApiResponse.success(categoryService.getActiveCategories());
	}
}
