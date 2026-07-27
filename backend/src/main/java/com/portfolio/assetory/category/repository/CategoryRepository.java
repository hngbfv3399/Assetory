package com.portfolio.assetory.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portfolio.assetory.category.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	List<Category> findByActiveTrueOrderBySortOrderAscIdAsc();

	Optional<Category> findByName(String name);

	Optional<Category> findByIdAndActiveTrue(Long id);
}
