# 08_pandas 학습 정리

> 저장소: `sjlimiei26/6_Python` → `08_pandas`
> 가상의 주식 데이터(가온국 증시)를 소재로 **pandas 기초 → 데이터 정제 파이프라인**까지 단계별로 다루는 실습 코드.
> 구성: 실습 스크립트 8개 + 공통 유틸 2개 + CSV 4개

---

## 0. 데이터 구성 (`data/`)

| 파일 | 행수 | 내용 |
|---|---|---|
| `prices.csv` | 90,000 | 시세 **정제본** — `code, date, open, high, low, close, volume, change, changeRate` |
| `raw-prices.csv` | 92,721 | 시세 **오염본** — 날짜 형식 3종 혼재, `N/A`/`-`/빈칸, 천단위 콤마, 중복 행 |
| `companies.csv` | 120 | 종목 정보 **정제본** — 섹터코드/섹터명, 시장, 상장일, 대표, 주소, 직원수, 상장주식수, 시총 |
| `raw-companies.csv` | 122 | 종목 정보 **오염본** — 공백, 대소문자 불일치, 전각문자, 결측, 중복 |

### 정제 목표
| 항목 | 목표 |
|---|---|
| 행수 | 90,000 |
| 종목수 | 120 |
| 종목별 행수 | 750 |

---

## 1. 공통 유틸

### `utils/config.py` — 경로·설정 모음
```python
BASE_DIR = Path(__file__).parent.parent
DATA_DIR = BASE_DIR / "data"          # / : 경로 연결 연산자
RAW_PATH = DATA_DIR / "raw-prices.csv"
ENCODING = "utf-8-sig"

STEP_DIR = DATA_DIR / "steps"
STEP_DIR.mkdir(parents=True, exist_ok=True)   # 폴더 없으면 생성

def step_path(filename): ...   # 단계별 중간 결과 경로
def path(name): ...            # data 폴더 내 파일 경로
```

### `utils/loader.py` — 데이터 로더
- `load_csv(dedup=True)` : `raw-prices.csv`를 읽으면서 `na_values`, `thousands` 적용 → `to_datetime(format='mixed')` → 중복 제거 → `sort_values` → `reset_index(drop=True)`
- `load_prices()` : `prices.csv` (정제본)
- `load_companies(raw=False)` : `raw=True`면 오염본을 `dtype=str, keep_default_na=False`로 원본 그대로 읽음

---

## 2. `01_basic.py` — Series / DataFrame 기초

### Series (라벨링된 1차원 배열)
```python
s = pd.Series([10, 20, 30, 40])          # 인덱스 미지정 시 0부터 자동 생성
s = pd.Series(datas, name="sample")      # name → DataFrame의 열 이름이 됨
s = pd.Series(datas, index=['a','b','c','d'])
```
- 속성: `s.index`, `s.values`, `s.name`
- **연산은 순서가 아니라 인덱스 기준으로 수행**
- 한쪽에만 있는 인덱스는 결과가 `NaN`

### DataFrame (2차원 테이블)
- Series 여러 개를 묶은 구조. 행(인덱스) + 열(컬럼)
- 속성: `df.dtypes`, `df.shape`, `df.index`, `df.columns`

### `read_csv` 주요 옵션
| 옵션 | 역할 |
|---|---|
| `encoding` | `utf-8` / `utf-8-sig` |
| `parse_dates=["date"]` | 해당 열을 datetime으로 변환 |
| `na_values=["N/A", "-"]` | 결측으로 취급할 문자열 지정 |
| `thousands=","` | `1,000,000` 형태를 숫자로 변환 |

### 핵심 포인트
- `read_csv`는 열마다 타입을 추론하는데, **한 열에 숫자가 아닌 값이 하나라도 있으면 그 열 전체가 문자열**이 된다.
- `parse_dates`는 **해당 열 전체가 같은 형식일 때만** 적용됨.
  형식이 섞여 있으면 → `df['date'] = pd.to_datetime(df['date'], format='mixed')`

### 불러온 뒤 점검
`df.head(n)` · `df.shape` · `df.info()` · `df.dtypes`

---

## 3. `02_select.py` — 조회 (SQL 대응표)

