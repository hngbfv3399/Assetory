# 오류 해결 기록

프로젝트 완료까지 실제로 발생한 오류와 해결 내용을 누적한다. 비밀번호, JWT, 원문 토큰, 개인 정보는 기록하지 않는다.

## 2026-08-05 — 프런트 검증 명령을 프로젝트 루트에서 실행함

- 상태: 해결
- 문제점: 프로젝트 루트에서 `npm run lint`를 실행해 `Missing script: lint` 오류가 발생했다.
- 원인: `lint`와 `build` 스크립트는 루트가 아니라 `frontend/package.json`에 정의되어 있다.
- 해결 방법: 작업 디렉터리를 `frontend`로 변경해 동일한 검증 명령을 다시 실행했다.
- 검증 결과: `cd frontend && npm run lint && npm run build`가 모두 성공했다.
- 재발 방지: 프런트 검증은 항상 `frontend` 디렉터리에서 실행한다.

## 2026-08-05 — 제한된 샌드박스에서 Vite 개발 서버 포트를 열 수 없음

- 상태: 해결
- 문제점: 제한된 실행 환경에서 `npm run dev -- --host 127.0.0.1` 실행 시 `listen EPERM` 오류로 Vite 개발 서버가 시작하지 못했다.
- 원인: 로컬 포트 바인딩이 제한된 샌드박스 환경의 권한 범위를 벗어났다.
- 해결 방법: 승인된 로컬 실행 환경에서 같은 명령으로 개발 서버를 실행했다.
- 검증 결과: `http://127.0.0.1:5173`에서 판매자 스튜디오 화면을 열어 데스크톱·모바일 화면을 확인했다.
- 재발 방지: 브라우저 기반 UI 검증에 필요한 로컬 서버는 포트 바인딩이 허용된 실행 환경에서 실행한다.

## 2026-08-05 — zsh에서 판매자 목록 검증 URL의 `&`가 패턴으로 해석됨

- 상태: 해결
- 문제점: 가상 계정의 `GET /api/seller/products?page=0&size=20` 검증 명령이 `no matches found`로 실패했다.
- 원인: zsh가 따옴표 없는 URL의 `&`를 셸 제어 문자로 해석했다.
- 해결 방법: 전체 URL을 작은따옴표로 감쌌다.
- 검증 결과: 가상 판매자 계정은 상품 목록 1건, 가상 구매자 계정은 빈 판매자 목록을 정상적으로 받았다.
- 재발 방지: Query String을 포함한 curl URL은 항상 인용한다.

## 작성 형식

### YYYY-MM-DD — 오류 제목

- 상태: 해결 / 진행 중
- 문제점:
- 원인:
- 해결 방법:
- 검증 결과:
- 재발 방지:

---

## 2026-07-25 — Spring Boot가 DB URL을 읽지 못함

- 상태: 해결
- 문제점: `./mvnw spring-boot:run` 실행 시 `Driver com.mysql.cj.jdbc.Driver claims to not accept jdbcUrl, ${DB_URL}` 오류가 발생했다.
- 원인: Spring Boot는 `.env` 파일을 자동으로 읽지 않으며, 새 터미널에서 DB 환경변수가 등록되지 않았다. 또한 DB URL의 `&` 문자를 따옴표로 감싸지 않아 셸 파싱 오류가 발생할 수 있었다.
- 해결 방법: `.env` 값을 셸에서 사용할 수 있도록 따옴표로 감싸고, `.env`를 로드한 뒤 서버를 실행하는 `backend/scripts/run-local.sh`을 만들었다.
- 검증 결과: `bash scripts/run-local.sh`로 서버를 실행한 뒤 JPA 테이블 생성과 `GET /api/categories` 응답을 확인했다.
- 재발 방지: 새 터미널에서는 `cd backend && bash scripts/run-local.sh`으로 서버를 실행한다.

## 2026-07-25 — 상품 목록 조회 중 썸네일 JPQL 경로 오류

- 상태: 해결
- 문제점: `GET /api/products`가 `INTERNAL_SERVER_ERROR`를 반환했다.
- 원인: `ProductImage` 엔티티에는 `productId` 필드가 없고 `product` 연관관계만 있는데, Repository의 파생 쿼리가 `productId`를 엔티티 속성으로 해석하려 했다.
- 해결 방법: 썸네일 조회 Repository 메서드를 `image.product.id` 경로를 명시한 JPQL로 변경했다.
- 검증 결과: 서버 재시작 후 판매 중 상품 3개가 썸네일·판매자 닉네임과 함께 정상 반환됐고, 임시저장 상품은 목록에서 제외됐다.
- 재발 방지: 연관관계 ID 조건은 파생 쿼리의 해석에 의존하지 않고, 복잡한 경로는 명시적인 JPQL로 작성한다.

