# Assetory API 문서

> 기준: 2026-07-28 현재 구현
> 범위: 3~7단계 백엔드 API
> 공통 응답: `{ "success": boolean, "data": ..., "message": string | null }`

## 공통 규칙

인증이 필요한 API는 아래 헤더를 사용한다.

```http
Authorization: Bearer {accessToken}
```

성공 응답 예시:

```json
{ "success": true, "data": {}, "message": null }
```

오류 응답 예시:

```json
{
  "success": false,
  "data": { "code": "ERROR_CODE" },
  "message": "오류 설명"
}
```

인증 헤더가 없으면 `401 UNAUTHORIZED`, 잘못된 요청값은 `400 INVALID_INPUT`을 반환한다.

## 공개 탐색

인증 없이 조회할 수 있다. 상품·후기는 판매 중(`ON_SALE`)이고 논리 삭제되지 않은 상품만 노출한다.

| Method | API | 주요 입력 | 성공 | 설명 |
| --- | --- | --- | --- | --- |
| GET | `/api/categories` | 없음 | 200 | 활성 카테고리 목록 |
| GET | `/api/products` | `categoryId`, `keyword`, `sort`, `page`, `size` | 200 | 공개 상품 목록 |
| GET | `/api/products/{productId}` | 경로 ID | 200 | 공개 상품 상세 |
| GET | `/api/products/{productId}/reviews` | `sort`, `page`, `size` | 200 | 공개 후기 목록 |

### 상품 목록

`GET /api/products?categoryId=1&keyword=react&sort=LATEST&page=0&size=20`

| 파라미터 | 기본값 | 허용 값/제약 |
| --- | --- | --- |
| `categoryId` | 없음 | 카테고리 ID |
| `keyword` | 없음 | 상품명·요약 검색어 |
| `sort` | `LATEST` | `LATEST`, `PRICE_LOW`, `PRICE_HIGH`, `POPULAR` |
| `page` | `0` | 0 이상 |
| `size` | `20` | 1 이상, 100 이하 |

응답에는 상품 ID, 이름, 요약, 가격, 대표 이미지, 판매자 닉네임, 평균 평점, 후기 수, 페이지 정보가 포함된다. `POPULAR`은 현재 공개 후기 수 내림차순이며 주문·결제 구현 후 구매 수량 기준으로 전환한다.

공개 대상이 아닌 상품 상세·후기 조회는 `404 PRODUCT_NOT_FOUND`다. 후기 정렬은 `LATEST`, `RATING_HIGH`, `RATING_LOW`을 지원하며 기본 `size`는 10이다.

## 판매자 상품 관리

모든 API에 인증이 필요하다. 상품·이미지·구매 자료는 해당 상품의 판매자 본인만 조회·변경할 수 있다. 타 판매자 요청은 `403 FORBIDDEN`, 없는 상품은 `404 PRODUCT_NOT_FOUND`다.

| Method | API | 주요 입력 | 성공 | 설명 |
| --- | --- | --- | --- | --- |
| POST | `/api/seller/products` | `categoryId`, `name`, `summary`, `description`, `price` | 201 | `DRAFT` 상품 생성 |
| GET | `/api/seller/products` | `status`, `page`, `size` | 200 | 내 상품 목록 |
| GET | `/api/seller/products/{productId}` | 경로 ID | 200 | 내 상품 상세 |
| PATCH | `/api/seller/products/{productId}` | 수정할 기본 정보만 전송 | 200 | 내 상품 부분 수정 |
| DELETE | `/api/seller/products/{productId}` | 경로 ID | 200 | 상품 논리 삭제 |
| POST | `/api/seller/products/{productId}/images` | `imageUrl`, `originalName`, `isThumbnail` | 201 | 이미지 메타데이터 등록 |
| DELETE | `/api/seller/products/{productId}/images/{imageId}` | 경로 ID | 200 | 상품 이미지 삭제 |
| POST | `/api/seller/products/{productId}/resources` | `name`, `type`, `url`, `originalName`, `fileSize` | 201 | 구매 자료 메타데이터 등록 |
| PATCH | `/api/seller/products/{productId}/resources/{resourceId}` | `name`, `url` 중 수정 필드 | 200 | 구매 자료 수정 |
| DELETE | `/api/seller/products/{productId}/resources/{resourceId}` | 경로 ID | 200 | 구매 자료 삭제 |
| PATCH | `/api/seller/products/{productId}/publish` | 없음 | 200 | 판매 시작, 응답 `ON_SALE` |
| PATCH | `/api/seller/products/{productId}/suspend` | 없음 | 200 | 판매 중지, 응답 `STOPPED` |