| SQL | Pandas |
|---|---|
| `SELECT a, b FROM t` | `df[['a','b']]` |
| `WHERE close > 100000` | `df[ df['close'] > 100000 ]` |
| `WHERE 조건1 AND 조건2` | `df[(조건1) & (조건2)]` |
| `WHERE 조건1 OR 조건2` | `df[(조건1) \| (조건2)]` |
| `ORDER BY col` | `df.sort_values(col, ascending=True/False)` |
| `LIMIT n` | `df.head(n)` |
| `DISTINCT col` | `df[col].unique()` / 개수는 `nunique()` |
| `COUNT(*) ... GROUP BY` | `df[col].value_counts()` |
| `IN (...)` | `df[col].isin([...])` |
| `BETWEEN a AND b` | `df[col].between(a, b)` |

### 대괄호 개수
```python
df['close']     # Series      (shape: (n,))
df[['close']]   # DataFrame   (shape: (n, 1))
```
> 바깥 대괄호 = 데이터프레임에서 "꺼내기", 안쪽 대괄호 = "목록" 지정

### loc vs iloc
| | `loc` (Label Location) | `iloc` (Integer Location) |
|---|---|---|
| 기준 | 인덱스/컬럼 **라벨** | 0부터 시작하는 **위치** |
| 슬라이싱 | 끝 라벨 **포함** | 끝 위치 **제외** |
| 예 | `df.loc[0:2]` → 3건 | `df.iloc[0:2]` → 2건 |

```python
# loc은 행 조건 + 열 선택 동시 가능
df.loc[ df['close'] > 200_000, ['code', 'close', 'volume'] ]

# 인덱스 변경 후 라벨로 조회
indexed = df.set_index('code')
indexed.loc['G0001', ['close', 'changeRate']]
```

---

## 4. `03_columns.py` — 열(컬럼) 다루기

```python
day['range'] = day['high'] - day['low']          # 열 추가
day = day.drop(columns=['temp'])                 # 열 삭제
renamed = day.rename(columns={'close': '종가'})   # 열 이름 변경
```

### `.str` 접근자 — 문자열 메소드를 모든 행에 일괄 적용
```python
day['code'].str.len()
day['code'].str[1:]
day['code'].str.startswith('G00').sum()
sample.str.strip()
```

### `astype` vs `to_numeric`
```python
sample2.astype('float64')                     # ValueError! 값 하나만 이상해도 전체 실패
pd.to_numeric(sample2, errors='coerce')       # 변환 불가한 값만 NaN 처리
pd.to_numeric(sample2.str.replace(',',''), errors='coerce')   # 콤마 먼저 제거
```
> `astype`은 전부 아니면 전무, `to_numeric(errors='coerce')`는 오염 데이터만 NaN으로 남기고 나머지는 변환.

---

## 5. `04_agg_transform.py` — 그룹화 (groupby)

### agg — 그룹 수만큼 행 반환 (요약)
```python
df.groupby('code')['close'].mean()

summary = df.groupby('code').agg(
    평균종가=("close", "mean"),
    최고가=("close", "max"),
    거래일수=("date", "count")
)
# .agg(새로운_열_이름=(계산할_기존_열, 적용할_함수))
```

### transform — 원본과 같은 행 수로 반환 (후속 연산용)
```python
df['code_mean'] = df.groupby('code')['close'].transform('mean')
```

| | agg | transform |
|---|---|---|
| 반환 행 수 | 그룹 수 | 원본 데이터 수 |
| 용도 | 결과 요약 확인 | 원본 df와 연산 이어가기 |

---

## 6. `05_diagnose.py` — 진단

> 정제 전 점검 순서: **타입 → 중복 → 결측 → 이상치 → 검증**

### 기본 점검
`df.shape` · `df.head(3)` · `df.dtypes` · `df.info()` · `df.describe()`
- `describe()`는 숫자형 열만 통계가 나옴 → 문자열로 잘못 읽힌 열(`close`, `volume`) 발견
- `info()`의 non-null 개수가 전체 행수와 다르면 결측 존재

### 결측 확인
```python
na = df.isnull().sum()     # isna()와 동일
```
- `read_csv`는 `N/A` 정도만 자동으로 NaN 처리 → 나머지 오염값은 놓침
- 원본 그대로 보려면:
```python
df2 = pd.read_csv(RAW_PATH, encoding=ENCODING,
                  keep_default_na=False,   # 자동 결측 변환 끄기
                  dtype=str)               # 모든 열 문자열로
```
그 후 `(df2[col] == 'N/A').sum()`, `== '-'`, `== ''` 로 실제 오염 개수 확인
- 해결책: `na_values=['N/A', '-', '']`