## 2026-07-25 — 로컬 실행 스크립트에 애플리케이션 포트를 직접 전달할 수 없음

- 상태: 해결
- 문제점: `bash scripts/run-local.sh --server.port=8081` 실행 시 Maven이 `--server.port=8081`을 자신의 명령행 옵션으로 해석해 시작하지 못했다.
- 원인: Spring Boot 실행 인자는 Maven 플러그인 속성으로 전달해야 하는데, 스크립트가 인자를 그대로 Maven에 넘겼다.
- 해결 방법: 스크립트가 `ASSETORY_SERVER_PORT` 환경변수를 읽어 `spring-boot.run.arguments`로 전달하도록 수정했다.
- 검증 결과: `ASSETORY_SERVER_PORT=8081 bash scripts/run-local.sh`로 서버를 실행해 8081 헬스 API와 공개 후기 API를 정상 호출했다.
- 재발 방지: 기본 서버는 `bash scripts/run-local.sh`, 다른 포트가 필요하면 `ASSETORY_SERVER_PORT=8081 bash scripts/run-local.sh`을 사용한다.

## 2026-07-25 — React 상세 화면이 이전 백엔드 서버에 연결됨

- 상태: 해결
- 문제점: React 상품 목록은 표시됐지만, 상품 상세·후기 요청이 실패했다.
- 원인: 프론트 개발 서버의 `/api` 프록시 기본 대상은 8080인데, 최신 후기 API가 적용된 서버는 별도 검증 포트에서 실행 중이었다.
- 해결 방법: 기본 대상은 8080으로 유지하면서, `ASSETORY_API_TARGET` 환경변수로 검증용 백엔드 포트를 지정할 수 있게 했다.
- 검증 결과: `ASSETORY_API_TARGET=http://localhost:8083 npm run dev`로 실행한 프론트에서 목록·상세·후기와 인기순 전환을 확인했다.
- 재발 방지: 기본 개발 시 백엔드를 8080에서 실행하고, 별도 포트 검증 시 `ASSETORY_API_TARGET=http://localhost:포트번호 npm run dev`를 사용한다.

## 2026-07-26 — 샌드박스에서 로컬 MySQL 연결 실패

- 상태: 해결
- 문제점: `bash scripts/run-local.sh` 실행 시 MySQL이 3306 포트에서 실행 중인데도 `Communications link failure`로 애플리케이션이 시작하지 못했다.
- 원인: 제한된 샌드박스 실행 환경이 로컬 TCP MySQL 연결을 허용하지 않았다. MySQL 서비스와 애플리케이션 설정 자체의 오류는 아니었다.
- 해결 방법: 승인된 로컬 실행 환경에서 DB 연결을 확인하고 백엔드를 실행했다.
- 검증 결과: DB 조회와 Spring Boot 기동이 성공했으며, 판매자 상품 등록 API의 정상·오류 시나리오를 실제 호출로 확인했다.
- 재발 방지: 로컬 DB를 이용하는 서버 실행·통합 API 검증은 로컬 연결이 허용된 실행 환경에서 수행한다.

## 2026-07-26 — 판매자 상품 목록 추가 후 import 위치 오류

- 상태: 해결
- 문제점: 내 상품 목록 조회 코드를 추가한 뒤 `./mvnw compile`이 실패했다.
- 원인: 추가한 Java import가 클래스 닫는 중괄호 뒤에 배치됐다.
- 해결 방법: import를 파일 상단 패키지 선언 아래로 옮긴 뒤 다시 컴파일한다.
- 검증 결과: import 위치 수정 후 `./mvnw compile`이 성공했다.
- 재발 방지: Java 파일 변경 뒤 import와 클래스 범위를 먼저 확인하고 컴파일한다.

## 2026-07-26 — 상태 전이 구현 중 구매 자료 import 누락

- 상태: 해결
- 문제점: 이미지·구매 자료 등록과 상태 전이 코드를 추가한 뒤 컴파일이 실패했다.
- 원인: `SellerProductService`에서 사용하는 `ProductResource` import가 누락됐다.
- 해결 방법: 해당 import를 추가하고 재컴파일한다.
- 검증 결과: import 추가 후 `./mvnw compile`이 성공했다.
- 재발 방지: 새 도메인 타입을 사용하는 Service 변경 후 import를 확인한다.

