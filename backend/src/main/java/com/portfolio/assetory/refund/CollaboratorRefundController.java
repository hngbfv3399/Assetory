package com.portfolio.assetory.refund;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.assetory.access.repository.ProductAccessRepository;
import com.portfolio.assetory.collaborator.service.ProductPermissionService;
import com.portfolio.assetory.global.auth.CurrentUserId;
import com.portfolio.assetory.global.exception.BusinessException;
import com.portfolio.assetory.global.exception.ErrorCode;
import com.portfolio.assetory.global.response.ApiResponse;
import com.portfolio.assetory.order.domain.OrderStatus;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/seller/products/{productId}/refunds")
public class CollaboratorRefundController {
	private final CollaboratorRefundService service;
	public CollaboratorRefundController(CollaboratorRefundService service) { this.service = service; }

	@GetMapping
	public ResponseEntity<ApiResponse<RefundController.ListResponse>> list(@CurrentUserId Long userId, @PathVariable Long productId,
		@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
		return ResponseEntity.ok(ApiResponse.success(service.list(userId, productId, page, size)));
	}

	@PatchMapping("/{refundId}/approve")
	public ResponseEntity<ApiResponse<RefundController.Response>> approve(@CurrentUserId Long userId, @PathVariable Long productId, @PathVariable Long refundId,
		@Valid @RequestBody(required = false) RefundController.SellerProcessRequest request) {
		return ResponseEntity.ok(ApiResponse.success(service.approve(userId, productId, refundId, request == null ? null : request.sellerMessage())));
	}

	@PatchMapping("/{refundId}/reject")
	public ResponseEntity<ApiResponse<RefundController.Response>> reject(@CurrentUserId Long userId, @PathVariable Long productId, @PathVariable Long refundId,
		@Valid @RequestBody RefundController.RejectRequest request) {
		return ResponseEntity.ok(ApiResponse.success(service.reject(userId, productId, refundId, request.rejectionReason())));
	}

	@PatchMapping("/{refundId}/complete")
	public ResponseEntity<ApiResponse<RefundController.Response>> complete(@CurrentUserId Long userId, @PathVariable Long productId, @PathVariable Long refundId) {
		return ResponseEntity.ok(ApiResponse.success(service.complete(userId, productId, refundId)));
	}
}

@Service
@Transactional
class CollaboratorRefundService {
	private final RefundRepository refundRepository;
	private final ProductAccessRepository accessRepository;
	private final ProductPermissionService permissionService;

	CollaboratorRefundService(RefundRepository refundRepository, ProductAccessRepository accessRepository, ProductPermissionService permissionService) {
		this.refundRepository = refundRepository;
		this.accessRepository = accessRepository;
		this.permissionService = permissionService;
	}

	@Transactional(readOnly = true)
	RefundController.ListResponse list(Long userId, Long productId, int page, int size) {
		permissionService.getProductForManagerOperations(userId, productId);
		if (page < 0 || size < 1 || size > 100) throw new BusinessException(ErrorCode.INVALID_INPUT);
		var result = refundRepository.findAllForProduct(productId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestedAt")));
		return new RefundController.ListResponse(result.getContent().stream().map(RefundController.Response::from).toList(), result.getNumber(), result.getTotalPages());
	}

	RefundController.Response approve(Long userId, Long productId, Long refundId, String message) {
		Refund refund = refund(userId, productId, refundId);
		if (refund.getStatus() != RefundStatus.REQUESTED) throw new BusinessException(ErrorCode.INVALID_REFUND_STATUS);
		refund.approve(message);
		refund.getOrderItem().getOrder().markRefundApproved();
		return RefundController.Response.from(refund);
	}

	RefundController.Response reject(Long userId, Long productId, Long refundId, String reason) {
		Refund refund = refund(userId, productId, refundId);
		if (refund.getStatus() != RefundStatus.REQUESTED) throw new BusinessException(ErrorCode.INVALID_REFUND_STATUS);
		refund.reject(reason);
		refund.getOrderItem().getOrder().markRefundRejected();
		return RefundController.Response.from(refund);
	}

	RefundController.Response complete(Long userId, Long productId, Long refundId) {
		Refund refund = refund(userId, productId, refundId);
		if (refund.getStatus() != RefundStatus.APPROVED) throw new BusinessException(ErrorCode.INVALID_REFUND_STATUS);
		refund.complete();
		refund.getOrderItem().getOrder().markRefunded();
		accessRepository.findByOrderItemId(refund.getOrderItem().getId()).ifPresent(access -> access.revoke());
		return RefundController.Response.from(refund);
	}

	private Refund refund(Long userId, Long productId, Long refundId) {
		permissionService.getProductForManagerOperations(userId, productId);
		return refundRepository.findById(refundId).filter(value -> value.getOrderItem().getProduct().getId().equals(productId))
			.orElseThrow(() -> new BusinessException(ErrorCode.REFUND_NOT_FOUND));
	}
}
