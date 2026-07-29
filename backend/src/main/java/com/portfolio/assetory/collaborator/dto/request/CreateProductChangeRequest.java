package com.portfolio.assetory.collaborator.dto.request;

import tools.jackson.databind.JsonNode;
import com.portfolio.assetory.collaborator.domain.ProductChangeType;

import jakarta.validation.constraints.NotNull;

public record CreateProductChangeRequest(
	@NotNull ProductChangeType type,
	@NotNull JsonNode payload
) {}
