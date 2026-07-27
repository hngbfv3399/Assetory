package com.portfolio.assetory.category.dto.response;

import com.portfolio.assetory.category.domain.Category;

public record CategoryResponse(
	Long id,
	String name
) {
	public static CategoryResponse from(Category category) {
		return new CategoryResponse(category.getId(), category.getName());
	}
}
