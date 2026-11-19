# CHORONG-82 문서 반영 요청 (2026-11-19)

명세서 사본(`docs/standard/`)은 원본 xlsx에서 만든 것이라 에이전트가 고치지 않습니다. 아래는 **A. xlsx를 이렇게 고쳐 주세요**와 **B. 코드가 겹치는 것을 누가 맞출지**입니다. A는 xlsx를 고친 뒤 사본을 다시 만들어 주세요.

---

## A. 명세서(xlsx)를 이렇게 고쳐 주세요

| # | 명세서 위치 (사본 줄) | 지금 문구 | 바꿀 문구 |
| --- | --- | --- | --- |
| A1 | 컴포넌트 명세 시트 **CP-04** '보이는 것' (`fe_compare.md` 93줄) | `남은 상품이 있으면 '{상품명} 상품과 자금 용도({용도})가 겹치는 상품을 골라 주세요.' (용도가 여럿이면 가운데 점으로 이음)` | `… '{상품명} 상품과 자금 용도({용도})와 상환 방식({방식})이 겹치는 상품을 골라 주세요.' (용도, 방식이 여럿이면 가운데 점으로 이음, 상품에 방식 정보가 없어 세 방식이 모두 가능하면 '상환 방식'만 씀)` |
| A2 | 폴더 구조 시트 `styles/common.js` 줄 (`fe_common.md` 187줄) | `… SlotBadge (비교 A, B 칸 딱지), CompareButton ([+ 비교] 버튼, 담김과 비교 불가 모양)` | 끝에 **`Num (숫자 글자 모양, CM-12), Unit (작은 단위 글자 모양, CM-12)`** 추가 |
| A3 | 컴포넌트 명세 시트 **CM-04** 동작 (`fe_common_parts.md` 531~532줄) | `props: product, from, rank (선택), matchRate (선택)` | props에 **`hits`** 추가 (아래 'A3 제안') |
| A4 | 컴포넌트 명세 시트 **CM-04** 동작 (`fe_common_parts.md` 532줄) | `줄 아무 곳 클릭, Enter → 상품 상세 창` | `줄 아무 곳 클릭 → 상품 상세 창 / 키보드, 화면 낭독기는 상품명 버튼에서 Enter` (줄 전체는 role="button"이 아님) |
| A5 | 컴포넌트 명세 시트 **CM-10** '보이는 것' (`fe_compare.md` 31줄) | `'자금 용도와 상환 방식이 겹치는 상품만 보여요' 띠 + 비교함 첫 상품의 자금 용도 딱지 + '숨긴 상품 N개' (0개면 안 보임)` | `… 비교함 첫 상품의 자금 용도 딱지 + 상환 방식 딱지 (세 방식 모두 가능한 상품, 방식 정보가 없는 상품은 방식 딱지 없음) + '숨긴 상품 N개' …` / 글은 회색, 딱지는 흰 알약에 주황 글자 / 아이콘은 전구(bulb) |
| A6 (선택) | 공통 디자인 시트 '적용 방법 / 공통 부품' 줄 (`fe_design.md` 12줄) | `styles/common.js 에 Button, Card, Panel, Chip, Tag, EmptyBox, InlineError` | 폴더 구조 시트(`fe_common.md` 187줄)와 같은 최신 이름으로 맞추거나, `이름 목록은 폴더 구조 시트 참고`로 한 곳만 관리 (IconButton, FormStack, FormField, FormLabel, FormLabelNote, ChipGrid, SegmentedFull, SlotBadge, CompareButton, A2의 Num, Unit이 빠져 있음) |

### A1 설명
- 실제로 비교할 수 있는 조건은 자금 용도 **그리고** 상환 방식이 겹치는 것입니다 (`getCompareBlock`, E15). 비교함에 첫 상품을 담을 때 나오는 알림은 이미 "자금 용도(…)와 상환 방식(…)이 겹치는 상품만"이라고 말합니다. 빈 칸 안내 글만 용도만 말합니다.
- 같이 정해 주세요 (선택): **CP-01** 안내 글 (`fe_compare.md` 45줄, `ComparePage` 433줄)은 지금 `자금 용도가 겹치는 두 상품을 항목마다 비교해요.`입니다. 같은 이유로 `자금 용도와 상환 방식이 겹치는 두 상품을 …`으로 바꿀지 정해 주세요. 정해지면 xlsx와 코드를 같이 바꿉니다. (이 항목은 코드를 아직 안 바꿨습니다)

### A2 설명 (MoneyText, RateText 단위 표 중복)
- 문제: `MoneyText.jsx`, `RateText.jsx`에 숫자(`Num`)와 작은 단위(`Unit`) 글자 모양이 똑같이 두 벌 있습니다.
- 정하는 방법 세 가지:
  1. 한 파일이 export하고 다른 파일이 import → **불가.** lint 규칙 `react-refresh/only-export-components`(컴포넌트 파일은 컴포넌트만 export)에 막히고, 이 규칙은 끌 수 없습니다 (AGENT.md).
  2. 새 파일을 만든다 (예: `unitText.js`) → **불가.** 폴더 구조 시트에 없는 파일은 만들 수 없습니다.
  3. `styles/common.js`에 둔다 → **제안.** 명세서에는 목록에 이름 두 개만 더하면 됩니다. `fe_common.md` 90줄 "여러 곳 공통 모양만 styles/common.js"의 취지와 같고, 이미 `SlotBadge`, `CompareButton`을 이름만 목록에 넣은 선례가 있습니다.
