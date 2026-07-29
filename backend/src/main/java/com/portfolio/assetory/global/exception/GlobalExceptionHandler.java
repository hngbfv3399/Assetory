package com.portfolio.assetory.global.exception;

import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.portfolio.assetory.global.response.ApiResponse;
import com.portfolio.assetory.global.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<ErrorResponse>> handleBusinessException(BusinessException exception) {
		ErrorCode errorCode = exception.getErrorCode();
		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ApiResponse.failure(errorCode.name(), errorCode.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<ErrorResponse>> handleValidationException() {
		return ResponseEntity
			.status(ErrorCode.INVALID_INPUT.getStatus())
			.body(ApiResponse.failure(ErrorCode.INVALID_INPUT.name(), ErrorCode.INVALID_INPUT.getMessage()));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse<ErrorResponse>> handleTypeMismatchException() {
		return ResponseEntity
			.status(ErrorCode.INVALID_INPUT.getStatus())
			.body(ApiResponse.failure(ErrorCode.INVALID_INPUT.name(), ErrorCode.INVALID_INPUT.getMessage()));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiResponse<ErrorResponse>> handleMissingParameterException() {
		return ResponseEntity
			.status(ErrorCode.INVALID_INPUT.getStatus())
			.body(ApiResponse.failure(ErrorCode.INVALID_INPUT.name(), ErrorCode.INVALID_INPUT.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<ErrorResponse>> handleException(Exception exception) {
		log.error("Unhandled exception", exception);
		return ResponseEntity
			.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
			.body(ApiResponse.failure(
				ErrorCode.INTERNAL_SERVER_ERROR.name(),
				ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
			));
	}
}
