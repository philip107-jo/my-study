# CHORONG-82 문서 반영 요청 (2026-11-19)

명세서 사본(`docs/standard/`)은 원본 xlsx에서 만든 것이라 에이전트가 고치지 않습니다. 아래는 **xlsx를 고쳐야 하는 것(A)** 과 **사람끼리 합의가 필요한 것(B)** 입니다. A는 xlsx를 고친 뒤 사본을 다시 만들어 주세요.

---

## A. 명세서(xlsx)를 고쳐 주세요

| # | 명세서 위치 (사본 줄) | 지금 문구 | 바꿀 문구 | 코드 상태 |
| --- | --- | --- | --- | --- |
| A1 | 컴포넌트 명세 시트 **CP-04** '보이는 것' (`fe_compare.md` 93줄) | `남은 상품이 있으면 '{상품명} 상품과 자금 용도({용도})가 겹치는 상품을 골라 주세요.' (용도가 여럿이면 가운데 점으로 이음)` | `… '{상품명} 상품과 자금 용도({용도})와 상환 방식({방식})이 겹치는 상품을 골라 주세요.' (용도, 방식이 여럿이면 가운데 점으로 이음, 상품에 방식 정보가 없어 세 방식이 모두 가능하면 '상환 방식'만 씀)` | **코드를 먼저 고침** (`CompareEmptySlot.jsx`, `useCompareStore.getCompareConditionText`). 지금은 코드와 명세서가 다름 |
| A2 | 폴더 구조 시트 `styles/common.js` 줄 (`fe_common.md` 187줄) | `… SlotBadge (비교 A, B 칸 딱지), CompareButton ([+ 비교] 버튼, 담김과 비교 불가 모양)` | 끝에 **`Num (숫자 글자 모양, CM-12), Unit (작은 단위 글자 모양, CM-12)`** 추가 | **코드를 먼저 고침** (`styles/common.js` 끝, `MoneyText.jsx`, `RateText.jsx`가 import). 아래 'A2 배경' 참고 |
| A3 | 컴포넌트 명세 시트 **CM-04** 동작 (`fe_common_parts.md` 531~532줄) | `props: product, from, rank (선택), matchRate (선택) / 줄 아무 곳 클릭, Enter → 상품 상세 창` | props에 **`hits`** 추가 (아래 'A3 제안') | 구현 안 함 (명세서가 먼저). CHORONG-64 간편 검색 때 같이 구현 |
| A4 | 컴포넌트 명세 시트 **CM-04** 동작 (`fe_common_parts.md` 532줄) | `줄 아무 곳 클릭, Enter → 상품 상세 창` | `줄 아무 곳 클릭 → 상품 상세 창 / 키보드, 화면 낭독기는 상품명 버튼에서 Enter` (줄 전체는 role="button"이 아님) | **코드를 먼저 고침** (`ProductRow.jsx`). 동작은 같고 글만 정확하게 하는 수정 |

### A1 추가 설명
- 실제 규칙은 자금 용도 **그리고** 상환 방식이 겹쳐야 비교할 수 있습니다 (`getCompareBlock`, E15). 비교함에 첫 상품을 담을 때 나오는 알림도 이미 "자금 용도(…)와 상환 방식(…)이 겹치는 상품만"이라고 말합니다. 빈 칸 안내만 용도만 말해서 어긋났습니다.
- 같이 정할 것(선택): **CP-01** 안내 글 (`fe_compare.md` 45줄, `ComparePage` 433줄)은 지금 `자금 용도가 겹치는 두 상품을 항목마다 비교해요.`입니다. 같은 이유로 `자금 용도와 상환 방식이 겹치는 두 상품을 …`으로 바꿀지 정해 주세요. 정해지면 xlsx와 코드를 같이 바꿉니다 (코드는 아직 안 바꿈).