- UI 번호(UI-28 등)를 붙일지는 선택입니다. `SlotBadge`, `CompareButton`처럼 이름만 넣어도 됩니다.
- **xlsx에 붙여 넣을 문장** (폴더 구조 시트 `styles/common.js` 줄의 두 번째 칸, 지금 문장 끝에 아래를 이어 붙임):
  `, Num (숫자 글자 모양, CM-12), Unit (작은 단위 글자 모양, CM-12)`
  → 붙인 뒤 전체: `Button, Card, Panel, Chip, Tag, EmptyBox, InlineError, IconButton, FormStack, FormField (UI-23), FormLabel (UI-24), FormLabelNote (UI-25), ChipGrid (UI-26), SegmentedFull (UI-27), SlotBadge (비교 A, B 칸 딱지), CompareButton ([+ 비교] 버튼, 담김과 비교 불가 모양), Num (숫자 글자 모양, CM-12), Unit (작은 단위 글자 모양, CM-12)`
- 이름을 더 구체적으로 `NumText`, `UnitText`로 바꾸자는 의견이 나오면 위 문장의 이름만 바꾸면 됩니다 (코드도 같은 이름으로 같이 바꿈). 지금 코드 이름은 `Num`, `Unit`입니다.

### A3 제안 (CM-04 `hits`, 간편 검색 `N/M 일치`)
- 시안의 간편 검색 상품 줄은 태그 자리 맨 앞에 `N/M 일치` 글과 초록 '충족' 태그(체크 아이콘)를 보여 줍니다. 고른 키워드 M개 중 N개를 충족했다는 뜻입니다.
- 서버 API-07 응답에 충족 키워드 수와 키워드별 이유 문구가 이미 있습니다.
- 제안 문구: `props: … hits (선택, 간편 검색용 { matched: 충족한 키워드 수, total: 고른 키워드 수, reasons: 키워드별 이유 문구 목록 }): 있으면 태그 맨 앞에 'N/M 일치' 글 + 초록 충족 태그, 없으면 지금과 똑같이 표시`
- 상세 검색, 맞춤 추천 줄은 `hits`를 안 넘기므로 그대로입니다.
- 구현은 CHORONG-64(SR-07~09) 담당과 같이 합니다. (`ProductRow.jsx`에 TODO 주석 있음)

---

## B. 코드가 겹치는 것: 누가 맞추나 (쉽게 말하면)

**한 줄 요약**: 내(조빌립, 47번, 68번) 코드와 겹치는 것은 **내가 내 쪽을 맞춥니다.** 45번(김채희), 51번이 맡은 파일은 건드리지 않고 아래처럼 알려만 드립니다. 명세서는 고치지 않아도 되는 항목들입니다.

| # | 무슨 일인가 | 누가 맞추나 | 쉬운 설명 |
| --- | --- | --- | --- |
| B1 | `options.js`의 기간 칩 숫자가 같은 값에 이름만 다르게 두 벌 (`LONG_TERM_PURPOSES`/`HOUSING_PURPOSES`, `SHORT_TERM_MONTHS`/`MONTHS_OPTIONS_DEFAULT`, `LONG_TERM_MONTHS`/`MONTHS_OPTIONS_HOUSING`) | **조빌립이 맞춥니다** | 47번 쪽 이름 3줄을 지우고 `getTermOptions`가 45번 이름을 쓰게 합니다. 45번 코드는 하나도 안 바뀝니다. 지우는 이름은 명세서에 없어서 명세서 수정도 필요 없습니다. |
| B2 | `일치도` 표기 (명세서는 이미 `추천도`): `RecommendCard.jsx` 245줄, `ReasonList.jsx` 66줄 '조건 일치도', `ToolShortcuts.jsx` 305줄 '일치도 96%', 주석 몇 곳 | 45번 김채희 | 45번 담당 파일이라 알려만 드립니다. 자세한 줄은 `fe_recommend_todo.md`에 있습니다. |
| B3 | `/recommend` 주소가 두 곳에 따로 있음: `RecommendDialog.jsx`의 `RESULT_PATH`, `credit/CreditResultCard.jsx`의 `RECOMMEND_PATH` (조장님 11-17 지적) | 45번, 51번 | 두 담당이 어디에 한 곳으로 둘지 정해서 합칩니다. |
| B4 | `RecommendCard.jsx`가 줄 전체를 버튼으로 두고 안에 하트, [+ 비교] 버튼이 또 있음 (버튼 안의 버튼) | 45번 김채희 | 조빌립의 `ProductRow.jsx`와 같은 방식으로 고치면 됩니다: 카드에서 role, tabIndex를 빼고 상품명을 `<button>`으로 둡니다. |
| B5 | `styles/common.js` 227줄 주석 "명세서 UI 번호는 변경 요청 중" (명세서에 UI-23~27이 이미 있어 낡은 주석) | 45번 김채희 | 45번이 만든 입력 칸 모양 위의 주석이라 알려만 드립니다. 지우거나 고치면 됩니다. |
