package com.portfolio.assetory.category.dto.response;

import java.util.List;

import com.portfolio.assetory.category.domain.Category;

public record CategoryResponse(
	Long id,
	String name,
	List<CategoryResponse> children
) {
	public static CategoryResponse from(Category category, List<CategoryResponse> children) {
		return new CategoryResponse(category.getId(), category.getName(), children);
	}
}
