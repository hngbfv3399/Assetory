package com.portfolio.assetory.product.dto.request;

import com.portfolio.assetory.product.domain.ProductResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductResourceRequest(@NotBlank String name, @NotNull ProductResourceType type, @NotBlank String url, String originalName, Long fileSize) {}