### 오염값 탐색
```python
df['close'].value_counts(dropna=False)   # dropna=False → NaN도 하나의 값으로 집계
pd.to_numeric(df2[col], errors="coerce").isna().sum()   # 숫자 변환 불가 건수
```

### 중복 확인
```python
df.duplicated(subset=['code', 'date']).sum()
# 원본과 같은 길이의 True/False 반환. 첫 번째는 False, 두 번째부터 True
```

### ⭐ 날짜 형식 차이로 숨어 있던 중복 (이 파일의 핵심)
데이터에 `2026-09-18`, `20260918`, `2026.09.18` 세 형식이 섞여 있음.
```python
lens = df['date'].astype(str).str.len().value_counts()   # 8글자 / 10글자 구분
dot_cnt = df['date'].astype(str).str.contains(r"\.", regex=True).sum()
```
문자열 상태에서는 다른 값으로 취급되어 중복으로 잡히지 않지만,
`to_datetime(format='mixed')`로 변환하면 같은 날짜가 되어 **중복이 드러남**.

```python
temps['date'] = pd.to_datetime(temps['date'], format='mixed')
temps.duplicated(subset=['code','date']).sum()   # 변환 후 증가
```

---

## 7. `06_outlier.py` — 이상치 탐색

### 전처리 (Step 1)
```python
NUM_COLS = ['open','high','low','close','volume','change','changeRate']
for col in NUM_COLS:
    df[col] = pd.to_numeric(df[col].astype(str).str.replace(',','',regex=False),
                            errors='coerce')

df['date'] = pd.to_datetime(df['date'], format='mixed')
df = df.drop_duplicates(subset=['code','date'], keep='first').reset_index(drop=True)
# → 약 90,000행
```

### 중간 결과 저장 (pickle)
```python
df.to_pickle(step1_path)        # 저장, 반환값 없음
df = pd.read_pickle(step1_path) # 읽기, DataFrame 반환
```

### 이상치 탐지 3가지 기준

**① 종목 중앙값(median) 대비**
```python
med = df.groupby('code')['close'].transform('median')
(df['close'] > med * 50).sum()    # 너무 큰 값
(df['close'] < med * 0.05).sum()  # 너무 작은 값
```

**② IQR (사분위 범위)**
```python
q1, q3 = df['close'].quantile([0.25, 0.75])
iqr = q3 - q1
lo, hi = q1 - 1.5*iqr, q3 + 1.5*iqr
out_mask = (df['close'] < lo) | (df['close'] > hi)
```
- 평균·표준편차와 달리 극단값에 흔들리지 않아 이상치 탐지에 사용
- **한계**: 전체 기준으로 하면 하한선이 음수로 나옴(종가는 음수 불가) → **종목별로 적용해야 함**
```python
def is_outlier(data):
    q1, q3 = data.quantile([0.25, 0.75])
    iqr = q3 - q1
    lo, hi = q1 - 1.5*iqr, q3 + 1.5*iqr
    return (data < lo) | (data > hi)

result = df.groupby('code')['close'].transform(is_outlier)
```

**③ 논리 검증**
```python
result2 = (df["close"] > df["high"]) | (df['close'] < df['low'])  # 종가가 고가/저가 벗어남
result3 = df['volume'] < 0                                        # 거래량 음수
```

### 이상치 처리 (Step 2)
삭제가 아니라 **결측으로 전환**
```python
mask = result | result2
df.loc[mask, 'close'] = pd.NA
df['close'] = pd.to_numeric(df['close'], errors='coerce')
df.loc[result3, 'volume'] = pd.NA
df.to_pickle(step_path('_step2.pkl'))
```

---

## 8. `07_missing.py` — 결측치 처리

### 3가지 전략
| 전략 | 메소드 | 판단 기준 |
|---|---|---|
| 삭제 | `dropna(subset=, how=)` | 삭제해도 분석이 가능한가? |
| 대치 | `fillna(값/평균/중앙값)` | 적절한 대표값이 있는가? |
| 보간 | `interpolate()` | 시계열인가? 앞뒤 값을 이어도 되는가? |

