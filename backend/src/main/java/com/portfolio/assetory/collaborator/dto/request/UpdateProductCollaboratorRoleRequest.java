package com.portfolio.assetory.collaborator.dto.request;

import com.portfolio.assetory.collaborator.domain.ProductCollaboratorRole;

import jakarta.validation.constraints.NotNull;

public record UpdateProductCollaboratorRoleRequest(@NotNull ProductCollaboratorRole role) {}
