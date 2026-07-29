package com.portfolio.assetory.collaborator.dto.response;

import java.time.LocalDateTime;

import com.portfolio.assetory.collaborator.domain.ProductCollaborator;
import com.portfolio.assetory.collaborator.domain.ProductCollaboratorRole;
import com.portfolio.assetory.collaborator.domain.ProductCollaboratorStatus;

public record ProductCollaboratorResponse(
	Long id,
	Long userId,
	String email,
	String nickname,
	ProductCollaboratorRole role,
	ProductCollaboratorStatus status,
	LocalDateTime invitedAt,
	LocalDateTime respondedAt,
	LocalDateTime removedAt
) {
	public static ProductCollaboratorResponse from(ProductCollaborator collaborator) {
		return new ProductCollaboratorResponse(
			collaborator.getId(),
			collaborator.getUser().getId(),
			collaborator.getUser().getEmail(),
			collaborator.getUser().getNickname(),
			collaborator.getRole(),
			collaborator.getStatus(),
			collaborator.getInvitedAt(),
			collaborator.getRespondedAt(),
			collaborator.getRemovedAt()
		);
	}
}