## 2026-07-26 — 판매 시작의 연관관계 ID 조회 오류

- 상태: 해결
- 문제점: 판매 시작 API가 대표 이미지 존재 여부를 확인할 때 `500`을 반환했다.
- 원인: `ProductImage`에는 `productId` 속성이 없고 `product` 연관관계만 있는데, 파생 exists 쿼리가 잘못된 속성 경로를 사용했다.
- 해결 방법: `image.product.id`와 `resource.product.id`를 명시하는 JPQL exists 쿼리로 변경한다.
- 검증 결과: 수정 후 컴파일에 성공했고, 판매 시작·공개 노출·판매 중지·비노출을 실제 API로 확인했다.
- 재발 방지: 연관관계 ID 조건은 명시적인 JPQL 경로를 사용한다.

## 2026-07-26 — 백엔드 실행 시 8080 포트 충돌

- 상태: 해결
- 문제점: `bash scripts/run-local.sh`로 백엔드를 실행할 때 `Port 8080 was already in use`로 기동이 실패했다.
- 원인: 이미 실행 중인 Assetory 또는 다른 로컬 프로세스가 8080 포트를 점유하고 있었다.
- 해결 방법: 기존 8080 서버를 그대로 사용하거나, 별도 검증 서버는 `ASSETORY_SERVER_PORT=8081 bash scripts/run-local.sh`로 실행한다.
- 검증 결과: 서버가 이미 실행 중인 상태로 확인됐으며, 포트 충돌은 애플리케이션 코드나 DB 연결 오류가 아니다.
- 재발 방지: 서버를 중복 실행하기 전에 기존 프로세스와 포트를 확인한다.

## 2026-07-26 — Postman Collection이 이전 단계의 productId를 사용함

- 상태: 해결
- 문제점: 판매 준비 부족 검증이 기대한 `400 PRODUCT_NOT_READY` 대신 `403 FORBIDDEN`을 반환했다.
- 원인: 로컬 환경의 공개 탐색용 `productId=1`이 Collection 변수보다 우선되어, 방금 등록한 상품이 아닌 다른 판매자의 샘플 상품을 요청했다.
- 해결 방법: 4단계 Collection 전용 변수명을 `sellerProductId`로 분리했다.
- 검증 결과: Collection JSON 형식 재검증 예정이며, 수정된 Collection을 다시 Import해야 한다.
- 재발 방지: 단계별 Collection에서 런타임 생성 ID는 기존 환경 변수와 겹치지 않는 전용 이름으로 관리한다.

## 2026-07-27 — 찜 목록 Controller import 위치 오류

- 상태: 해결
- 문제점: 찜 목록 조회를 추가한 뒤 `./mvnw compile`이 실패했다.
- 원인: `WishlistListResponse` import가 `WishlistController` 클래스 닫는 중괄호 뒤에 배치됐다.
- 해결 방법: import를 패키지 선언 아래의 import 영역으로 옮겼다.
- 검증 결과: 수정 후 백엔드 컴파일과 실제 찜 목록 API를 통과했다.
- 재발 방지: Java 파일 변경 뒤 import가 클래스 선언 앞에만 있는지 확인하고 컴파일한다.

## 2026-07-27 — 기본 Maven 테스트가 DB 환경변수 없이 실행됨

- 상태: 해결
- 문제점: `./mvnw test`가 `DB_URL`을 찾지 못해 ApplicationContext 로딩에 실패했다.
- 원인: 테스트 실행은 `backend/.env`를 자동으로 읽지 않는데, 기본 프로필의 datasource 설정은 환경변수를 요구한다.
- 해결 방법: `.env`를 로드한 로컬 DB 환경에서 테스트를 다시 실행한다.
- 검증 결과: `.env`를 로드한 뒤 `./mvnw test`를 실행해 테스트 1건을 통과했다.
- 재발 방지: DB 의존 통합 테스트는 환경변수를 명시적으로 로드하거나 테스트 전용 datasource를 구성한다.

## 2026-07-28 — 주문 상세 조회가 500을 반환함