### A2 배경 (MoneyText, RateText 단위 표 중복)
- 문제: `MoneyText.jsx`, `RateText.jsx`에 `UNIT_SIZE`, `UNIT_WEIGHT`, `Num`, `Unit`이 똑같이 두 벌이었음.
- 처음 결정: 명세서는 안 고치고 `MoneyText`가 export하고 `RateText`가 import. → **lint 규칙 `react-refresh/only-export-components`(컴포넌트 파일은 컴포넌트만 export)에 막혀 불가**. 규칙을 끌 수 없음 (AGENT.md).
- 새 파일(예: `unitText.js`)도 폴더 구조 시트에 없는 파일이라 만들 수 없음.
- 채택: `styles/common.js`로 이동. 명세서 `fe_common.md` 90줄 "여러 곳 공통 모양만 styles/common.js"의 취지와 같고, 이미 `SlotBadge`, `CompareButton`도 이름만 목록에 추가해 둔 선례가 있음. **명세서에는 이름 두 개만 더하면 됨.**
- 11-19 낮에 같은 방식(`NumText`, `UnitText`)을 시도했다가 '목록에 이름이 없다'며 되돌렸는데, 이번에는 이름을 `Num`, `Unit`으로 정하고 이 요청서로 목록에 넣는 것으로 정리함. (UI 번호를 붙일지는 선택: `SlotBadge`, `CompareButton`처럼 이름만 넣으면 됨)

### A3 제안 (CM-04 `hits`, 간편 검색 `N/M 일치`)
- 시안의 간편 검색 상품 줄은 태그 자리 맨 앞에 `N/M 일치` 글과 초록 '충족' 태그(체크 아이콘)를 보여 줌. 고른 키워드 M개 중 N개를 충족했다는 뜻.
- 서버 API-07 응답에 충족 키워드 수와 키워드별 이유 문구가 이미 있음.
- 제안 문구: `props: … hits (선택, 간편 검색용 { matched: 충족한 키워드 수, total: 고른 키워드 수, reasons: 키워드별 이유 문구 목록 }): 있으면 태그 맨 앞에 'N/M 일치' 글 + 초록 충족 태그, 없으면 지금과 똑같이 표시`
- 상세 검색, 맞춤 추천 줄은 `hits`를 안 넘기므로 그대로.
- 구현은 CHORONG-64(SR-07~09) 담당과 같이 (`ProductRow.jsx`에 TODO 주석 있음).

---

## B. 사람끼리 합의가 필요한 것 (명세서 수정 없음)

| # | 내용 | 대상 | 지금 상태 | 제안 |
| --- | --- | --- | --- | --- |
| B1 | `options.js` 기간 칩 상수가 같은 값에 이름만 다르게 두 벌: `LONG_TERM_PURPOSES`/`HOUSING_PURPOSES`, `SHORT_TERM_MONTHS`/`MONTHS_OPTIONS_DEFAULT`, `LONG_TERM_MONTHS`/`MONTHS_OPTIONS_HOUSING` | 조장님, CHORONG-45 담당 (김채희) | **안 고침.** 47번 이름은 `getTermOptions` 한 곳만, 45번 이름은 `validate.js`, `StepMoney`, `RecommendResult`가 씀 | 45번 이름으로 통일하고 `getTermOptions`가 45번 이름을 쓰게 한 뒤 47번 상수 3줄 삭제. 지우려는 3개 이름은 명세서에 없어서 명세서 수정은 필요 없음 (`options.test.js`가 `getTermOptions`를 이미 검사함) |
| B2 | 45번 담당 파일의 `일치도` 표기 (명세서는 이미 `추천도`): `RecommendCard.jsx` 245줄, `ReasonList.jsx` 66줄 '조건 일치도', `ToolShortcuts.jsx` 305줄 '일치도 96%', 주석 몇 곳 | CHORONG-45 담당 | **안 고침** (남의 담당 파일). 자세한 줄은 `fe_recommend_todo.md` | 45번이 고침 |
| B3 | `/recommend` 주소 상수 두 벌: `RecommendDialog.jsx`의 `RESULT_PATH`, `credit/CreditResultCard.jsx`의 `RECOMMEND_PATH` (조장님 11-17 지적) | CHORONG-45, 51 담당 | **안 고침** | 한 곳으로 합침 (어디에 둘지는 두 담당이 정함) |
| B4 | `RecommendCard.jsx`의 버튼 안의 버튼 (`article role="button"` 안에 하트, [+ 비교]) | CHORONG-45 담당 | **안 고침.** `ProductRow`는 이미 고침 | `ProductRow`와 같은 방식: 카드에서 role, tabIndex 빼고 상품명을 `<button>`으로 (이 브랜치의 `ProductRow.jsx` 참고) |
| B5 | `styles/common.js` 227줄 주석 "명세서 UI 번호는 변경 요청 중" (명세서에 UI-23~27이 이미 있어 낡은 주석) | CHORONG-45 담당 | **안 고침** | 주석을 지우거나 고침 |
