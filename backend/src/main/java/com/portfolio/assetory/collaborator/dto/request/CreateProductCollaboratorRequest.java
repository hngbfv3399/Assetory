package com.portfolio.assetory.collaborator.dto.request;

import jakarta.validation.constraints.Positive;

import com.portfolio.assetory.collaborator.domain.ProductCollaboratorRole;

public record CreateProductCollaboratorRequest(
	@jakarta.validation.constraints.NotNull @Positive Long userId,
	ProductCollaboratorRole role
) {
}
