# Postman 사용 안내

## 3단계 공개 탐색 API

1. 백엔드를 기본 포트로 실행한다.

   ```bash
   cd backend
   bash scripts/run-local.sh
   ```

2. Postman의 **Import**에서 다음 두 파일을 선택한다.

   - `Assetory-Phase-03-Public-Explore.postman_collection.json`
   - `Assetory-Local.postman_environment.json`

3. 우측 상단 환경에서 **Assetory Local**을 선택한다.
4. Collection Runner로 전체를 실행하거나, 각 요청의 **Tests** 탭 결과를 확인한다.

## 포함한 검증

- 정상 응답: 카테고리, 상품 목록, 필터·검색·인기순, 공개 상품 상세, 후기 목록
- 공개 조건: 임시저장 상품 상세 요청은 `404 PRODUCT_NOT_FOUND`
- 입력 오류: 잘못된 상품·후기 정렬값은 `400 INVALID_INPUT`
- 응답 구조: 공통 `success` 응답, 페이지네이션, 이미지·후기 집계 필드

## 환경변수

- `baseUrl`: 기본값 `http://localhost:8080`
- `productId`: 공개 테스트 상품 ID, 기본값 `1`
- `draftProductId`: 임시저장 테스트 상품 ID, 기본값 `4`

이 환경 파일에는 비밀번호, 토큰, DB 정보 같은 민감값을 넣지 않는다.

## 4단계 판매자 상품 관리 API

1. `Assetory-Phase-04-Seller-Product-Management.postman_collection.json`도 함께 Import한다.
2. **Assetory Local** 환경의 `sellerEmail`, `sellerPassword`에 테스트 판매자 계정을 입력한다. 이 값은 내보내거나 Git에 저장하지 않는다.
3. Collection Runner에서 1번부터 순서대로 실행한다. Collection이 생성한 검증 상품은 마지막 요청에서 논리 삭제된다.

포함한 검증: 로그인, `DRAFT` 등록, 판매 준비 부족 차단, 대표 이미지·구매 자료 등록, 판매 시작·중지, 자료 URL 비노출, 자료 수정·삭제, 상품 논리 삭제.

## 5단계 찜·장바구니 API

1. `Assetory-Phase-05-Wishlist-Cart.postman_collection.json`을 Import한다.
2. **Assetory Local** 환경의 `buyerEmail`, `buyerPassword`에 테스트 구매자 계정을 입력한다. 이 값은 내보내거나 Git에 저장하지 않는다.
3. `productId`는 판매 중인 공개 상품 ID로 설정하고 Collection Runner에서 순서대로 실행한다.

포함한 검증: 찜 등록·중복 차단·목록·취소, 장바구니 추가·중복 차단·조회·개별 삭제·빈 장바구니 비우기.
