package com.portfolio.assetory.review.dto.request;
import jakarta.validation.constraints.Max; import jakarta.validation.constraints.Min; import jakarta.validation.constraints.Size;
public record UpdateReviewRequest(@Min(1) @Max(5) Integer rating, @Size(min=1,max=1000) String content) {}
