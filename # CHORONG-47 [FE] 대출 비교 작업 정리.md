# CHORONG-47 [FE] 대출 비교 작업 정리

- 이슈: CHORONG-47 (대출 비교 기능 프론트엔드)
- 브랜치: `CHORONG-47-fe-대출-비교-기능-프론트엔드` (최신 `develop`에서 생성)
- 작업일: 2026-11-16
- 구현 범위: CM-09, CM-10, CP-01~07 (+ 비교함 스토어 ST-04)
- 기준: 기능과 구성은 명세서(`fe_compare.md`), 모양은 시안(`cashlink_21.html`)
- 모든 경로는 `frontend/src/` 기준

## 1. 한눈에 보기

| 구분 | 개수 |
|---|---|
| 새로 만든 파일 | 16개 |
| 기존 파일에 추가 | 10개 |
| 테스트 | 61개 통과 (이 작업에서 새로 쓴 테스트 포함) |
| 검사 | `npm run lint && npm test && npm run build` 통과 (PowerShell 기준) |

## 2. 새로 만든 파일

### 화면, 스토어, API, 계산

| 파일 | 명세 번호 | 하는 일 |
|---|---|---|
| `pages/ComparePage.jsx` | CP-01 | `/compare` 화면. 비교함의 A, B 상품을 서버에서 받아 와 카드, 판정, 비교표를 보여 줌. 금액, 기간 입력 검사, 두 상품 한도와 기간에 맞게 자동 조정 + 알림, [결과 저장] (요청 중 버튼 잠금, 409 안내) |
| `store/useCompareStore.js` | ST-04 | 비교함 스토어. `tray`(담는 중), `pair`(비교 화면 A, B), `swapSlot`, `from`. `tray`, `pair`만 sessionStorage에 저장 |
| `api/myApi.js` | API-20, 21, 23 | `getLikes`, `getRecentViews`, `postCompare` |
| `utils/loanMath.js` | CP-02, 03, 05 | 금리 중간값, 원리금균등 월 상환액과 총이자, 항목별 유리 판정 |
| `utils/loanMath.test.js` | - | 위 계산의 테스트 |

### 비교함, 필터 띠 (`components/common/`)

| 파일 | 명세 번호 | 하는 일 |
|---|---|---|
| `CompareTray.jsx` | CM-09 | 화면 아래 어두운 비교함 바. A, B 칸, 칸마다 X, [비교하기], 전체 비우기. 비교 화면에서는 숨김. 이동이 막히면 한 번 흔들림 |
| `PurposeFilterBanner.jsx` | CM-10 | "자금 용도가 겹치는 상품만 보여요" 띠 + 숨긴 개수. 비교함이 비면 사라짐 |

### 비교 화면 조각 (`components/compare/`)

| 파일 | 명세 번호 | 하는 일 |
|---|---|---|
| `CompareInput.jsx` | CP-06 | 빌릴 금액(만원) + 상환 기간 칩 + 계산 기준 안내 |
| `CompareVerdict.jsx` | CP-02 | 판정 요약. "A 1 : 5 B", 결론 한 줄, 총이자 차이 문장 (못 구하면 이유 문장) |
| `CompareCard.jsx` | CP-03 | 상품 카드. A 주황, B 파랑 테두리. 금리/한도/기간, 상환 방식 등, 한도·기간 초과 경고, [이자 계산], [다른 상품으로 비교], X. 상대 칸이 비면 간단 카드, 숨김 상품은 안내 카드 |
| `CompareEmptySlot.jsx` | CP-04 | 빈 칸 점선 카드. [비교 상품 고르기] → 상품 찾기 |
| `CompareTable.jsx` | CP-05 | 비교표. 항목 이름이 가운데, A와 B가 마주 보는 가로 막대, 유리한 쪽에 "유리" 딱지 |
| `CompareSidebar.jsx` | CP-07 | 비교할 상품 고르기. 찜한 대출, 최근 본 상품을 3개씩 쪽 넘김. 용도가 안 맞는 카드는 흐리게 |

### 기타

| 파일 | 하는 일 |
|---|---|
| `assets/icons/alert.svg` | 한도, 기간 초과 경고 아이콘 |

## 3. 기존 파일에 추가한 것

