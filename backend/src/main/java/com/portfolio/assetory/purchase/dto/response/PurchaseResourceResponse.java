package com.portfolio.assetory.purchase.dto.response;
import java.util.List; import com.portfolio.assetory.product.domain.ProductResource;
public record PurchaseResourceResponse(Long productId,List<Resource> resources){public static PurchaseResourceResponse from(Long productId,List<ProductResource> resources){return new PurchaseResourceResponse(productId,resources.stream().map(r->new Resource(r.getId(),r.getDisplayName(),r.getResourceType().name())).toList());}public record Resource(Long id,String name,String type){}}
