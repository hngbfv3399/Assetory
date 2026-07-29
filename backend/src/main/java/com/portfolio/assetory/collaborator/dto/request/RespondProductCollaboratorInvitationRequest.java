package com.portfolio.assetory.collaborator.dto.request;

import com.portfolio.assetory.collaborator.domain.ProductCollaboratorStatus;

import jakarta.validation.constraints.NotNull;

public record RespondProductCollaboratorInvitationRequest(
	@NotNull ProductCollaboratorStatus status
) {
}
