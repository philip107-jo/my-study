# CHORONG-82 인수인계 (2026-11-19, 리팩토링: 중복 코드 개선 및 비교 TODO 처리)

CHORONG-47(대출 비교), CHORONG-68(공통 부품) 작업을 이어서 **새 브랜치 `CHORONG-82-리팩토링-중복-코드-개선-및-비교-todo-처리`** 에서 합니다. 이 문서는 지금까지의 상태, 오늘 고친 것, 앞으로 할 것, 정해진 것과 안 정해진 것을 모았습니다. (이 문서는 `docs/` 아래라 git에 안 올라감, 각자 PC 전용)

읽는 순서: 이 문서 → `docs/history/CHORONG-47/문서_반영_목록.md` (명세서와 다른 점, 맨 위 '명세서 갱신 확인' 블록부터) → `fe_compare_todo.md`

---

## 1. 브랜치 만들기 (cmd)

지금 브랜치(`CHORONG-47-fe-대출-비교-기능-프론트엔드`)는 최신 develop(`766f0cf`, PR #40)과 똑같고 앞서는 커밋도 없습니다. 오늘 고친 파일 6개는 **커밋하지 않은 수정 상태**라서, 새 브랜치를 만들면 그대로 따라옵니다.

```
cd C:\Users\user1\Desktop\chorongs
git fetch origin
git status
git checkout -b CHORONG-82-리팩토링-중복-코드-개선-및-비교-todo-처리
git status
```

- `git status`에 아래 6개가 수정(M)으로 보이면 정상입니다. 다른 파일이 섞여 있으면 멈추고 확인합니다.
- 브랜치를 만들기 전에 `git fetch` 후 `git rev-list --left-right --count origin/develop...HEAD`가 `0 0`이 아니면(develop에 새 커밋이 생김) 먼저 `git merge origin/develop`을 합니다.
- `test-compare` 브랜치는 `ml/venv`(10,479개 파일)가 들어 있어 **절대 push하지 않습니다**. `stash@{0}`(CHORONG-68 쪽 임시 보관)도 건드리지 않습니다.
- `git add`, `git commit`, `git push`는 사용자가 직접 합니다. 에이전트는 명령만 알려 줍니다.

---

## 2. 오늘(11-19) 이미 고친 것 (커밋 전, 6개 파일)

명세서(11-19 11:38 갱신본)와 조장님 요청(11-19)에 맞춘 것입니다. lint 오류 0, 테스트 211개, build 통과를 확인했습니다.

| 파일 | 바꾼 내용 |
| --- | --- |
| `components/compare/CompareSidebar.jsx` | 추천 카드 딱지 `일치도 N%` → `추천도 N%` (화면 글 1곳, 주석 3곳). 조장님 요청, 명세서 CP-07도 `추천도` |
| `components/common/ProductRow.jsx` | 딱지 `추천도 N%` (화면 글, 주석). 19줄 주석에 `찜한 대출` 추가. 16줄의 "소분류는 변경 요청 중" 옛 주석 삭제 (명세서 CM-04에 소분류가 들어옴) |
| `components/compare/CompareEmptySlot.jsx` | 8줄 주석: 남은 상품 있으면 `startSwap`, 없으면 `clearTray`, 거르기는 상품 찾기 화면이 함 (CM-10, getCompareBlock) |
| `components/compare/CompareInput.jsx` | 6줄에서 "계산 기준 안내" 삭제 (안내는 ComparePage에 있음), 11줄 `methodOptions`는 "두 상품이 모두 가능한 방식, 2개 이상일 때만 칩" |
| `components/layout/Layout.jsx` | 19줄 주석에 `찜한 대출` 추가 |
| `store/useCompareStore.js` | `startSwap` 옛 주석 정리 (명세서가 두 번째 값을 정의함) |

문서(gitignored)도 고쳤습니다: `CHORONG-47/문서_반영_목록.md`(맨 위 '명세서 갱신 확인' 블록, R8 `/compare 안 막음` 줄 삭제, N6 취소 표시, R13 `추천도`, T16 해결), `CHORONG-47/fe_compare_history.md`, `CHORONG-68/fe_common_parts_history.md`에 오늘 기록과 되돌림이 적혀 있습니다. **이 작업의 이력은 CHORONG-47 폴더에 이미 있으니, CHORONG-82 이력(`docs/history/CHORONG-82/…`)에는 "CHORONG-47 이력 2026-11-19 참고" 한 줄만 적으면 됩니다.**

### 오늘 했다가 되돌린 것 (합의 전이라서)
- `NumText`, `UnitText`를 `styles/common.js`에 추가해 단위 표 합치기 → 명세서 목록에 이름이 없어서 되돌림
- `options.js`의 `LONG_TERM_*`, `SHORT_TERM_MONTHS` 지우고 45번 이름으로 합치기 → 45번 담당과 합의 전이라 되돌림
- 위 두 변경의 diff를 백업해 두었습니다: `%TEMP%\revert_backup\unit_options_changes.patch` (임시 폴더라 재부팅 등으로 지워질 수 있음. 필요하면 지금 `docs/history/CHORONG-82/`로 복사해 두세요)

---

## 3. 할 일

### 3-1. 단위 표 두 벌 정리 (CM-12, 68번 몫) — **결정됨, 바로 할 수 있음**
- 문제: `MoneyText.jsx`, `RateText.jsx`에 똑같은 `UNIT_SIZE`, `UNIT_WEIGHT`, `Num`, `Unit`이 두 벌
- **결정(11-19)**: 명세서는 안 고치고 import 방식으로 합칩니다. `MoneyText.jsx`가 위 4개를 `export`하고 `RateText.jsx`가 `import`해서 중복 약 20줄을 지웁니다. 고칠 파일은 이 두 개뿐.
- 두 파일의 `TODO(CM-12, CHORONG-68 …)` 주석은 "스타일은 MoneyText.jsx에서 가져다 씀" 설명으로 바꿉니다.
- 주의: 명세서 `fe_common.md` 90줄("styled-components 이름은 그 컴포넌트 파일 안에 둠, 여러 곳 공통 모양만 styles/common.js")의 취지와는 조금 어긋납니다. 70줄(`export default` 컴포넌트 하나)은 어기지 않습니다. PR 본문에 "명세서 변경 없이 MoneyText의 스타일을 RateText가 가져다 쓰는 방식으로 중복을 정리했다" 한 줄 적습니다.
- 참고: "목록에 먼저 추가" 방향은 43번 담당이 코드 주석으로 남긴 제안(`7c7dea8`)이고, 조장님이 요구한 방향이 아닙니다. 조장님 글은 "똑같이 두 벌"까지입니다.
- 이력: `CHORONG-68/fe_common_parts_history.md`에 적습니다 (68번 몫이므로).

### 3-2. `options.js` 기간 칩 상수 두 벌 — **합의 필요**
- 같은 값이 이름만 다르게 두 벌: `LONG_TERM_PURPOSES`/`HOUSING_PURPOSES`, `SHORT_TERM_MONTHS`/`MONTHS_OPTIONS_DEFAULT`, `LONG_TERM_MONTHS`/`MONTHS_OPTIONS_HOUSING`
- 47번 이름은 `getTermOptions` 한 곳만 쓰고, 45번 이름은 `validate.js`, `StepMoney`, `RecommendResult`가 씀. 명세서에는 지우려는 3개 이름이 없어서 **명세서 수정은 필요 없음**, 합의만 필요.
- 합의할 것: 45번 이름(`HOUSING_*`, `MONTHS_OPTIONS_*`)으로 통일할지. 합의되면 `getTermOptions`가 45번 이름을 쓰게 하고 47번 상수 3줄을 지웁니다 (`options.test.js`가 `getTermOptions`를 이미 검사함).
- 대상: 조장님, 45번 담당(김채희)

### 3-3. `sample.js`, `sample.test.js` 삭제 (선택, 조장님 말씀)
- 테스트 세팅 확인용 임시 파일로, 서로만 쓰고 다른 곳은 안 씁니다. 이 프로젝트 규칙상 에이전트는 파일을 지우지 않으므로 **사용자가 직접 지우거나, 경로를 보여 주고 허락을 받은 뒤** 합니다.

### 3-4. 비교 TODO 처리 목록
| 항목 | 상태 | 해야 할 일 |
| --- | --- | --- |
| 찜 API (MEM-04, API-20) | 서버 없음 | 서버가 올라오면 `api/axiosInstance.js`의 `PREPARING_APIS`에서 `/api/me/likes` 줄만 지움. 응답이 `list` + `total, page, size`인지 확인 (명세서도 `list`로 정리됨). `LikeButton.jsx`, `LikedList.jsx`, `useMyStore.js`의 TODO(API-20)도 같이 지움 |
| 최근 본 API (MEM-08, API-21) | 서버 없음 | `PREPARING_APIS`의 `/api/me/recent-views` 줄 지우고 `ProductDetailSheet.jsx`의 TODO(MEM-08) 확인. 회원일 때만 기록 (명세서 CM-07) |
| 비교 내역 저장 (MEM-10, API-23) | 서버 없음 | `PREPARING_APIS`의 `/api/me/compares` 줄 지우고 [결과 저장] 성공, 409 `이미 저장한 조합이에요` 동작 확인. 지금은 `ComparePage`가 `error.isPreparing`이면 '아직 준비 중인 서비스예요' 알림 |
| 비교 화면 사이드바 (CP-07) | 서버 없는 동안 회원이 열면 찜, 최근 본 구역이 **숨겨짐** (`isPreparing` → `guest` 처리, `CompareSidebar.jsx` 236줄). `문서_반영_목록` T17에 "오류 카드로 뜸"이라고 적힌 건 **옛 설명** (43번이 `PREPARING_APIS`를 넣기 전) | 서버가 올라오면 구역이 나타나고, 0건이면 숨겨지고, 실패하면 '목록을 불러오지 못했어요' + [다시 시도]가 뜨는지 확인. T17 문구는 고치면 좋음 |
| 상품 찾기 비교함 필터 (CHORONG-64) | 안 함 (64번 몫) | `PurposeFilterBanner` 표시, `getCompareBlock`이 `null`이 아닌 상품 숨김 (SR-02, SR-07, CM-10). 이 브랜치가 아니라 64번 브랜치에서 |
| 간편 검색 `N/M 일치` (CM-04, SR-07~09) | **안 정해짐, 64번 몫** | 제안: `ProductRow`에 `hits = { matched, total, reasons }` props 추가, 없으면(null) 지금과 동일. 명세서 CM-04 props(`product, from, rank, matchRate`)에 없으니 **명세서에 먼저 추가** 필요. 서버 API-07 응답에 `충족 키워드 수`, 키워드별 이유 문구가 있음 |
| ProductRow 버튼 안의 버튼 (CM-04, 68번) | **안 정해짐** | 줄 전체가 `role="button"`인데 안에 하트, [+ 비교]가 있음. 상품명을 `<button>` 하나로 두고 줄 클릭은 마우스용으로만 두는 방식을 검토. `RecommendCard`(45번 담당)도 같은 문제라 같이 정해야 함. 동작과 접근성이 바뀌는 변경이라 합의 뒤에 |
| 빈 칸 카드 안내 글 (CP-04) | 명세서 그대로 | 명세서도 `자금 용도({용도})가 겹치는 상품`만 말함. 상환 방식도 겹쳐야 하니 문구를 바꿀지는 **명세서 쪽 결정** (지금은 명세서와 일치) |

---

## 4. 다른 사람에게 알릴 것
| 대상 | 내용 |
| --- | --- |
| 45번 담당 (김채희) | 명세서가 `추천도`로 바뀌었는데 `RecommendCard.jsx`(`일치도`), `ReasonList.jsx`(`조건 일치도`), `ToolShortcuts.jsx`(샘플 딱지 `일치도 96%`)가 아직 `일치도`. `styles/common.js` 227줄의 "명세서 UI 번호는 변경 요청 중" 주석도 낡음. `RecommendCard`, `RecommendResult`는 47번이 [+ 비교]와 비교함 필터를 연결하느라 고쳤으니 PR 본문에 적음 |
| 45, 51번 담당 | `RESULT_PATH`(`RecommendDialog.jsx`)와 `RECOMMEND_PATH`(`CreditResultCard.jsx`)가 둘 다 `'/recommend'`로 따로 있음 (조장님 11-17 지적, 아직 안 합침) |
| 조장님 | ① 기간 칩 상수 이름 통일(3-2) ② 단위 표를 import 방식으로 푼 것 보고(3-1) ③ `N/M 일치` props를 명세서에 넣을지 |
| 42번 담당 (BE) | MEM-04(응답 `list`), MEM-08, MEM-10 일정 |

---

## 5. 작업 규칙 메모 (이 프로젝트)
- 작업 폴더 `Skills.md`와 루트 `AGENT.md`를 먼저 읽음. 명세서는 `docs/standard/`에서 읽고 **고치지 않음** (원본 xlsx를 고친 뒤 사본을 다시 만듦)
- 여러 이슈가 같이 쓰는 파일(`styles/`, `utils/`, `constants/`, `api/`, `store/`)은 **필요한 것만 추가**하고 기존 것은 안 고침. 예외는 조장님이 요청한 정리
- **파일, 폴더를 지우지 않음.** 변경 버리기(`git checkout --`)도 지우기로 보고 허락을 받음
- 파일 2개 이상을 고치면 코드를 쓰기 전에 파일 목록과 바꿀 내용을 한 줄씩 보여 줌
- 합의가 필요한 건 **먼저 고치지 않고 묻기** (오늘 `options.js`와 `NumText`를 먼저 고쳤다가 되돌림)
- 사용자 터미널은 Windows cmd. PowerShell 전용 문법 금지. 커밋은 `-m` 두 번, 둘째에 `Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>`
- 실제 값 `.env` 파일은 읽지 않음
- 로컬 백엔드 실행(cmd): `cd backend` → `set DB_URL=jdbc:oracle:thin:@//localhost:1521/xe` → `set DB_USERNAME=C##TEST` → `set DB_PASSWORD=TEST` → `mvnw.cmd spring-boot:run`
- 작업 완료 검사(PowerShell, `frontend` 폴더): `npm run lint; npm test; npm run build` → 통과 결과를 보여 주고 "끝났다"고 함. 지금 기준 lint 오류 0, 테스트 7개 파일 211개, build 성공

## 6. 이번 작업을 하며 알게 된 원인과 예방 (중복 코드)
- 이슈를 나눠 동시에 만들면 서로 못 봄 (45번 `HOUSING_*` vs 47번 `LONG_TERM_*`), 한 이슈 안에서도 작은 함수를 복사해 쓰다가 생김 (`getRateText`)
- **새 함수, 상수, 스타일을 만들기 전에 `git grep`으로 같은 이름, 같은 값이 develop에 있는지 먼저 확인**, 같은 게 두 번째 파일에서 필요해지면 `utils/`, `constants/`로 옮김, develop은 자주 받음

## 7. 이 브랜치에서 할 일의 순서 (제안)
1. 브랜치를 만들고 `git status`로 오늘 고친 6개가 따라왔는지 확인
2. 오늘 고친 6개를 먼저 커밋 (제목에 `CHORONG-82`)
3. 3-1 단위 표 import (바로 가능)
4. 3-2, 3-3은 합의, 허락이 나오면
5. 3-4 서버가 올라온 항목은 `PREPARING_APIS` 줄을 지우고 동작 확인
6. 이력(`docs/history/CHORONG-82/`)을 적고 검사 후 PR (대상 `develop`, 제목에 `CHORONG-82`)

PR 본문에 적을 것: 중복 정리(단위 표 import, 합의되면 기간 칩 상수), `추천도` 표기, 옛 주석 정리, 명세서와 어긋난 곳 없음, 서버 대기 항목은 TODO로 남김.
