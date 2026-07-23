package com.portfolio.assetory.global.response;

public record ApiResponse<T>(
	boolean success,
	T data,
	String message
) {
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, data, null);
	}

	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<>(true, data, message);
	}

	public static ApiResponse<ErrorResponse> failure(String code, String message) {
		return new ApiResponse<>(false, new ErrorResponse(code), message);
	}
}
