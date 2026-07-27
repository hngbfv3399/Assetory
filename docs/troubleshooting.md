# 오류 해결 기록

프로젝트 완료까지 실제로 발생한 오류와 해결 내용을 누적한다. 비밀번호, JWT, 원문 토큰, 개인 정보는 기록하지 않는다.

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
