# CHORONG-82 문서 반영 요청 (2026-11-25)

CHORONG-82에서 비교 흐름을 다시 만들면서 **명세서와 달라진 점** 중 아직 반영되지 않은 것만 모았습니다.

- 기준 사본: `docs/standard/` 2026-11-24 12:02 판 (줄 번호도 이 판 기준)
- 원본 xlsx를 고친 뒤 사본 md를 다시 만듦 (사본은 직접 고치지 않음)

---

### 1-1. 없앤 기능

| No. | 위치 | 지금 명세서 | 바꿀 것 |
| --- | --- | --- | --- |
| F1 | `fe_compare.md` 13줄 CM-09 비교함 CompareTray, 217줄 SCR-20 (`fe_common_parts.md` 788줄), `fe_common.md` 290줄 번호 표, 208줄 폴더 설명 | 비교할 상품 2개를 담는 떠 있는 바, [비교하기], 흔들기 | 항목 삭제. `components/common/CompareTray.jsx` 파일 삭제함 |
| F2 | `fe_common_parts.md` 43줄 LY-03 동작, 보이는 것 | 비교함이 있으면 다른 화면 이동을 막고 알림 + 바 흔들기 / '화면 위에 뜨는 것'에 비교함 | 이동 막기 삭제, '비교함' 빼고 '담긴 상품 확인 팝업'(F5) 추가 |
| F3 | `fe_common_parts.md` 421줄 UI-21 | `비교함이 떠 있으면 그 위로` | 삭제 |
| F4 | `fe_design.md` 103줄 겹침 순서, `fe_common.md` 173줄 E15 '어디서', 224줄 persist 설명 | `비교함 58`, '비교함' 표현 | 비교함 줄 삭제, 표현은 '비교 화면 상품'으로 |

### 1-2. [+ 비교] 동작 (CM-04, CM-07, RC 카드 공통)

| No. | 위치 | 지금 명세서 | 바꿀 글 |
| --- | --- | --- | --- |
| F5 | `fe_common_parts.md` 536줄 CM-04 [+ 비교], 575줄 CM-07 | 비교함에 담음 (addTray, 담긴 상품 누르면 뺌), 담겼으면 'A 담음' | 아래 '[+ 비교] 규칙' |

**[+ 비교] 규칙** (보이는 화면은 지금과 같음: 맞춤 추천, 상품 찾기, 찜한 대출의 게시(ON) 상품)

| 비교 화면 상태 | 누른 상품 | 동작 |
| --- | --- | --- |
| 비어 있음, 또는 같은 상품 하나 | - | A에 넣고 `/compare`로 이동 |
| 하나만 담김 | 비교 가능 | 담긴 상품은 두고 빈 칸에 넣고 `/compare` |
| 하나만 담김 | 비교 불가 | **팝업 ①** |
| 두 개 담김 | 이미 비교 중 | 바꾸지 않고 `/compare` |
| 두 개 담김 | 다른 상품 | **팝업 ②** |
| 고르는 중(1-3) | 남은 상품과 비교 가능 | 그 칸에 넣고 `/compare` |
| 고르는 중 | 비교 불가 | 알림 (CM-09에 있던 두 문구 그대로: 자금 용도 / 상환 방식) |
| 고르는 중 | 남겨 둔 상품 ('A 담음') | 고르기 끔, 화면 그대로 |

- **팝업 ①** (우측 상단 X): '이미 담겨 있는 상품이 있어요' + '방금 고른 상품은 {자금 용도가 / 상환 방식이} 겹치지 않아 함께 비교할 수 없어요.' + 담긴 상품의 비교 카드(버튼 없이) + [담겨진 상품 뺄래요] (담긴 상품 빼고 누른 상품을 A에, `/compare`) + [담겨진 상품으로 비교] (담긴 상품 두고 상세 검색에서 고르는 중). X, Esc, 바깥 클릭은 닫기만. 담긴 상품이 숨김이면 팝업 없이 누른 상품으로 바꿈. 담긴 상품 조회(API-03) 실패: 알림 '담긴 상품을 확인하지 못했어요. 다시 눌러 주세요'
- **팝업 ②**: '대출 비교에 담겨 있던 상품이 사라져요. 비교하시겠어요?' + [취소] [비교하기] (누른 상품을 A에, B는 빈 칸)

### 1-3. 고르는 중 (새 개념: 비교 화면의 한 칸을 고르러 나온 상태)