| 파일 | 추가한 내용 |
|---|---|
| `App.jsx` | `/compare` Route에 ComparePage 연결 |
| `components/layout/Layout.jsx` | CompareTray 연결. 비교함에 상품이 있으면 담기 시작한 화면 묶음 밖으로 이동 시 되돌리고 안내 알림 + 비교함 흔들기 |
| `components/ui/Toast.jsx` | 비교함이 떠 있으면 알림을 그 위로 올림 |
| `components/common/ProductRow.jsx` | [+ 비교] 버튼. 담김 "A 담음"(줄 연주황 바탕 + 딱지), 용도 안 맞음 "비교 불가"(줄 흐리게). 추천, 상품 찾기에서만 보이고 폭 480 이하에서 숨김 |
| `components/common/ProductDetailSheet.jsx` | [+ 비교] 버튼. 랭킹에서 열면 숨김, 담았으면 창 닫음 |
| `api/productApi.js` | `getCompareProducts` (API-03) |
| `constants/options.js` | 상환 기간 선택지(전월세, 주택 구입 60/120/180, 그 밖 12/36/60), `getTermOptions` |
| `styles/common.js` | `SlotBadge`(A/B 딱지), `CompareButton`([+ 비교] 버튼 모양) |
| `utils/format.js`, `format.test.js` | `formatPrepayFee`(없음/최대 n%/기관 문의), `formatRange`(범위 글) + 테스트 |

## 4. 동작 흐름

1. 맞춤 추천 또는 상품 찾기 화면에서 [+ 비교]를 누르면 비교함(`tray`)에 담김
2. 첫 상품의 자금 용도가 기준이 됨. 용도가 하나도 안 겹치는 상품은 담기지 않고 알림
3. 3번째 상품은 B를 바꿈. 이미 담긴 상품을 다시 누르면 뺌
4. 2개가 차면 [비교하기] → `tray`가 `pair`로 옮겨지고 `/compare`로 이동, 비교함은 비움
5. `/compare`가 `pair`로 상품을 받아(API-03) 카드, 판정, 비교표를 그림
6. 비교 화면의 [다른 상품으로 비교]나 [비교 상품 고르기] → 남길 상품만 비교함에 두고 상품 찾기로 이동 (바꿀 칸 기억)
7. 비교함에 상품이 있는 동안 담기 시작한 화면 묶음(맞춤 추천 또는 상품 찾기) 밖으로 이동하면 되돌림
8. [결과 저장] → API-23. 같은 조합은 409로 "이미 저장한 조합" 안내

## 5. 정한 규칙 (사용자 요청 + 판단)

- 비교는 **맞춤 추천**과 **상품 찾기**에서만 사용. 랭킹과 그 밖 화면에서는 [+ 비교]가 안 보임
- 담기 시작한 화면에서만 머묾 (추천에서 담았으면 추천에서, 상품 찾기에서 담았으면 상품 찾기에서)
- 찜, 최근 본 구역은 요청이 401이면 숨김 (회원 확인용 `useAuthStore`가 아직 없어서)
- 자금 용도를 모르는 상품(용도 태그 없음)은 겹치지 않는 것으로 봄
- 기간 칩: 겹치는 용도가 전부 전월세, 주택 구입이면 60/120/180개월, 아니면 12/36/60개월
- 자동 조정은 금액이 두 상품 한도를 넘거나 기간이 최대 기간을 넘거나 선택지에 없을 때만 바꿈

## 6. 명세서와 다르게 한 곳

| 항목 | 내용 |
|---|---|
| `startSwap(slot, keepProduct)` | 명세서는 `slot`만 적었으나, 남길 상품의 용도를 비교함에 담아야 해서 두 번째 값을 추가 |
| `shakeCount`, `shakeTray` | 명세서 ST-04에 없는 필드. 시안의 "이동이 막히면 비교함이 흔들림"을 위해 추가 (메모리만, 저장 안 함) |
| 상환 기간 입력 | 시안은 숫자 입력이지만 명세서(CP-06)의 칩을 유지 |
| 중도상환수수료 | 명세서대로 "최대 n%" (시안의 "3년 지나면 없음"은 예시 데이터라 제외) |
| 사이드바 위치 | 도우미(CHORONG-63)가 아직 없어 페이지 안 오른쪽 열(300px)에 둠. 도우미가 올라오면 위치 재조정 필요 |

## 7. 아직 안 된 것 (TODO)

