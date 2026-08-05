package com.portfolio.assetory.global.config;

import java.math.BigDecimal;
import java.util.List;

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
	private static final int CATALOG_SAMPLE_COUNT = 100;
	private static final List<CatalogSample> CATALOG_SAMPLES = List.of(
		new CatalogSample("브랜드 아이덴티티 스타터 키트", "로고와 색상 규칙을 빠르게 정리하는 브랜드 템플릿입니다.", "브랜드를 시작할 때 필요한 기본 시각 자산과 적용 가이드를 담았습니다."),
		new CatalogSample("모바일 앱 화면 설계 키트", "앱 화면 흐름을 구성할 수 있는 실무형 UI 키트입니다.", "온보딩부터 설정 화면까지, 반복되는 모바일 화면을 빠르게 구성할 수 있습니다."),
		new CatalogSample("콘텐츠 운영 노션 템플릿", "콘텐츠 기획과 발행 일정을 한곳에서 관리하는 템플릿입니다.", "아이디어 수집, 일정 관리, 발행 체크리스트를 한 흐름으로 정리했습니다."),
		new CatalogSample("React 커머스 컴포넌트 모음", "상품 탐색과 결제 흐름에 필요한 React 컴포넌트 모음입니다.", "목록, 필터, 장바구니 등 커머스 화면의 기본 구조를 제공합니다."),
		new CatalogSample("랜딩 페이지 카피 라이브러리", "서비스 소개 문장을 빠르게 다듬을 수 있는 카피 모음입니다.", "첫 화면부터 CTA까지 활용할 수 있는 짧고 명확한 문장 예시를 담았습니다."),
		new CatalogSample("1인 창작자 판매 운영 가이드", "디지털 상품 판매 흐름을 점검하는 실전 전자책입니다.", "상품 기획, 소개 작성, 고객 응대의 핵심 기준을 단계별로 설명합니다."),
		new CatalogSample("데이터 대시보드 디자인 시스템", "지표를 읽기 쉽게 보여 주는 대시보드용 컴포넌트입니다.", "카드, 차트, 상태 표시를 일관된 규칙으로 조합할 수 있습니다."),
		new CatalogSample("API 문서 작성 템플릿", "개발자와 협업할 때 쓰는 API 문서 템플릿입니다.", "요청, 응답, 오류 사례를 빠짐없이 기록하는 표준 구조를 제공합니다."),
		new CatalogSample("프리랜서 계약서 체크리스트", "프로젝트 시작 전 확인할 계약 항목을 정리한 전자책입니다.", "업무 범위와 일정, 수정 정책을 명확히 합의하는 데 도움을 줍니다."),
		new CatalogSample("소셜 미디어 비주얼 팩", "채널별 게시물을 빠르게 제작하는 그래픽 에셋입니다.", "피드, 스토리, 배너에 바로 적용할 수 있는 편집 가능한 시안을 제공합니다.")
	);

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
			Category threeDimensional = findOrCreateCategory(categoryRepository, "3D", 4);
			Category music = findOrCreateCategory(categoryRepository, "음악·사운드", 5);
			Category film = findOrCreateCategory(categoryRepository, "영상·영화", 6);
			Category gaming = findOrCreateCategory(categoryRepository, "게임", 7);
			Category education = findOrCreateCategory(categoryRepository, "교육·자기계발", 8);
			List<Category> catalogCategories = List.of(
				findOrCreateSubcategory(categoryRepository, "UI/UX", 1, design),
				findOrCreateSubcategory(categoryRepository, "게임 에셋", 2, design),
				findOrCreateSubcategory(categoryRepository, "아트·일러스트", 3, design),
				findOrCreateSubcategory(categoryRepository, "브랜드·그래픽", 4, design),
				findOrCreateSubcategory(categoryRepository, "폰트·타이포그래피", 5, design),
				findOrCreateSubcategory(categoryRepository, "웹·앱 템플릿", 1, development),
				findOrCreateSubcategory(categoryRepository, "코드·라이브러리", 2, development),
				findOrCreateSubcategory(categoryRepository, "플러그인·확장", 3, development),
				findOrCreateSubcategory(categoryRepository, "프로그램·도구", 4, development),
				findOrCreateSubcategory(categoryRepository, "판매·운영 가이드", 1, ebook),
				findOrCreateSubcategory(categoryRepository, "영상 강의", 2, ebook),
				findOrCreateSubcategory(categoryRepository, "비즈니스 자료", 3, ebook),
				findOrCreateSubcategory(categoryRepository, "3D 모델", 1, threeDimensional),
				findOrCreateSubcategory(categoryRepository, "텍스처·머티리얼", 2, threeDimensional),
				findOrCreateSubcategory(categoryRepository, "3D 프린팅", 3, threeDimensional),
				findOrCreateSubcategory(categoryRepository, "음악 샘플·루프", 1, music),
				findOrCreateSubcategory(categoryRepository, "사운드 이펙트", 2, music),
				findOrCreateSubcategory(categoryRepository, "음원 제작 도구", 3, music),
				findOrCreateSubcategory(categoryRepository, "영상 템플릿", 1, film),
				findOrCreateSubcategory(categoryRepository, "모션 그래픽", 2, film),
				findOrCreateSubcategory(categoryRepository, "영상 편집 도구", 3, film),
				findOrCreateSubcategory(categoryRepository, "게임 개발 도구", 1, gaming),
				findOrCreateSubcategory(categoryRepository, "게임 UI·아이콘", 2, gaming),
				findOrCreateSubcategory(categoryRepository, "맵·레벨 에셋", 3, gaming),
				findOrCreateSubcategory(categoryRepository, "전문 강의", 1, education),
				findOrCreateSubcategory(categoryRepository, "학습 워크북", 2, education),
				findOrCreateSubcategory(categoryRepository, "자기계발 자료", 3, education)
			);
			User seller = findOrCreateUser(
				userRepository, passwordEncoder, SAMPLE_SELLER_EMAIL, "에셋토리판매자"
			);

			if (productRepository.count() == 0) {
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

			createCatalogSampleProducts(
				productRepository,
				productImageRepository,
				seller,
				catalogCategories
			);

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

	private void createCatalogSampleProducts(
		ProductRepository productRepository,
		ProductImageRepository productImageRepository,
		User seller,
		List<Category> categories
	) {
		for (int index = 0; index < CATALOG_SAMPLE_COUNT; index++) {
			int sampleIndex = index;
			CatalogSample sample = CATALOG_SAMPLES.get(sampleIndex % CATALOG_SAMPLES.size());
			String name = "%s %03d".formatted(sample.name(), sampleIndex + 1);
			Category category = categories.get(sampleIndex % categories.size());
			productRepository.findByName(name).ifPresentOrElse(
				product -> {
					product.update(category, null, null, null, null, null, null, null);
					productRepository.save(product);
				},
				() -> createOnSaleProduct(
					productRepository,
					productImageRepository,
					seller,
					category,
					name,
					sample.summary(),
					sample.description(),
					BigDecimal.valueOf(9_900L + (sampleIndex % 10) * 3_000L),
					catalogImageUrl(sampleIndex)
				)
			);
		}
	}

	private String catalogImageUrl(int index) {
		String[] imageIds = {
			"photo-1558655146-d09347e92766",
			"photo-1559028012-481c04fa702d",
			"photo-1544716278-ca5e3f4abd8c",
			"photo-1460925895917-afdab827c52f",
			"photo-1498050108023-c5249f4df085"
		};
		return "https://images.unsplash.com/%s?auto=format&fit=crop&w=1200&q=80"
			.formatted(imageIds[index % imageIds.length]);
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

	private Category findOrCreateSubcategory(
		CategoryRepository categoryRepository,
		String name,
		int sortOrder,
		Category parent
	) {
		return categoryRepository.findByName(name)
			.orElseGet(() -> categoryRepository.save(Category.create(name, sortOrder, parent)));
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
		Product product = Product.create(seller, category, name, summary, description, null, price, null, null, null);
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
			new BigDecimal("9900"),
			null,
			null,
			null
		);
		productRepository.save(product);
	}

	private record CatalogSample(String name, String summary, String description) {
	}
}