### 상태 규칙

- 상품 생성 상태는 `DRAFT`다.
- 판매 시작에는 대표 이미지 1개와 활성 구매 자료 1개 이상이 필요하다. 미충족 시 `400 PRODUCT_NOT_READY`다.
- 판매 중지 또는 논리 삭제된 상품은 공개 탐색과 새 찜·장바구니 추가 대상에서 제외된다.
- 판매자 상품 상세에는 구매 자료의 이름·유형만 반환하며 실제 `url`은 노출하지 않는다.

## 찜

모든 API에 인증이 필요하다. 판매 중이며 삭제되지 않은 상품만 찜할 수 있다.

| Method | API | 주요 입력 | 성공 | 설명 |
| --- | --- | --- | --- | --- |
| POST | `/api/wishlists/products/{productId}` | 경로 ID | 201 | 찜 등록 |
| GET | `/api/wishlists` | `page`, `size` | 200 | 내 찜 목록 |
| DELETE | `/api/wishlists/products/{productId}` | 경로 ID | 200 | 찜 취소 |

### 찜 등록

`POST /api/wishlists/products/{productId}`

요청 본문은 없다.

```json
{
  "success": true,
  "data": { "productId": 10, "wished": true },
  "message": null
}
```

| 상태 | 코드 | 조건 |
| --- | --- | --- |
| 404 | `PRODUCT_NOT_FOUND` | 없는·비공개·판매 중지·삭제 상품 |
| 409 | `WISHLIST_ALREADY_EXISTS` | 이미 찜한 상품 |

### 내 찜 목록

`GET /api/wishlists?page=0&size=20`

`page`는 0 이상, `size`는 1~100이다. 현재 판매 중인 상품만 최근 찜한 순으로 반환한다.

```json
{
  "success": true,
  "data": {
    "products": [{
      "id": 10,
      "name": "React 관리자 대시보드 템플릿",
      "price": 29000,
      "thumbnailUrl": "https://example.com/thumbnail.jpg",
      "sellerNickname": "지호"
    }],
    "page": 0,
    "totalPages": 1
  },
  "message": null
}
```

### 찜 취소

`DELETE /api/wishlists/products/{productId}`

```json
{
  "success": true,
  "data": { "productId": 10, "wished": false },
  "message": null
}
```

본인의 찜 관계만 삭제한다. 관계가 없거나 타 회원의 관계이면 `404 WISHLIST_NOT_FOUND`로 처리해 데이터 존재 여부를 노출하지 않는다.

## 장바구니

모든 API에 인증이 필요하다. 디지털 상품이므로 수량을 관리하지 않으며, 사용자당 하나의 장바구니에 동일 상품을 한 번만 담을 수 있다.

| Method | API | 주요 입력 | 성공 | 설명 |
| --- | --- | --- | --- | --- |
| POST | `/api/cart/items` | 본문 `{ "productId": 10 }` | 201 | 상품 추가 |
| GET | `/api/cart` | 없음 | 200 | 내 장바구니 조회 |
| DELETE | `/api/cart/items/{cartItemId}` | 경로 ID | 200 | 개별 상품 제거 |
| DELETE | `/api/cart/items` | 없음 | 200 | 장바구니 비우기 |

### 상품 추가

`POST /api/cart/items`

```json
{ "productId": 10 }
```

```json
{
  "success": true,
  "data": { "productId": 10, "added": true },
  "message": null
}
```

Notion 원문에는 요청 본문이 없지만 경로에 상품 식별자가 없어 상품을 특정할 수 없다. 따라서 경로·HTTP Method는 유지하고 최소 보완으로 `productId` 요청 본문을 사용한다.

| 상태 | 코드 | 조건 |
| --- | --- | --- |
| 400 | `INVALID_INPUT` | `productId` 누락 또는 형식 오류 |
| 404 | `PRODUCT_NOT_FOUND` | 없는·비공개·판매 중지·삭제 상품 |
| 409 | `CART_ITEM_ALREADY_EXISTS` | 이미 담긴 상품 |

### 내 장바구니 조회

`GET /api/cart`

```json
{
  "success": true,
  "data": {
    "items": [{
      "cartItemId": 31,
      "productId": 10,
      "name": "React 관리자 대시보드 템플릿",
      "price": 29000,
      "thumbnailUrl": "https://example.com/thumbnail.jpg",
      "sellerNickname": "지호"
    }],
    "totalPrice": 29000,
    "itemCount": 1
  },
  "message": null
}
```