### ⭐ 보간은 반드시 종목별로
```python
demo['close'].interpolate()                                        # ✗
df.groupby('code')['close'].transform(lambda s: s.interpolate())   # ✓
```
> groupby 없이 보간하면 **앞 종목의 마지막 값과 다음 종목의 첫 값을 이어버림**.
> groupby를 쓰면 같은 종목 안에서만 연결됨.

### 열 별로 다른 전략 적용
```python
OHLC = ['open','high','low','close']

# 1) 종목별 보간
for col in OHLC:
    df[col] = df.groupby('code')[col].transform(lambda s: s.interpolate())

# 2) 맨 앞/뒤에 남은 결측은 ffill + bfill
for col in OHLC:
    df[col] = df.groupby('code')[col].transform(lambda s: s.ffill().bfill())

# 3) volume은 결측 유지 (0으로 채우면 데이터 왜곡)
```
- `.ffill()` : 바로 앞의 값으로 채움
- `.bfill()` : 바로 뒤의 값으로 채움

---

## 9. `08_merge.py` — 병합 (SQL JOIN)

### 왜 나눠서 저장하나 (정규화)
시세 90,000행에 섹터명을 매번 저장하면 같은 문자열이 수천 번 반복됨
→ 분리 저장하고 식별코드로 연결. **저장할 때는 나누고, 분석할 때는 합친다.**

### 기본 사용법
```python
left_df.merge(right_df, on="기준열", how="방식")
# how : "inner" / "left" / "right" / "outer"

m = prices.merge(companies, on='code', how='left')
```

### 열 이름 충돌
같은 이름의 열이 양쪽에 있으면 `_x`, `_y`가 붙음 → `suffixes`로 지정
```python
p2.merge(companies[['code','name']], on='code',
         suffixes=('_price', '_company'))
```

### 키가 안 맞는 4가지 원인
| 원인 | 확인 방법 |
|---|---|
| ① 공백 | 앞뒤: `(s != s.str.strip()).sum()` / 중간: `s.str.contains(r"\S\s+\S", regex=True)` |
| ② 대소문자 | `s.nunique()`, `s.unique()` vs `s.str.upper().unique()` |
| ③ 전각문자 | `ord(ch)`가 `0xFF01~0xFF5F` 또는 `0x3000` 인지 검사 + `s.map(함수)` |
| ④ 타입 불일치 | `str` vs `int64` 기준열로 merge 시 **오류 발생** |

```python
# map(함수) : 시리즈 각 값에 함수를 적용해 같은 타입(시리즈)으로 반환
fw = raw_comp[raw_comp['name'].map(has_fullwidth)]
```

### 누락 데이터 추적
```python
chk = prices.merge(part, on='code', how='outer', indicator=True)
chk['_merge'].value_counts()
# both / left_only / right_only
```
> `how='inner'`로 합쳐서 행이 줄었을 때는 원인 파악이 어려움.
> `how='outer'` + `indicator=True`로 어떤 데이터가 빠졌는지 확인할 수 있음.

---

## 전체 흐름 요약

```
01~04  pandas 문법 익히기
       Series/DataFrame → 조회 → 열 조작 → 그룹화

05~08  raw-prices.csv 정제 파이프라인
       05 진단    : 타입/중복/결측 상태 파악, 정제 목표 수립
       06 이상치  : 숫자·날짜 변환 → 중복 제거 → _step1.pkl
                    IQR/중앙값/논리검증으로 탐지 → NaN 전환 → _step2.pkl
       07 결측    : 종목별 보간 + ffill/bfill, volume은 결측 유지
       08 병합    : companies와 merge, 키 불일치 원인 점검
```

## 꼭 기억할 포인트

1. 한 열에 숫자 아닌 값이 하나라도 있으면 **열 전체가 문자열**이 된다.
2. `parse_dates`는 형식이 통일된 경우만 → 섞였으면 `to_datetime(format='mixed')`.
3. `astype`은 전부 아니면 전무, `to_numeric(errors='coerce')`는 오염값만 NaN.
4. **날짜를 타입 변환하면 숨어 있던 중복이 드러난다.**
5. IQR은 전체가 아니라 **그룹(종목)별**로 적용해야 의미 있다.
6. 보간은 반드시 `groupby`와 함께 — 안 그러면 다른 종목끼리 이어진다.
7. 모든 결측을 채울 필요는 없다. `volume`처럼 **유지가 맞는 경우**도 있다.
8. merge가 안 맞으면 공백 · 대소문자 · 전각문자 · 타입을 의심한다.