- 상태: 해결
- 문제점: 결제 완료 주문의 `GET /api/orders/{orderId}`가 내부 서버 오류를 반환했다.
- 원인: 주문 상세 응답에서 결제 정보를 별도로 조회하는 과정이 현재 주문 조회 흐름과 맞지 않아 지연 로딩 객체 접근 경로가 불안정했다.
- 해결 방법: 주문이 보유한 `completedAt` 값을 주문 상세 응답의 결제 완료 시각으로 사용하도록 조회 경로를 단순화했다.
- 검증 결과: 서버 재시작 후 결제 완료 주문의 상세 조회가 `200`과 `paidAt`을 반환했다.
- 재발 방지: 주문 집계 조회에서는 이미 보유한 주문 상태·완료 시각을 우선 사용하고, 별도 연관 엔티티 조회는 필요한 경우에만 명시적으로 fetch 한다.

## 2026-07-28 — 환불 완료에서 결제 상태 ENUM 저장 실패

- 상태: 해결
- 문제점: 판매자 환불 완료 API가 `500 INTERNAL_SERVER_ERROR`를 반환했다.
- 원인: 기존 MySQL `payments.status` ENUM은 `SUCCESS`, `FAILED`만 허용하는데, 환불 완료에서 새 `REFUNDED` 결제 상태를 저장하려 했다.
- 해결 방법: Mock 결제 성공은 결제 이력으로 보존하고, 환불 완료 상태는 `Refund.COMPLETED`, 주문 `REFUNDED`, 구매 접근권한 `REVOKED`로 연동했다.
- 검증 결과: 승인→완료 후 구매 자료 상세가 `403`으로 차단되고, 환불 상태가 `COMPLETED`로 반환됐다.
- 재발 방지: 기존 ENUM 값을 확장할 때는 명시적 DB 마이그레이션을 먼저 추가하고, 결제 승인 이력과 환불 상태를 혼동하지 않는다.

## 2026-07-29 — 샌드박스의 Maven 테스트가 로컬 MySQL에 연결하지 못함

- 상태: 해결
- 문제점: `.env`를 로드한 `./mvnw test`가 `Communications link failure`로 ApplicationContext 생성에 실패했다.
- 원인: 제한된 샌드박스 실행 환경은 로컬 MySQL TCP 연결을 허용하지 않았다.
- 해결 방법: 동일한 `.env` 기반 명령을 승인된 로컬 연결 환경에서 재실행했다.
- 검증 결과: `AssetoryApplicationTests` 1건이 성공했고, 이후 `bash scripts/run-local.sh` 기동과 공동 작업자 API의 실제 curl 종단 검증도 통과했다.
- 재발 방지: MySQL이 필요한 테스트·서버 기동·종단 검증은 로컬 연결이 허용된 환경에서 실행한다.

## 2026-07-29 — 상품 통계 정렬 메서드 누락

- 상태: 해결
- 문제점: 판매 통계 구현 뒤 `./mvnw compile`이 `ProductMetrics.salesCount()`를 찾지 못해 실패했다.
- 원인: 상품별 판매 건수 정렬에 메서드 참조를 사용하면서 내부 집계 클래스의 접근자를 추가하지 않았다.
- 해결 방법: `salesCount()` 접근자를 추가하고 다시 컴파일했다.
- 검증 결과: 재컴파일과 전체 Maven 테스트가 성공했다.
- 재발 방지: 내부 집계 타입을 정렬·매핑에 사용할 때 메서드 참조의 대상 접근자를 컴파일 전에 확인한다.

## 2026-07-29 — 필수 통계 기간 파라미터 누락이 500을 반환함

- 상태: 해결
- 문제점: `GET /api/seller/statistics/sales`에서 `endDate`를 생략하면 `500 INTERNAL_SERVER_ERROR`가 반환됐다.
- 원인: `MissingServletRequestParameterException`이 전역 입력 검증 처리 대상에 없었다.
- 해결 방법: `GlobalExceptionHandler`에서 해당 예외를 `400 INVALID_INPUT`으로 변환했다.
- 검증 결과: 서버 재시작 뒤 누락 요청이 `400 INVALID_INPUT`, 미인증 요청이 `401 UNAUTHORIZED`를 반환하는 것을 curl로 확인했다.
- 재발 방지: 필수 `@RequestParam`을 추가할 때 누락 예외도 전역 입력 검증 범위에 포함한다.

## 2026-07-29 — 주문 상태 집계 회귀 테스트의 불필요한 목 설정