가격은 현재 판매 가격을 사용한다. 판매 중지·삭제 상품은 목록에서 제외한다. 장바구니가 없거나 비어 있으면 `items: []`, `totalPrice: 0`, `itemCount: 0`을 반환한다.

### 개별 제거·비우기

`DELETE /api/cart/items/{cartItemId}`

```json
{
  "success": true,
  "data": { "productId": 10, "removed": true },
  "message": null
}
```

본인 장바구니에 없는 항목 또는 타 회원 항목은 `404 CART_ITEM_NOT_FOUND`다.

`DELETE /api/cart/items`

```json
{
  "success": true,
  "data": null,
  "message": "장바구니를 비웠습니다."
}
```

장바구니가 이미 비어 있어도 성공으로 처리하며, 상품 원본 데이터는 삭제하지 않는다.

## 검증 자료

## 환불

환불은 주문 항목 단위로 요청한다. 모든 API에 인증이 필요하다.

| Method | API | 설명 |
| --- | --- | --- |
| POST | `/api/refunds` | 구매자 환불 요청 (`orderItemId`, `reason`) |
| GET | `/api/refunds` | 구매자 환불 목록 |
| GET | `/api/refunds/{refundId}` | 구매자 환불 상세 |
| PATCH | `/api/refunds/{refundId}/cancel` | 요청 상태의 환불 취소 |
| GET | `/api/seller/refunds` | 판매자 환불 목록 |
| GET | `/api/seller/refunds/{refundId}` | 판매자 환불 상세 |
| PATCH | `/api/seller/refunds/{refundId}/approve` | 판매자 승인 (`sellerMessage` 선택) |
| PATCH | `/api/seller/refunds/{refundId}/reject` | 판매자 거절 (`rejectionReason`) |
| PATCH | `/api/seller/refunds/{refundId}/complete` | 승인된 환불 완료 |

- 완료 시 환불은 `COMPLETED`, 주문은 `REFUNDED`가 되며 해당 주문 항목의 구매 접근권한은 회수된다.
- 거절 시 환불은 `REJECTED`, 주문은 `REFUND_REJECTED`가 되며 구매 접근권한은 유지된다.
- 구매자·판매자 이외의 환불 상세 접근은 `404 REFUND_NOT_FOUND`다.

## 문의 채팅

동일 구매자·상품·판매자 조합은 하나의 상담방을 재사용한다. 구매자와 해당 상품 판매자만 접근할 수 있다.

| Method | API | 설명 |
| --- | --- | --- |
| POST | `/api/inquiries/rooms` | 상담방 생성 또는 기존 방 재사용 (`productId`) |
| GET | `/api/inquiries/rooms` | 내 상담방 목록 |
| GET | `/api/inquiries/rooms/{roomId}` | 상담방 상세 및 메시지 목록 |
| POST | `/api/inquiries/rooms/{roomId}/messages` | 메시지 전송 (`content`) |
| GET | `/api/inquiries/rooms/{roomId}/messages` | 메시지 목록 |
| PATCH | `/api/inquiries/rooms/{roomId}/read` | 상대방 메시지 읽음 처리 |
| PATCH | `/api/inquiries/rooms/{roomId}/close` | 상담방 종료 |

- 종료된 방의 메시지 전송은 `400 INQUIRY_ROOM_CLOSED`다.
- 참여자가 아닌 회원의 상담방 접근은 `404 INQUIRY_ROOM_NOT_FOUND`다.

| 범위 | Postman Collection |
| --- | --- |
| 공개 탐색 | [`postman/Assetory-Phase-03-Public-Explore.postman_collection.json`](../postman/Assetory-Phase-03-Public-Explore.postman_collection.json) |
| 판매자 상품 관리 | [`postman/Assetory-Phase-04-Seller-Product-Management.postman_collection.json`](../postman/Assetory-Phase-04-Seller-Product-Management.postman_collection.json) |
| 찜·장바구니 | [`postman/Assetory-Phase-05-Wishlist-Cart.postman_collection.json`](../postman/Assetory-Phase-05-Wishlist-Cart.postman_collection.json) |

Postman 환경은 [`postman/Assetory-Local.postman_environment.json`](../postman/Assetory-Local.postman_environment.json)을 사용한다. 비밀번호·토큰·DB 정보는 환경 파일에 저장하거나 커밋하지 않는다.
