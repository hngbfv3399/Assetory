package com.portfolio.assetory.sellerorder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.portfolio.assetory.order.domain.OrderStatus;
import com.portfolio.assetory.payment.repository.PaymentRepository;
import com.portfolio.assetory.product.repository.ProductImageRepository;
import com.portfolio.assetory.sellerorder.dto.response.SellerOrderCountsResponse;
import com.portfolio.assetory.sellerorder.repository.SellerOrderItemRepository;
import com.portfolio.assetory.collaborator.service.ProductPermissionService;

@ExtendWith(MockitoExtension.class)
class SellerOrderServiceTest {
	@Mock private SellerOrderItemRepository orderItemRepository;
	@Mock private ProductImageRepository imageRepository;
	@Mock private PaymentRepository paymentRepository;
	@Mock private ProductPermissionService permissionService;

	@Test
	void countsEachOrderItemStatus() {
		Long sellerId = 1L;
		LocalDate startDate = LocalDate.of(2026, 7, 1);
		LocalDate endDate = LocalDate.of(2026, 7, 31);
		when(orderItemRepository.countForSeller(eq(sellerId), eq(OrderStatus.PAID), eq(null), any(), any())).thenReturn(25L);
		when(orderItemRepository.countForSeller(eq(sellerId), eq(OrderStatus.REFUND_REQUESTED), eq(null), any(), any())).thenReturn(4L);
		when(orderItemRepository.countForSeller(eq(sellerId), eq(OrderStatus.REFUND_APPROVED), eq(null), any(), any())).thenReturn(2L);
		when(orderItemRepository.countForSeller(eq(sellerId), eq(OrderStatus.REFUNDED), eq(null), any(), any())).thenReturn(3L);
		when(orderItemRepository.countForSeller(eq(sellerId), eq(OrderStatus.REFUND_REJECTED), eq(null), any(), any())).thenReturn(1L);
		when(orderItemRepository.countForSeller(eq(sellerId), eq(null), eq(null), any(), any())).thenReturn(35L);

		SellerOrderService service = new SellerOrderService(orderItemRepository, imageRepository, paymentRepository, permissionService);
		SellerOrderCountsResponse response = service.counts(sellerId, null, startDate, endDate);

		assertEquals(35, response.total());
		assertEquals(25, response.paid());
		assertEquals(4, response.refundRequested());
		assertEquals(2, response.refundApproved());
		assertEquals(3, response.refunded());
		assertEquals(1, response.refundRejected());
	}
}
