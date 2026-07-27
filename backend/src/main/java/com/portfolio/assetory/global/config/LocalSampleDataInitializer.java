package com.portfolio.assetory.global.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.portfolio.assetory.category.domain.Category;
import com.portfolio.assetory.category.repository.CategoryRepository;
import com.portfolio.assetory.member.domain.User;
import com.portfolio.assetory.member.repository.UserRepository;
import com.portfolio.assetory.product.domain.Product;
import com.portfolio.assetory.product.domain.ProductImage;
import com.portfolio.assetory.product.domain.ProductImageType;
import com.portfolio.assetory.product.repository.ProductImageRepository;
import com.portfolio.assetory.product.repository.ProductRepository;
import com.portfolio.assetory.review.domain.Review;
import com.portfolio.assetory.review.repository.ReviewRepository;

@Configuration
@Profile("local")
public class LocalSampleDataInitializer {

	private static final String SAMPLE_SELLER_EMAIL = "seller.sample@assetory.local";
	private static final String SAMPLE_BUYER_EMAIL = "buyer.sample@assetory.local";

	@Bean
	CommandLineRunner initializeLocalSampleData(
		CategoryRepository categoryRepository,
		UserRepository userRepository,
		ProductRepository productRepository,
		ProductImageRepository productImageRepository,
		ReviewRepository reviewRepository,
		PasswordEncoder passwordEncoder
	) {
		return arguments -> {
			Category design = findOrCreateCategory(categoryRepository, "디자인", 1);
			Category development = findOrCreateCategory(categoryRepository, "개발", 2);
			Category ebook = findOrCreateCategory(categoryRepository, "전자책", 3);

			if (productRepository.count() == 0) {
				User seller = findOrCreateUser(
					userRepository, passwordEncoder, SAMPLE_SELLER_EMAIL, "에셋토리판매자"
				);

				createOnSaleProduct(
					productRepository, productImageRepository, seller, development,
					"React 관리자 대시보드 템플릿", "React로 제작한 관리자 페이지 템플릿입니다.",
					"대시보드와 회원 관리 화면이 포함되어 있습니다.", new BigDecimal("29000"),
					"https://images.unsplash.com/photo-1558655146-d09347e92766"
				);
				createOnSaleProduct(
					productRepository, productImageRepository, seller, design,
					"모던 브랜드 UI 키트", "웹과 앱 화면에 바로 적용할 수 있는 UI 키트입니다.",
					"피그마 컴포넌트와 컬러 가이드를 제공합니다.", new BigDecimal("19000"),
					"https://images.unsplash.com/photo-1559028012-481c04fa702d"
				);
				createOnSaleProduct(
					productRepository, productImageRepository, seller, ebook,
					"1인 디지털 상품 판매 가이드", "디지털 상품 판매를 시작하는 창작자를 위한 전자책입니다.",
					"상품 기획부터 판매 페이지 작성까지의 과정을 담았습니다.", new BigDecimal("12000"),
					"https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c"
				);
				createDraftProduct(productRepository, seller, development);
			}

			if (reviewRepository.count() == 0) {
				User buyer = findOrCreateUser(
					userRepository, passwordEncoder, SAMPLE_BUYER_EMAIL, "에셋토리구매자"
				);
				Product reactDashboard = productRepository.findByName("React 관리자 대시보드 템플릿")
					.orElseThrow();
				reviewRepository.save(Review.create(10001L, buyer, reactDashboard, 5, "구성이 깔끔해서 바로 적용하기 좋았습니다."));
				reviewRepository.save(Review.create(10002L, buyer, reactDashboard, 3, "기본 구조를 이해하는 데 도움이 됐습니다."));
				reviewRepository.save(Review.create(10003L, buyer, reactDashboard, 4, "관리 화면의 출발점으로 사용하기 좋습니다."));
			}
		};
	}

	private User findOrCreateUser(
		UserRepository userRepository,
		PasswordEncoder passwordEncoder,
		String email,
		String nickname
	) {
		return userRepository.findByEmail(email)
			.orElseGet(() -> userRepository.save(User.register(
				email,
				passwordEncoder.encode("AssetorySample123!"),
				nickname
			)));
	}

	private Category findOrCreateCategory(CategoryRepository categoryRepository, String name, int sortOrder) {
		return categoryRepository.findByName(name)
			.orElseGet(() -> categoryRepository.save(Category.create(name, sortOrder)));
	}

	private void createOnSaleProduct(
		ProductRepository productRepository,
		ProductImageRepository productImageRepository,
		User seller,
		Category category,
		String name,
		String summary,
		String description,
		BigDecimal price,
		String imageUrl
	) {
		Product product = Product.create(seller, category, name, summary, description, null, price);
		product.startSale();
		Product savedProduct = productRepository.save(product);
		productImageRepository.save(ProductImage.attach(
			savedProduct,
			imageUrl,
			name + ".jpg",
			ProductImageType.THUMBNAIL,
			1
		));
	}

	private void createDraftProduct(ProductRepository productRepository, User seller, Category category) {
		Product product = Product.create(
			seller,
			category,
			"비공개 테스트 상품",
			"공개 목록에서 제외되어야 하는 상품입니다.",
			"판매 시작 전 테스트 상품입니다.",
			null,
			new BigDecimal("9900")
		);
		productRepository.save(product);
	}
}