| 항목 | 이유 / 담당 |
|---|---|
| [결과 저장]의 비회원 로그인 안내 창 (`requireLogin`) | CM-17, CM-22 (CHORONG-43). 지금은 401이면 같은 문구를 알림으로 보여 줌 |
| 사이드바 회원일 때만 찜, 최근 본 요청 | `useAuthStore` (CHORONG-43). 지금은 401이면 구역 숨김 |
| 찜이 바뀔 때 찜 구역 다시 받기 | `useMyStore`의 `likedIds` (CHORONG-43) |
| 사이드바 "추천 상품" 구역 (일치도 %) | `useRecommendStore` (CHORONG-45) |
| 로그아웃, 탈퇴 때 비교함 비우기 | `useAuthStore` (CHORONG-43). `clearTray()`와 `setPair(null, null)` 호출 |
| 필터 띠를 추천, 상품 찾기에 붙이고 용도 겹치는 상품만 거르기 | 추천 CHORONG-45, 상품 찾기 CHORONG-64 |
| 추천 카드(RC-08)의 [+ 비교] | CHORONG-45 (`useCompareStore.addTray` 사용) |
| MY 비교 내역에서 비교 화면 열기 | CHORONG-43 (`setPair` 사용) |
| 계산기가 비교 카드의 이동 값을 받기 | CA-01 (CHORONG-49). 넘기는 값: `loanAmount`(원), `loanMonths`, `interestRate`, `repayMethod`(`EQUAL_PI`) |

## 8. 확인할 점

- Git Bash에서 `npm test`가 `Cannot read properties of undefined (reading 'config')`로 전부 실패함. 손대지 않은 테스트도 같고 PowerShell에서는 통과. 원인은 못 찾음
- 서버 API는 실제로 호출해 본 적 없음 (API-03은 CHORONG-46 백엔드 브랜치가 `develop`에 합쳐져야 실서버로 확인 가능). 아래 가짜 서버로 화면만 확인
- 브라우저에서 직접 눌러 본 확인은 아직 필요 (서버 렌더 결과만 시안과 대조)

## 9. 가짜 데이터로 확인하는 방법 (선택)

저장소 밖 임시 파일이라 git에 안 들어가고, `.env`도 건드리지 않음.

1. 가짜 서버 실행 (포트 4010):
   `node "C:\Users\user1\AppData\Local\Temp\claude\407613049977fed9\2b23827f-452e-479d-ab58-5d7541042c1e\scratchpad\mock-server.cjs"`
2. 개발 서버를 가짜 서버에 연결해 실행 (PowerShell, `frontend` 폴더):
   `$env:VITE_API_BASE_URL='http://localhost:4010'; npm run dev -- --port 5174 --strictPort`
3. http://localhost:5174/compare 접속 → 오른쪽 "비교할 상품 고르기"에서 카드를 눌러 A, B 채우기
4. 시험 조합
   - 1번 + 2번: 같은 용도 비교
   - 4번 + 5번: 전월세, 주택 구입이라 기간 칩 60/120/180개월
   - 1번 후 4번: "용도가 달라 비교 불가"
   - 7번: 금리 기관 문의 (이자 계산 버튼 없음)
   - 8번: 판매 종료 딱지
   - 금액 3,000만원 이상: 한도 초과 경고 (3번, 6번)
   - 같은 조합 저장 두 번: "이미 저장한 조합이에요"
   - 숨김 상품(9번)은 사이드바에 안 나옴. 브라우저 콘솔에서:
     `sessionStorage.setItem('chorong-compare', JSON.stringify({state:{tray:[],pair:[1,9]},version:0})); location.reload()`
5. 가짜 서버 파일은 세션용 임시 위치에 있어 나중에 지워질 수 있음 (필요하면 따로 복사해 보관)

## 10. 커밋 (직접 실행)

```
git status
git add frontend/src
git commit -m "CHORONG-47 [FE] 대출 비교: 비교함, 비교 화면, 비교 계산, [+ 비교] 연결"
git push origin CHORONG-47-fe-대출-비교-기능-프론트엔드
```

- 커밋 메시지 끝에 `Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>` 줄을 붙이기
- PR 대상은 `develop`, PR 제목에도 `CHORONG-47` 포함
- `docs/`는 `.gitignore` 대상이라 이 파일은 커밋되지 않음

## 11. 관련 기록

- 작업 이력: `docs/history/CHORONG-47/fe_compare_history.md`
- 남은 일: `docs/history/CHORONG-47/fe_compare_todo.md`
