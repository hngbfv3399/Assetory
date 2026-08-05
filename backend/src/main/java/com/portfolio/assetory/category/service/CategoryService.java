package com.portfolio.assetory.category.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.assetory.category.dto.response.CategoryResponse;
import com.portfolio.assetory.category.repository.CategoryRepository;

@Service
@Transactional(readOnly = true)
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public List<CategoryResponse> getActiveCategories() {
		return categoryRepository.findByActiveTrueAndParentIsNullOrderBySortOrderAscIdAsc()
			.stream()
			.map(category -> CategoryResponse.from(
				category,
				categoryRepository.findByParentIdAndActiveTrueOrderBySortOrderAscIdAsc(category.getId())
					.stream()
					.map(child -> CategoryResponse.from(child, List.of()))
					.toList()
			))
			.toList();
	}
}
