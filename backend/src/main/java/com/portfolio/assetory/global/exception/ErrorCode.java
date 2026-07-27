package com.portfolio.assetory.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
	NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
	CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
	INVALID_PRODUCT_STATUS(HttpStatus.BAD_REQUEST, "현재 상품 상태에서는 요청을 처리할 수 없습니다."),
	PRODUCT_NOT_READY(HttpStatus.BAD_REQUEST, "판매 시작에 필요한 상품 정보가 부족합니다."),
	WISHLIST_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 찜한 상품입니다."),
	WISHLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "찜한 상품을 찾을 수 없습니다."),
	CART_ITEM_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 장바구니에 담긴 상품입니다."),
	CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니 상품을 찾을 수 없습니다."),
	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
	ORDER_NOT_PAYABLE(HttpStatus.BAD_REQUEST, "현재 주문 상태에서는 결제할 수 없습니다."),
	PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."),
	PRODUCT_ALREADY_PURCHASED(HttpStatus.CONFLICT, "이미 구매한 디지털 상품입니다."),
	PRODUCT_PURCHASE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "구매할 수 없는 상품입니다."),
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "후기를 찾을 수 없습니다."),
	REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 작성한 후기입니다."),
	REFUND_NOT_FOUND(HttpStatus.NOT_FOUND, "환불 요청을 찾을 수 없습니다."),
	REFUND_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 환불 요청이 존재합니다."),
	INVALID_REFUND_STATUS(HttpStatus.BAD_REQUEST, "현재 환불 상태에서는 요청을 처리할 수 없습니다."),
	USER_INACTIVE(HttpStatus.FORBIDDEN, "활성 상태의 회원만 이용할 수 있습니다."),
	LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
	INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Access Token이 유효하지 않습니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh Token이 유효하지 않거나 만료되었습니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

	private final HttpStatus status;
	private final String message;

	ErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}
