package com.portfolio.assetory.order.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.assetory.cart.domain.CartItem;
import com.portfolio.assetory.cart.repository.CartItemRepository;
import com.portfolio.assetory.global.exception.BusinessException;
import com.portfolio.assetory.global.exception.ErrorCode;
import com.portfolio.assetory.member.domain.User;
import com.portfolio.assetory.member.repository.UserRepository;
import com.portfolio.assetory.order.domain.Order;
import com.portfolio.assetory.order.domain.OrderItem;
import com.portfolio.assetory.order.domain.OrderStatus;
import com.portfolio.assetory.order.dto.request.CreateCartOrderRequest;
import com.portfolio.assetory.order.dto.request.CreateDirectOrderRequest;
import com.portfolio.assetory.order.dto.response.OrderCreateResponse;
import com.portfolio.assetory.order.dto.response.OrderDetailResponse;
import com.portfolio.assetory.order.dto.response.OrderListResponse;
import com.portfolio.assetory.order.repository.OrderItemRepository;
import com.portfolio.assetory.order.repository.OrderRepository;
import com.portfolio.assetory.product.domain.Product;
import com.portfolio.assetory.product.domain.ProductStatus;
import com.portfolio.assetory.product.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class OrderService {
	private final UserRepository userRepository; private final ProductRepository productRepository;
	private final CartItemRepository cartItemRepository; private final OrderRepository orderRepository; private final OrderItemRepository orderItemRepository;
	public OrderService(UserRepository userRepository, ProductRepository productRepository, CartItemRepository cartItemRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
		this.userRepository=userRepository; this.productRepository=productRepository; this.cartItemRepository=cartItemRepository; this.orderRepository=orderRepository; this.orderItemRepository=orderItemRepository;
	}
	@Transactional
	public OrderCreateResponse createFromCart(Long buyerId, CreateCartOrderRequest request) {
		if (request.productIds().stream().distinct().count() != request.productIds().size()) throw new BusinessException(ErrorCode.INVALID_INPUT);
		List<CartItem> cartItems=cartItemRepository.findWithProductByUserIdAndProductIdIn(buyerId, request.productIds());
		if (cartItems.size()!=request.productIds().size()) throw new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND);
		return createOrders(buyerId, cartItems.stream().map(CartItem::getProduct).toList());
	}
	@Transactional
	public OrderCreateResponse createDirect(Long buyerId, CreateDirectOrderRequest request) {
		Product product=productRepository.findPublicProductById(request.productId(), ProductStatus.ON_SALE).orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
		return createOrders(buyerId, List.of(product));
	}
	private OrderCreateResponse createOrders(Long buyerId, List<Product> products) {
		User buyer=userRepository.findById(buyerId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		for(Product product:products) validatePurchasable(buyerId, product);
		Map<Long,List<Product>> bySeller=new LinkedHashMap<>();
		for(Product product:products) bySeller.computeIfAbsent(product.getSeller().getId(), ignored -> new java.util.ArrayList<>()).add(product);
		List<Order> orders=new java.util.ArrayList<>(); Map<Long,List<OrderItem>> itemsByOrderId=new LinkedHashMap<>();
		for(List<Product> sellerProducts:bySeller.values()) {
			BigDecimal total=sellerProducts.stream().map(Product::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
			Order order=orderRepository.save(Order.create(nextOrderNumber(), buyer, sellerProducts.get(0).getSeller(), total));
			List<OrderItem> items=orderItemRepository.saveAll(sellerProducts.stream().map(product -> OrderItem.create(order, product)).toList());
			orders.add(order); itemsByOrderId.put(order.getId(), items);
		}
		return OrderCreateResponse.from(orders, itemsByOrderId);
	}
	private void validatePurchasable(Long buyerId, Product product) {
		if (!product.isOnSale()) throw new BusinessException(ErrorCode.PRODUCT_PURCHASE_NOT_ALLOWED);
		if (product.getSeller().getId().equals(buyerId)) throw new BusinessException(ErrorCode.PRODUCT_PURCHASE_NOT_ALLOWED);
		if (orderItemRepository.existsByProductIdAndOrderBuyerIdAndOrderStatus(product.getId(), buyerId, OrderStatus.PAID)) throw new BusinessException(ErrorCode.PRODUCT_ALREADY_PURCHASED);
	}
	private String nextOrderNumber() { return "ORD-" + LocalDate.now().toString().replace("-", "") + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(); }
	public OrderListResponse getOrders(Long buyerId, OrderStatus status, int page, int size) {
		if(page<0||size<1||size>100)throw new BusinessException(ErrorCode.INVALID_INPUT); Page<Order> orders=status==null?orderRepository.findByBuyerId(buyerId,PageRequest.of(page,size)):orderRepository.findByBuyerIdAndStatus(buyerId,status,PageRequest.of(page,size));
		Map<Long,List<OrderItem>> items=new LinkedHashMap<>();for(Order order:orders)items.put(order.getId(),orderItemRepository.findWithProductByOrderId(order.getId()));return OrderListResponse.from(orders,items);
	}
	public OrderDetailResponse getOrder(Long buyerId,Long orderId){Order order=orderRepository.findWithBuyerById(orderId).orElseThrow(()->new BusinessException(ErrorCode.ORDER_NOT_FOUND));if(!order.getBuyer().getId().equals(buyerId))throw new BusinessException(ErrorCode.FORBIDDEN);return OrderDetailResponse.from(order,orderItemRepository.findWithProductByOrderId(orderId),order.getCompletedAt());}
}
