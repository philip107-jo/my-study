# CHORONG-46 [BE] 대출 비교 작업 정리

- 이슈: CHORONG-46 (대출 비교 기능 백엔드)
- 브랜치: `CHORONG-46-be-대출-비교-기능-백엔드`
- 커밋: `49eb1fa` (develop의 CHORONG-69 PROD-01 합친 뒤 그 위에 작업)
- 작업일: 2026-11-13
- 구현 범위: COM-01 비교용 상품 1~2개 (API-03)
- 기준: 백엔드 기능명세서 `be_product.md`의 COM-01 항목, 공통 규칙 `be_common.md`
- 모든 경로는 `backend/src/` 기준

## 1. 한눈에 보기

| 구분 | 개수 |
|---|---|
| 새로 만든 파일 | 1개 (테스트) |
| 기존 파일에 추가 | 4개 (PROD-01이 만든 파일에 COM-01 것만 추가) |
| 새 API | 1개 (`GET /api/products?ids=a,b`) |
| 테스트 | 13개 추가 (전체 47개 통과) |
| 검사 | `.\mvnw.cmd test` 통과, `.\mvnw.cmd -Psast compile spotbugs:check` 문제 0건 |

## 2. 추가된 기능 (API)

### COM-01 비교용 상품 1~2개

| 항목 | 내용 |
|---|---|
| API 번호 | API-03 |
| 방식, 주소 | `GET /api/products?ids=a,b` |
| 요청 값 | `ids` (상품 번호 1~2개, 쉼표로 구분. 예: `ids=1,2`) |
| 응답 값 | 상품 1~2개 상세 `List<ProductDetailResponseDTO>` (PROD-01 상세와 같은 칸, `features`, `prepayFeeMax` 포함). 요청한 id 순서대로 |
| 권한 | 모두 (로그인 없이 가능) |
| 용도 | 비교 화면에 담긴 상품 A, B의 상세를 한 번에 받아 옴 |

응답 예시 (모양만, 값은 예시)

```json
{
  "success": true,
  "data": [
    { "productId": 1, "companyName": "OO은행", "productName": "OO대출", "productStatus": "ON", "...": "PROD-01 상세 칸" },
    { "productId": 2, "companyName": "OO저축은행", "productName": "OO대출", "productStatus": "CLOSED", "...": "상세 칸 + 상태" }
  ]
}
```

### 상품 상태에 따른 응답

| 상품 상태 | 응답 |
|---|---|
| 게시 (ON) | 상세 전체 |
| 판매 종료 (CLOSED) | 상세 전체 + `productStatus: CLOSED` |
| 숨김 (HIDDEN) | `productId`, `companyName`, `productName`, `productStatus`만. 나머지 칸 null, 목록 칸(`tags`, `features`, `bandRates`)은 빈 목록 |

### 예외처리

| 상황 | 응답 |
|---|---|
| `ids`가 비어 있음, 3개 이상 | 400 `INVALID_INPUT` |
| 숫자가 아닌 id (`1,abc`) | 400 `INVALID_INPUT` |
| 같은 상품 두 번 (`1,1`) | 400 `INVALID_INPUT` (명세서에 없는 선택, 아래 5번) |
| 없는 상품이 하나라도 있음 | 404 `NOT_FOUND` |
| 월 상환액, 총이자 계산 | 서버가 하지 않음. 화면(프론트)이 계산 (COM-02) |

## 3. 파일별 변경

### 기존 파일에 추가 (PROD-01이 만든 파일)

| 파일 | 추가한 내용 |
|---|---|
| `main/java/chorong/product/controller/ProductController.java` | `getCompareProducts`: `@GetMapping(params = "ids")`, `@RequestParam List<Long> ids`. `ids`가 없는 `GET /api/products`는 검색(SEARCH) 쪽 주소라서 `params = "ids"`로 구분 |
| `main/java/chorong/product/service/ProductService.java` | `getCompareProducts(List<Long> ids)` 선언 |
| `main/java/chorong/product/service/ProductServiceImpl.java` | `getCompareProducts` 구현, `validateCompareIds`(개수, 중복 검사), 상수 `COMPARE_MIN`, `COMPARE_MAX` |
| `test/java/chorong/product/service/ProductServiceImplTest.java` | COM-01 테스트 10개 (PROD-01 테스트는 그대로) |

PROD-01 코드(`getProduct` 등)는 고치지 않았습니다.

### 새로 만든 파일

| 파일 | 하는 일 |
|---|---|
| `test/java/chorong/product/controller/ProductControllerTest.java` | 주소 테스트 3개. 숫자 아닌 id는 400, 그때 Service를 안 부름, `ids=1,2`는 번호 목록으로 전달되어 200 |

## 4. 동작 흐름

