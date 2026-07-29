package com.portfolio.assetory.inquiry;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.assetory.collaborator.service.ProductPermissionService;
import com.portfolio.assetory.global.auth.CurrentUserId;
import com.portfolio.assetory.global.exception.BusinessException;
import com.portfolio.assetory.global.exception.ErrorCode;
import com.portfolio.assetory.global.response.ApiResponse;
import com.portfolio.assetory.inquiry.domain.InquiryMessage;
import com.portfolio.assetory.inquiry.domain.InquiryRoom;
import com.portfolio.assetory.inquiry.domain.InquiryRoomStatus;
import com.portfolio.assetory.inquiry.repository.InquiryMessageRepository;
import com.portfolio.assetory.inquiry.repository.InquiryRoomRepository;
import com.portfolio.assetory.member.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/seller/products/{productId}/inquiries")
public class CollaboratorInquiryController {
	private final CollaboratorInquiryService service;
	public CollaboratorInquiryController(CollaboratorInquiryService service) { this.service = service; }

	@GetMapping
	public ResponseEntity<ApiResponse<InquiryController.RoomListResponse>> list(@CurrentUserId Long userId, @PathVariable Long productId,
		@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
		return ResponseEntity.ok(ApiResponse.success(service.list(userId, productId, page, size)));
	}

	@GetMapping("/{roomId}/messages")
	public ResponseEntity<ApiResponse<List<InquiryController.MessageResponse>>> messages(@CurrentUserId Long userId, @PathVariable Long productId, @PathVariable Long roomId) {
		return ResponseEntity.ok(ApiResponse.success(service.messages(userId, productId, roomId)));
	}

	@PostMapping("/{roomId}/messages")
	public ResponseEntity<ApiResponse<InquiryController.MessageResponse>> send(@CurrentUserId Long userId, @PathVariable Long productId, @PathVariable Long roomId,
		@Valid @RequestBody InquiryController.SendRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service.send(userId, productId, roomId, request.content())));
	}
}

@Service
@Transactional
class CollaboratorInquiryService {
	private final InquiryRoomRepository roomRepository;
	private final InquiryMessageRepository messageRepository;
	private final UserRepository userRepository;
	private final ProductPermissionService permissionService;

	CollaboratorInquiryService(InquiryRoomRepository roomRepository, InquiryMessageRepository messageRepository, UserRepository userRepository,
		ProductPermissionService permissionService) {
		this.roomRepository = roomRepository;
		this.messageRepository = messageRepository;
		this.userRepository = userRepository;
		this.permissionService = permissionService;
	}

	@Transactional(readOnly = true)
	InquiryController.RoomListResponse list(Long userId, Long productId, int page, int size) {
		permissionService.getProductForManagerOperations(userId, productId);
		if (page < 0 || size < 1 || size > 100) throw new BusinessException(ErrorCode.INVALID_INPUT);
		var result = roomRepository.findAllForProduct(productId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
		return new InquiryController.RoomListResponse(result.getContent().stream().map(InquiryController.RoomResponse::from).toList(), result.getNumber(), result.getTotalPages());
	}

	@Transactional(readOnly = true)
	List<InquiryController.MessageResponse> messages(Long userId, Long productId, Long roomId) {
		room(userId, productId, roomId);
		return messageRepository.findAllForRoom(roomId).stream().map(InquiryController.MessageResponse::from).toList();
	}

	InquiryController.MessageResponse send(Long userId, Long productId, Long roomId, String content) {
		InquiryRoom room = room(userId, productId, roomId);
		if (room.getStatus() == InquiryRoomStatus.CLOSED) throw new BusinessException(ErrorCode.INQUIRY_ROOM_CLOSED);
		var sender = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		return InquiryController.MessageResponse.from(messageRepository.save(InquiryMessage.send(room, sender, content)));
	}

	private InquiryRoom room(Long userId, Long productId, Long roomId) {
		permissionService.getProductForManagerOperations(userId, productId);
		return roomRepository.findById(roomId).filter(room -> room.getProduct().getId().equals(productId))
			.orElseThrow(() -> new BusinessException(ErrorCode.INQUIRY_ROOM_NOT_FOUND));
	}
}
