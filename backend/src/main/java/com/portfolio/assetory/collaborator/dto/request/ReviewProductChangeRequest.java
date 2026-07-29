package com.portfolio.assetory.collaborator.dto.request;

import com.portfolio.assetory.collaborator.domain.ProductChangeRequestStatus;

import jakarta.validation.constraints.NotNull;

public record ReviewProductChangeRequest(
	@NotNull ProductChangeRequestStatus status,
	String rejectionReason
) {}
