# 배포 준비 가이드

최종 갱신: 2026-07-29

## 현재 상태

- 백엔드 로컬 MySQL 실행과 Maven 검증은 완료됐다.
- 프론트 운영 화면은 보류 상태다.
- 실제 배포 대상, 클라우드 계정, 도메인, 운영 DB는 아직 제공되지 않았다. 따라서 이 문서는 배포 전 체크리스트이며 실제 원격 배포는 수행하지 않았다.

## 배포 전 필수 값

아래 값은 배포 플랫폼의 비밀 환경변수로만 등록한다.

| 변수 | 용도 |
| --- | --- |
| `DB_URL` | 운영 MySQL JDBC URL |
| `DB_USERNAME` | 운영 DB 계정 |
| `DB_PASSWORD` | 운영 DB 비밀번호 |
| `JWT_SECRET` | 32바이트 이상 무작위 JWT 서명 키 |
| `JWT_ACCESS_TOKEN_EXPIRATION` | Access Token 유효 기간(초) |
| `JWT_REFRESH_TOKEN_EXPIRATION` | Refresh Token 유효 기간(초) |
| `SPRING_PROFILES_ACTIVE` | 운영 프로필 이름 |
| `SERVER_PORT` 또는 플랫폼 제공 포트 | HTTP 포트 |

`backend/.env`와 실제 비밀번호·토큰·DB URL은 저장소, Notion, 로그, API 응답에 넣지 않는다.

## 운영 DB

- MySQL 8 이상을 사용한다.
- 현재 애플리케이션은 `spring.jpa.hibernate.ddl-auto=update`다. 첫 운영 배포 전에는 백업과 스키마 검토가 필요하다.
- 운영 안정화 시에는 명시적 마이그레이션 도구를 도입하고 `ddl-auto`를 검증 모드로 전환하는 것을 권장한다.

## 백엔드 빌드·기동

```bash
cd backend
./mvnw test
./mvnw package
java -jar target/assetory-0.0.1-SNAPSHOT.jar
```

플랫폼의 시작 명령은 JAR 이름과 포트 전달 방식을 실제 빌드 산출물에 맞춰 설정한다. 로컬 전용 `scripts/run-local.sh`은 `.env`를 읽으므로 운영 비밀값을 파일에 두지 않는 플랫폼에서는 사용하지 않는다.

## 배포 후 검증

1. 헬스 엔드포인트: `GET /api/v1/health`
2. 인증 흐름: `BASE_URL=https://배포주소 bash backend/scripts/test-auth-flow.sh`
3. 공동 작업자 흐름: `BASE_URL=https://배포주소 bash backend/scripts/test-collaborator-flow.sh`
4. 공개 상품 탐색, Mock 결제, 자료 접근, 환불, 문의, 통계 API를 실제 배포 URL에서 확인한다.
5. 애플리케이션 로그에 비밀번호·JWT·쿠키·개인정보가 없는지 확인한다.

## 배포 완료 기준

- 환경변수가 플랫폼 비밀 저장소에만 등록돼 있다.
- 운영 DB 연결·스키마 생성/검증이 성공한다.
- 컴파일·Maven 테스트·인증·공동 작업자 종단 검증이 성공한다.
- CORS와 프론트 API 주소가 실제 도메인에 맞는다.
- 롤백 대상 JAR/이미지와 DB 백업 절차가 준비돼 있다.
