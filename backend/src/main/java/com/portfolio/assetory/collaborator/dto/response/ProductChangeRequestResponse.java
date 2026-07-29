package com.portfolio.assetory.collaborator.dto.response;

import java.time.LocalDateTime;

import tools.jackson.databind.JsonNode;
import com.portfolio.assetory.collaborator.domain.ProductChangeRequest;
import com.portfolio.assetory.collaborator.domain.ProductChangeRequestStatus;
import com.portfolio.assetory.collaborator.domain.ProductChangeType;

public record ProductChangeRequestResponse(
	Long id,
	Long productId,
	Long requesterId,
	String requesterNickname,
	ProductChangeType type,
	JsonNode payload,
	ProductChangeRequestStatus status,
	Long reviewerId,
	LocalDateTime requestedAt,
	LocalDateTime reviewedAt,
	String rejectionReason
) {
	public static ProductChangeRequestResponse from(ProductChangeRequest request, JsonNode payload) {
		return new ProductChangeRequestResponse(request.getId(), request.getProduct().getId(), request.getRequester().getId(),
			request.getRequester().getNickname(), request.getType(), payload, request.getStatus(),
			request.getReviewer() == null ? null : request.getReviewer().getId(), request.getRequestedAt(),
			request.getReviewedAt(), request.getRejectionReason());
	}
}