| No. | 위치 | 바꿀 글 |
| --- | --- | --- |
| F6 | `fe_compare.md` 89줄 CP-04 동작 | [비교 상품 고르기] → 상세 검색(`/search`)으로, 그 빈 칸을 고르는 중. (startSwap, 비교함 문장 삭제) |
| F7 | `fe_compare.md` 70줄 CP-03 동작 | `[다른 상품으로 비교] → 남길 상품만 두고 상품 찾기로` → '누른 카드의 칸을 바꾸러 상세 검색으로. 반대 칸 상품은 그대로.' props에 `otherProduct`(반대 칸 상품), `isReadOnly`(X와 버튼을 숨김, 팝업 ①에서 씀) 추가 |
| F8 | `fe_common_parts.md` LY-03 | 비교 화면에 상품이 하나만 담긴 채 거기서 메뉴로 상품 찾기, 맞춤 추천, 찜한 대출에 가도 고르는 중. **세 화면 밖(랭킹, 계산기, 마이페이지, 비교 화면 등)으로 가면 풀림.** 풀리면 거르지 않고 전부 보임 |
| F9 | `fe_compare.md` 27줄 CM-10 | 아래 '필터 띠' |
| F10 | `fe_search.md` 24줄 SR-02, `fe_common_parts.md` 536줄 CM-04 | 고르는 중: 남겨 둔 상품 줄은 'A 담음', 비교 불가 상품은 '비교 불가'. 상세 검색은 아래 '상세 검색' |
| F11 | `fe_search.md` 96줄 SR-07 | 고르는 중: 남겨 둔 상품을 결과 맨 위에 둠 (결과에 없으면 일치 표시 없이). cmp는 남겨 둔 상품 번호 하나 |
| F12 | `fe_recommend.md` 131줄 RC-07 | `비교함에 상품이 있으면 …` → '고르는 중이면 남겨 둔 상품과 비교할 수 없는 상품은 숨김 (남겨 둔 상품은 보임)'. 상태 값 `useCompareStore(tray)` → `useCompareStore(pickBase)` |
| F13 | `fe_member.md` 198줄 MY-04 | 찜한 대출 위에 필터 띠(CM-10). 고르는 중이 아니면 안 보임 |

**필터 띠 (CM-10)**
- 띠 두 개를 위아래로: ① 고르는 중 띠: 전구(bulb) + '**{상품명}** 상품과 비교할 상품을 골라 주세요' + '**상품 찾기, 맞춤 추천, 찜한 대출 밖으로 이동하면 고르기가 끝나요**' ② 거르기 띠: **아이콘 bulb → alert**
- 기준은 비교함 첫 상품이 아니라 **남겨 둔 상품**
- 거르기 띠 딱지: `딱지는 흰 알약에 주황 글자` → '딱지는 흰 알약에 회색 글자. 자금 용도 딱지 묶음과 상환 방식 딱지 묶음 사이에 세로 구분선 (상환 방식 딱지가 없으면 구분선도 없음)'

**상세 검색 (SR-02), 고르는 중에만**
- 남겨 둔 상품의 자금 용도, 상환 방식 칩을 켜진 것으로 보여 줌 (주소, 최근 검색에는 안 남김, 눌러도 안 꺼짐)
- 남겨 둔 상품을 1쪽 맨 위에 고정 (칩, 검색어, 쪽과 상관없이. 서버 건수는 그대로)
- 조건, 최근 검색이 없어도 맞춤 추천 바로가기 대신 거른 목록
- 거르기로 지금 쪽이 없어지면 자동으로 1쪽
- 모두 빠졌을 때 문구 `비교함 상품과 …` → `비교할 상품과 …`
- cmp 설명 `비교함에 상품이 있으면 …` → '고르는 중이면 남겨 둔 상품 번호를 cmp로'

### 1-4. 그 밖

| No. | 위치 | 지금 | 바꿀 글 |
| --- | --- | --- | --- |
| F14 | `fe_compare.md` 41줄 CP-01 동작 | (없음) | B를 빈 칸으로 보여 줄 때(A와 같은 상품, 자금 용도가 안 겹침)는 저장에서도 B를 비움 |
| F15 | `fe_common_parts.md` 88줄 LY-05, 132줄 LY-14, `fe_search.md` 18줄 SR-01 | 상품 찾기 → `/search/simple` | 상품 찾기 → `/search` (상세 검색). 하위 메뉴의 간편 검색, 상세 검색은 그대로 |
| F16 | ST-04 (`fe_compare.md` 251줄, `fe_common_parts.md` 835줄, `fe_recommend.md` 362줄, `fe_search.md` 225줄) | tray, swapSlot, from, shakeCount, addTray, goCompare, startSwap 등 | 아래 '붙여 넣을 ST-04' |
| F17 | `fe_compare.md` 94줄, `fe_member.md` 244줄 등 '쓰는 곳' | `Layout (이동 막기)`, `CompareTray` | `Layout (팝업, 고르기 켜고 끄기)`, CompareTray 삭제. 로그아웃, 탈퇴는 `setPair(null, null)` + `setPickSlot(null)` |

**붙여 넣을 ST-04**

```
- 담는 값: pair: 비교 화면 두 상품 번호 [A, B] (빈 칸은 null) / pickSlot: 고르는 중인 칸 (null, 'A', 'B') / pickBase: 고르는 동안 남겨 둔 상품 { productId, companyName, productName, purposes, methods } / pending: 바로 넣을 수 없는 새 상품 { item, isFull } / pickProduct: 남겨 둔 상품 상세 (검색 결과 맨 위 고정용)
- 바꾸는 함수: startCompare(product) ([+ 비교]. 비교 화면으로 이동해도 되면 true) / resolvePending(keepStored), clearPending() / setPickSlot(칸, 남길 상품) (칸이 null이면 끔) / setPickProduct(상세) / setPair(A, B), putPair(id), clearSlot(칸) / canCompare(product, other)
- 같이 내보내는 함수: getComparePage, getCompareBase, getPickBaseSlot, getPinnedProduct, useCompareItems, getPurposes, hasSamePurpose, getMethods, hasSameMethod, getMethodLabels, getCompareConditionText, getCompareBlock
- 저장 위치: persist + sessionStorage (pair, pickSlot, pickBase), pending과 pickProduct는 메모리만
```