- 상태: 해결
- 문제점: 새 `SellerOrderServiceTest` 실행 시 `UnnecessaryStubbingException`으로 Maven 테스트가 실패했다.
- 원인: 모든 상태를 포괄하는 목 설정이 개별 상태 목 설정에 의해 사용되지 않았다.
- 해결 방법: 포괄 목 설정을 제거하고, 실제로 호출되는 총계와 각 상태별 Repository 집계만 설정했다.
- 검증 결과: `.env`를 로드한 `./mvnw test`에서 애플리케이션 컨텍스트와 주문 상태 집계 회귀 테스트를 포함한 2건이 성공했다.
- 재발 방지: Mockito strict mode 테스트에서는 호출되지 않는 기본 목 설정보다 검증 대상 호출별 목 설정만 둔다.

## 2026-07-29 — 변경 요청 JSON 타입과 Spring MVC 변환기 불일치

- 상태: 해결
- 문제점: 변경 요청 생성 API가 `500 HttpMessageConversionException`을 반환했고, 초기에는 `ObjectMapper` 빈도 없어 애플리케이션 컨텍스트 테스트가 실패했다.
- 원인: Spring Boot 4의 HTTP 변환기는 Jackson 3 `tools.jackson.*` 타입을 사용하지만, JWT 런타임 의존성에서 노출된 Jackson 2 `com.fasterxml.*` 타입을 사용했다.
- 해결 방법: 공통 `ObjectMapper` 빈을 Jackson 3 타입으로 등록하고, 변경 요청 DTO·응답·서비스의 JSON 타입을 동일한 Jackson 3 패키지로 통일했다.
- 검증 결과: Maven 전체 테스트와 편집자 변경 요청→소유자 승인→실제 상품 반영 curl 검증이 성공했다.
- 재발 방지: Spring Boot 4에서 JSON DTO를 추가할 때 HTTP 변환기와 같은 Jackson 패키지를 사용하고, 애플리케이션 컨텍스트 검증을 먼저 실행한다.

## 2026-07-29 — 역할 검증 curl 스크립트의 예약 변수 충돌

- 상태: 해결
- 문제점: MANAGER·EDITOR 권한 검증 스크립트가 `zsh: read-only variable: status`로 중단됐다.
- 원인: zsh의 예약 읽기 전용 변수 `status`를 HTTP 상태 코드 저장 변수로 사용했다.
- 해결 방법: 변수명을 `response_code`로 변경했다.
- 검증 결과: MANAGER의 상품 범위 환불·문의·통계 접근 성공과 EDITOR의 `403` 차단을 확인했다.
- 재발 방지: zsh 스크립트에서는 예약 변수명을 피하고 `set -euo pipefail`로 중간 실패를 즉시 중단한다.

## 2026-07-29 — 종단 검증 스크립트에서 서브셸 응답 상태가 사라짐

- 상태: 해결
- 문제점: 초기 공동 작업자 종단 검증 스크립트에서 회원 가입 함수를 명령 치환으로 호출하면, 이후 회원 ID가 올바르게 저장되지 않았다.
- 원인: Bash 명령 치환은 서브셸에서 실행되므로 함수가 갱신한 전역 응답 변수가 부모 셸에 반영되지 않았다.
- 해결 방법: 가입·로그인 함수가 명령 치환 출력 대신 `SIGNED_EMAIL`, `SIGNED_ID`, `LOGIN_TOKEN` 변수를 같은 셸에서 설정하도록 바꿨다.
- 검증 결과: `bash scripts/test-collaborator-flow.sh`의 15단계 전체가 로컬 서버에서 성공했다.
- 재발 방지: 셸 테스트에서 상태를 공유해야 할 함수는 명령 치환으로 감싸지 않고, 명시적 결과 변수를 사용한다.

## 2026-08-05 — 기본 Maven 테스트가 로컬 환경 설정 없이 실패함

- 상태: 해결
- 문제점: `./mvnw test`가 애플리케이션 컨텍스트의 `${DB_URL}` 미해결과 Mockito inline mock maker의 JVM attach 실패로 중단됐다.
- 원인: 기본 Maven 테스트 실행은 Git 제외된 `backend/.env`를 자동으로 읽지 않으며, 현재 JVM 환경은 Byte Buddy의 self-attach를 허용하지 않는다.
- 해결 방법: 테스트 전용 H2 datasource와 JWT 설정을 추가하고 Mockito subclass mock maker를 사용하도록 구성했다.
- 검증 결과: `./mvnw test`에서 ApplicationContext와 SellerOrderService 테스트 2건이 MySQL·`.env`·JVM attach 없이 통과했다.
- 재발 방지: 테스트 전용 datasource와 Mockito 실행 설정을 유지해 일반 `./mvnw test`가 독립적으로 실행되게 한다.

## 2026-08-05 — 로컬 서버 포트 충돌