1. 비교 화면이 `GET /api/products?ids=1,2`를 부름
2. Controller가 `1,2`를 `[1, 2]`로 받음. 숫자가 아니면 `GlobalExceptionHandler`가 400
3. Service `validateCompareIds`: 1~2개인지, 같은 번호가 없는지 확인. 아니면 400 (이때 DB는 조회하지 않음)
4. id마다 PROD-01의 `getProduct`를 불러 상세를 만듦. 없는 상품이면 404, 숨김 상품은 요약만, 판매 종료는 상세 + 상태
5. 요청한 id 순서대로 목록으로 응답 (`ApiResponse.success`)

### 조회 방식 (정한 것)

- 상품마다 `getProduct`를 부르는 "하나씩" 방식을 씀. 상품이 최대 2개라 DB 조회 횟수 차이가 작고, 상세(PROD-01)와 비교(COM-01)가 같은 규칙을 한 곳에서 쓰게 하려고 선택
- 명세서 처리 순서 2번은 4개 테이블(`loan_product`, `product_rate`, `product_tag`, `tag`)을 조회하라고만 적었고 방식은 정하지 않음. 한 번에 조회하는 방식은 만들지 않음

## 5. 명세서와 다르게 한 곳, 정한 규칙

| 항목 | 내용 |
|---|---|
| 같은 상품 두 번 (`ids=1,1`) | 명세서에 없음. 비교 화면에서 같은 상품을 담을 수 없어서 400으로 막음. 명세서(xlsx)에는 반영하지 않고 작업 이력에만 남김 |
| 조회 방식 | 위 4번의 "하나씩" 방식 (명세서가 방식을 정하지 않음) |

## 6. 화면(페이지)과의 연결

백엔드라 만든 화면은 없습니다. 이 API를 쓰는 화면은 CHORONG-47 프론트 작업입니다.

| 화면 | 쓰는 곳 | 내용 |
|---|---|---|
| 비교 화면 `/compare` (`pages/ComparePage.jsx`, CP-01) | `api/productApi.js`의 `getCompareProducts` (API-03) | 비교함의 A, B 상품 번호로 상세를 받아 카드, 판정, 비교표를 그림 |

- 프론트는 판매 종료 딱지, 숨김 상품 안내 카드를 위해 `productStatus`를 봄
- 이 API가 `develop`에 합쳐져야 프론트를 실서버로 확인할 수 있음 (CHORONG-47 문서의 "확인할 점"과 같은 내용)

## 7. 테스트

| 파일 | 개수 | 확인하는 것 |
|---|---|---|
| `ProductServiceImplTest` (COM-01 부분) | 10개 | id 1개, 2개 통과 / 없음, null, 3개, 같은 상품 두 번은 400 / 3개, 같은 상품 두 번이면 상품 조회 안 함 / 요청한 id 순서대로 응답 / 없는 상품 404 |
| `ProductControllerTest` | 3개 | 숫자 아닌 id 400 + `code: INVALID_INPUT` / 숫자 아닌 id면 Service 안 부름 / `ids=1,2`가 `[1, 2]`로 전달되어 200 |

- 입력 검사(B2)는 필수 테스트 대상이라 개수, 중복, 숫자 아님을 모두 확인함
- 가짜 DB(Mockito)를 쓰는 테스트라 진짜 DB와 엔티티 매핑은 서버를 띄워 확인해야 함

## 8. 아직 안 된 것

| 항목 | 이유 |
|---|---|
| 서버를 띄워 실제 DB로 호출 확인 | 자동 테스트만 통과함. DB에 상품 데이터가 있어야 200 확인 가능 |
| 한 번에 조회하는 방식 | 만들지 않기로 함 (필요하면 조회 횟수를 줄이는 방향으로 바꿀 수 있음) |
| 프론트(CHORONG-47)와 연결 확인 | 이 브랜치가 `develop`에 합쳐진 뒤 |

## 9. 직접 확인하는 방법

```powershell
cd backend
.\mvnw.cmd test
.\mvnw.cmd -Psast compile spotbugs:check
.\mvnw.cmd spring-boot:run
```

서버가 뜨면 (포트 8797)

```powershell
curl.exe "http://localhost:8797/api/products?ids=1,2"
```

| 주소 | 기대 결과 |
|---|---|
| `?ids=1,2` (DB에 있는 번호) | 200, 상품 2개 (요청한 순서) |
| `?ids=1` | 200, 상품 1개 |
| `?ids=1,1` | 400 |
| `?ids=1,2,3` | 400 |
| `?ids=abc` 또는 `?ids=` | 400 |
| `?ids=999999999` (없는 번호) | 404 |
| 숨김(HIDDEN) 상품 번호 | 200, 번호, 금융사, 상품명, 상태만 |

## 10. 커밋 (이미 커밋됨)

- 커밋: `49eb1fa CHORONG-46 [BE] COM-01 비교용 상품 1~2개 조회: GET /api/products?ids=, ids 검사(개수, 중복), 테스트`
- push와 PR(대상 `develop`, 제목에 `CHORONG-46` 포함)은 직접 진행

## 11. 관련 기록

- 작업 이력: `docs/history/CHORONG-46/be_product_history.md`
- 남은 일: `docs/history/CHORONG-46/be_product_todo.md` (없음)
