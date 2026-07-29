package com.portfolio.assetory.collaborator.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.assetory.collaborator.dto.request.CreateProductCollaboratorRequest;
import com.portfolio.assetory.collaborator.dto.request.RespondProductCollaboratorInvitationRequest;
import com.portfolio.assetory.collaborator.dto.request.CreateProductChangeRequest;
import com.portfolio.assetory.collaborator.dto.request.ReviewProductChangeRequest;
import com.portfolio.assetory.collaborator.dto.request.UpdateProductCollaboratorRoleRequest;
import com.portfolio.assetory.collaborator.dto.response.ProductCollaboratorResponse;
import com.portfolio.assetory.collaborator.dto.response.ProductChangeRequestResponse;
import com.portfolio.assetory.collaborator.service.ProductCollaboratorService;
import com.portfolio.assetory.collaborator.service.ProductChangeRequestService;
import com.portfolio.assetory.global.auth.CurrentUserId;
import com.portfolio.assetory.global.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/seller")
public class ProductCollaboratorController {
	private final ProductCollaboratorService collaboratorService;
	private final ProductChangeRequestService changeRequestService;

	public ProductCollaboratorController(ProductCollaboratorService collaboratorService, ProductChangeRequestService changeRequestService) {
		this.collaboratorService = collaboratorService;
		this.changeRequestService = changeRequestService;
	}

	@PostMapping("/products/{productId}/collaborators")
	public ResponseEntity<ApiResponse<ProductCollaboratorResponse>> invite(
		@CurrentUserId Long ownerId,
		@PathVariable Long productId,
		@Valid @RequestBody CreateProductCollaboratorRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(collaboratorService.invite(ownerId, productId, request.userId(), request.role())));
	}

	@GetMapping("/products/{productId}/collaborators")
	public ResponseEntity<ApiResponse<List<ProductCollaboratorResponse>>> getCollaborators(@CurrentUserId Long ownerId, @PathVariable Long productId) {
		return ResponseEntity.ok(ApiResponse.success(collaboratorService.getCollaborators(ownerId, productId)));
	}

	@PatchMapping("/collaborator-invitations/{collaboratorId}")
	public ResponseEntity<ApiResponse<ProductCollaboratorResponse>> respond(
		@CurrentUserId Long userId,
		@PathVariable Long collaboratorId,
		@Valid @RequestBody RespondProductCollaboratorInvitationRequest request
	) {
		return ResponseEntity.ok(ApiResponse.success(collaboratorService.respond(userId, collaboratorId, request.status())));
	}

	@DeleteMapping("/products/{productId}/collaborators/{collaboratorId}")
	public ResponseEntity<ApiResponse<Void>> remove(@CurrentUserId Long ownerId, @PathVariable Long productId, @PathVariable Long collaboratorId) {
		collaboratorService.remove(ownerId, productId, collaboratorId);
		return ResponseEntity.ok(ApiResponse.success("공동 작업자가 제거되었습니다.", null));
	}

	@PatchMapping("/products/{productId}/collaborators/{collaboratorId}/role")
	public ResponseEntity<ApiResponse<ProductCollaboratorResponse>> changeRole(@CurrentUserId Long ownerId, @PathVariable Long productId,
		@PathVariable Long collaboratorId, @Valid @RequestBody UpdateProductCollaboratorRoleRequest request) {
		return ResponseEntity.ok(ApiResponse.success(collaboratorService.changeRole(ownerId, productId, collaboratorId, request.role())));
	}

	@PostMapping("/products/{productId}/change-requests")
	public ResponseEntity<ApiResponse<ProductChangeRequestResponse>> createChangeRequest(@CurrentUserId Long userId, @PathVariable Long productId,
		@Valid @RequestBody CreateProductChangeRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(changeRequestService.create(userId, productId, request)));
	}

	@GetMapping("/products/{productId}/change-requests")
	public ResponseEntity<ApiResponse<List<ProductChangeRequestResponse>>> getChangeRequests(@CurrentUserId Long ownerId, @PathVariable Long productId) {
		return ResponseEntity.ok(ApiResponse.success(changeRequestService.list(ownerId, productId)));
	}

	@PatchMapping("/product-change-requests/{requestId}")
	public ResponseEntity<ApiResponse<ProductChangeRequestResponse>> reviewChangeRequest(@CurrentUserId Long ownerId, @PathVariable Long requestId,
		@Valid @RequestBody ReviewProductChangeRequest request) {
		return ResponseEntity.ok(ApiResponse.success(changeRequestService.review(ownerId, requestId, request)));
	}
}