- 상태: 해결
- 문제점: `bash scripts/run-local.sh` 실행 시 8080 포트가 이미 사용 중이라 서버가 기동하지 못했다.
- 원인: 기존 로컬 백엔드 프로세스가 8080을 점유하고 있었다.
- 해결 방법: 기존 프로세스를 중단하지 않고 검증 전용 서버를 `ASSETORY_SERVER_PORT=8082`로 실행했다.
- 검증 결과: 8082 서버에서 판매자·구매자 2계정의 상품 등록, 판매 시작, Mock 결제, 구매 자료 접근 API 흐름이 성공했다.
- 재발 방지: 실행 중인 개발 서버를 유지해야 하는 검증은 별도 포트를 사용하고, 필요 없는 프로세스만 소유자 확인 후 종료한다.

## 2026-08-05 — MySQL 마이그레이션의 조건부 컬럼 추가 문법 오류

- 상태: 해결
- 문제점: 판매 방식 컬럼을 추가하는 SQL이 `ADD COLUMN IF NOT EXISTS` 구문 오류로 중단됐다.
- 원인: 현재 로컬 MySQL은 해당 조건부 DDL 구문을 지원하지 않는다.
- 해결 방법: `information_schema.columns`를 조회한 뒤 동적 SQL로 누락된 컬럼만 추가하도록 마이그레이션을 변경했다.
- 검증 결과: 수정된 마이그레이션을 다시 적용해 `user_roles` 제거와 판매 방식 컬럼 추가를 확인한다.
- 재발 방지: 운영 DB에서 지원하는 DDL 문법을 전제로 하지 말고, 버전 호환 SQL 또는 전용 마이그레이션 도구를 사용한다.

## 2026-08-05 — Vite 개발 서버에서 인증 API가 CORS로 차단됨

- 상태: 해결
- 문제점: `127.0.0.1` Vite 개발 서버에서 회원가입 요청이 브라우저 네트워크 오류로 실패했다.
- 원인: 백엔드의 `/api/**`에 로컬 개발 Origin을 허용하는 CORS 설정이 없었다.
- 해결 방법: `WebConfig`에 `localhost`와 `127.0.0.1`의 임의 개발 포트만 허용하는 CORS 매핑을 추가했다.
- 검증 결과: 백엔드 재기동 뒤 5175 Vite 브라우저에서 회원가입·로그인이 성공했고, 판매자 센터 접근이 정상 표시됐다.
- 재발 방지: 브라우저에서 프론트·백엔드를 다른 Origin으로 개발할 때 API CORS 허용 범위를 명시하고, 운영 Origin은 배포 환경에서 별도로 제한한다.

## 2026-08-05 — 로그인 직후 이전 refresh 실패가 새 세션을 제거함

- 상태: 해결
- 문제점: 회원가입 뒤 로그인은 성공했지만 즉시 `/seller`가 비로그인 접근 거부 화면으로 이동했다.
- 원인: 앱 시작 시 실행된 refresh 요청이 실패한 뒤 로그인 mutation이 저장한 새 access token을 다시 지웠다.
- 해결 방법: refresh 실패 시 현재 Zustand store에 새 access token이 없는 경우에만 세션을 초기화하도록 변경했다.
- 검증 결과: 같은 판매자 계정으로 재로그인해 `/seller` 화면이 정상 표시되는 것을 브라우저에서 확인했다.
- 재발 방지: 비동기 세션 복구와 로그인 mutation이 경합할 때 오래된 실패 응답이 새 인증 상태를 덮어쓰지 않게 한다.

## 2026-08-05 — 로컬 MySQL 미실행으로 브라우저 종단 검증 중단

- 상태: 미해결
- 문제점: `bash scripts/run-local.sh`가 MySQL 연결 단계에서 `Communications link failure`로 종료되어 판매자 화면의 실제 API 브라우저 검증을 시작하지 못했다.
- 원인: 로컬 환경의 MySQL 서비스가 현재 연결을 수락하지 않는다.
- 해결 방법: 데이터베이스를 변경하지 않고 중단했다. MySQL 서비스와 로컬 환경 변수를 확인한 뒤 서버를 다시 기동해야 한다.
- 검증 결과: 프론트 린트·빌드와 H2 기반 Maven 테스트 2건은 통과했으며, 실제 MySQL 기반 브라우저 종단 검증은 미실행이다.
- 재발 방지: 브라우저 회귀 전 `bash scripts/run-local.sh`와 헬스 API로 DB 연결 상태를 먼저 확인한다.
