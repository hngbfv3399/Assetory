# Assetory

디지털 상품 창작자를 위한 판매자 중심 플랫폼입니다. 상품 등록부터 주문·Mock 결제·구매 자료 제공·환불·문의·판매 통계·공동 작업자 운영까지의 백엔드 도메인 API를 제공합니다.

## 현재 범위

- Spring Boot 4, Java 17, MySQL, React/Vite
- JWT 기반 회원·인증, 판매자 상품 관리, 찜·장바구니, 주문·Mock 결제, 구매 자료 접근권한
- 후기·환불·문의 채팅, 판매자 주문 관리, 판매 통계·예상 정산
- 상품별 공동 작업자: `MANAGER`, `EDITOR`, `VIEWER`
- 공동 작업자 변경 요청과 소유자 최종 승인·반려

프론트의 판매자 운영 화면은 현재 보류 상태이며, 백엔드 API와 자동 검증을 우선 완성한다.

## 요구 사항

- Java 17
- MySQL 8 이상
- Node.js 20 이상 및 npm
- `curl`, `jq`, Bash (종단 검증 스크립트용)

## 로컬 실행

1. MySQL에 `assetory` 데이터베이스와 애플리케이션 계정을 만든다.
2. 환경 파일을 만든다.

   ```bash
   cd backend
   cp .env.example .env
   ```

3. `backend/.env`의 `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`을 실제 값으로 채운다. 이 파일은 Git에 포함하지 않는다.
4. 백엔드를 실행한다.

   ```bash
   cd backend
   bash scripts/run-local.sh
   ```

5. 필요하면 프론트를 별도 터미널에서 실행한다.

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

## 검증

```bash
cd backend
./mvnw compile

set -a; source .env; set +a
./mvnw test

# 실행 중인 백엔드(기본 http://localhost:8080)에서 실행
bash scripts/test-auth-flow.sh
bash scripts/test-collaborator-flow.sh
```

`BASE_URL=https://example.com bash scripts/test-collaborator-flow.sh`처럼 배포 환경에도 같은 공동 작업자 종단 검증을 실행할 수 있다. 스크립트는 매번 테스트 회원과 임시 상품을 생성한다.

## 공동 작업자 정책

| 역할 | 권한 |
| --- | --- |
| OWNER | 모든 상품·운영 작업, 공동 작업자 관리, 변경 요청 최종 승인·반려, 상품 삭제 |
| MANAGER | 배정 상품의 주문 집계·환불·문의 즉시 처리, 판매 상태 변경 요청 |
| EDITOR | 상품 정보·이미지·구매 자료 변경 요청 |
| VIEWER | 배정 상품의 통계 조회 |

공동 작업자의 상품 변경은 즉시 반영되지 않는다. `PENDING` 요청을 소유자가 `APPROVED`하면 기존 상품 관리 로직으로 반영하고, `REJECTED`하면 반려 사유를 남긴다.

## 문서

- [API 문서](docs/api.md)
- [프로젝트 진행 현황](docs/progress.md)
- [배포 준비 가이드](docs/deployment.md)
- [문제 해결 기록](docs/troubleshooting.md)
- Notion: 개발 관리, API 설계, DB 흐름

## 배포

실제 배포 대상과 계정·도메인은 아직 결정되지 않았다. 배포 전 확인 항목과 환경변수는 [배포 준비 가이드](docs/deployment.md)에 정리돼 있다. 실제 원격 배포는 대상 서비스와 계정 권한을 받은 뒤 수행한다.
