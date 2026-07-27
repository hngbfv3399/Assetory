package com.portfolio.assetory.payment.dto.request;
import jakarta.validation.constraints.NotNull;
public record MockPaymentRequest(@NotNull Long orderId,@NotNull Result result){public enum Result{SUCCESS,FAIL}}
