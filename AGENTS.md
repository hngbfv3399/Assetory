# Assetory Agent Guide

이 파일은 Assetory에서 작업하는 코딩 에이전트의 짧은 진입점이다. 상세한 하네스 규칙은 [docs/harness-engineering.md](docs/harness-engineering.md)를 따른다.

## 읽는 순서와 기준 문서

1. 사용자의 현재 요청과 승인 범위
2. Notion [디지털 상품 판매자 플랫폼 · 개발 관리](https://app.notion.com/p/3a5a6bf2033380148284f369b3b9b384)
3. Notion [API 설계](https://app.notion.com/p/3a5a6bf20333808da59dfb53a1c6254c)와 [DB 흐름](https://app.notion.com/p/3a5a6bf203338015be48efea5ffd2c64)
4. 이 저장소의 `docs/`와 현재 구현

- 기획·ERD·API·화면 설계는 이미 완료된 것으로 취급한다. 실제 충돌이나 누락이 확인된 경우에만 변경하고, 이유를 작업일지에 남긴다.
- Notion의 일정 상태와 로컬 작업일지가 다르면 임의로 Notion을 수정하지 않는다. 사용자에게 차이를 알리고 승인받는다.
- 현재 단계의 범위만 구현한다. 다음 단계의 도메인, API, 화면을 미리 구현하지 않는다.

## 프로젝트 요약

- 개인 창작자를 위한 디지털 상품 판매자 중심 플랫폼이다.
- 기술 스택은 Spring Boot, React, MySQL이다.
- 디지털 상품만 다룬다. 배송·실제 PG 결제·실제 송금 정산은 범위 밖이며, 결제는 Mock 결제, 정산은 예상 금액 표시만 제공한다.
- 한 회원은 구매자와 판매자 역할을 모두 가질 수 있고 기능 화면을 전환한다.

## 아키텍처와 API 규칙

- 백엔드는 레이어드 아키텍처를 사용한다. 기능별로 `controller`, `service`, `repository`, `domain`, `dto`를 둔다.
- Controller는 HTTP 입출력만, Service는 비즈니스 로직·트랜잭션, Repository는 영속성만 담당한다.
- 엔티티를 API 응답으로 직접 노출하지 않는다.
- 공통 응답 형태는 `success`, `data`, `message`다. 공통 예외와 오류 코드는 `global`에 둔다.
- 새 백엔드 API는 `BusinessException + ErrorCode + GlobalExceptionHandler` 전역 예외 처리 체계를 반드시 사용한다. Controller·Service에서 응답 생성을 위한 임의의 `try-catch`를 두지 않으며, 세부 규칙은 `docs/harness-engineering.md`를 따른다.
- Notion에 정한 HTTP method, 경로, 인증 조건을 임의로 바꾸거나 새 라이브러리를 추가하지 않는다. 필요성·영향을 먼저 설명한다.

## 작업 방식

- 매 개발 단계의 시작에는 이전에 완료·검증한 작업을 먼저 요약하고, `docs/progress.md`를 기준으로 남은 TODO와 금일 구현 범위를 제시한 뒤 작업한다.
- 구현 전에는 Notion의 해당 개발 단계·API 설계·DB 흐름을 확인하고, 이를 근거로 이번에 진행할 작업, 구현 순서, 완료 기준, 범위 밖 항목을 사용자에게 먼저 알린다. 설계와 실제 진행 상태가 다르면 차이를 함께 알린다.
- 검증이 끝난 각 작업 뒤에는 갱신된 진행률과 남은 TODO를 짧게 보고한다.
- 작업 전 관련 코드·문서·Git 상태를 읽고, 작은 단위로 구현한다.
- 실행하지 않은 검증을 완료라고 말하지 않는다. 오류가 나면 새 기능보다 원인 해결을 우선한다.
- 민감값은 환경변수만 사용하고 `.env` 및 토큰·비밀번호를 Git에 넣거나 출력하지 않는다.
- 사용자 승인 없이 외부 서비스 변경, 원격 푸시, 데이터 삭제·초기화를 하지 않는다.
- 오류가 발생하면 기능 작업을 이어가기 전에 원인을 해결하고, `docs/troubleshooting.md`에 문제점·원인·해결 방법·검증 결과를 기록한다. 비밀번호·토큰·개인정보는 기록하지 않는다.

## 완료와 검증

- 변경 범위에 맞는 컴파일, 린트, 테스트, 실제 API 또는 UI 동작을 검증한다.
- 인증 변경은 `backend/scripts/test-auth-flow.sh`를 우선 사용한다.
- 프론트 변경은 `npm run lint`, `npm run build`, 개발 서버 통신을 확인한다.
- 실제 수행한 명령, 결과, 미검증 항목만 최종 보고한다.
- 새 터미널에서 백엔드를 실행할 때는 `cd backend && bash scripts/run-local.sh`를 사용한다. 이 스크립트가 Git 제외된 `backend/.env`를 로드한다.

## 주요 문서

- [하네스 엔지니어링](docs/harness-engineering.md): 도메인 불변 규칙, 작업 흐름, 검증 기준
- [3일차 작업일지](docs/day-03.md): 기반·회원 공통 로직의 실제 작업 기록
