# 파이썬 가변/불변 객체와 판다스 view/copy

## 1. 가변(Mutable) vs 불변(Immutable)

### 정의

객체를 만든 뒤 **그 객체 내부의 값을 바꿀 수 있느냐**가 기준이다.

| 구분 | 타입 |
|---|---|
| 불변 | `int`, `float`, `bool`, `str`, `tuple`, `frozenset`, `bytes` |
| 가변 | `list`, `dict`, `set`, `bytearray`, 대부분의 사용자 정의 클래스 |

### id로 확인

```python
# 불변: 값을 바꾸면 새 객체가 생기고 변수가 그쪽을 가리킴
s = "hello"
print(id(s))        # 140234...
s += " world"
print(id(s))        # 140891... (주소 변경)

# 가변: 같은 객체 내부가 바뀜
nums = [1, 2, 3]
print(id(nums))     # 140555...
nums.append(4)
print(id(nums))     # 140555... (주소 동일)
```

### 왜 중요한가

파이썬의 `=`는 값을 복사하는 게 아니라 **이름을 붙이는 동작**이다. 가변 객체에 이름을 여러 개 붙이면 한쪽 수정이 전부에 반영된다.

```python
a = [1, 2, 3]
b = a          # 복사 아님, 같은 객체에 이름 하나 더
b[0] = 100
print(a)       # [100, 2, 3]
```

---

## 2. 얕은 복사 vs 깊은 복사

### 차이

- **얕은 복사**: 바깥 껍데기만 새로 만들고, 안쪽 객체는 원본과 공유
- **깊은 복사**: 안쪽 객체까지 재귀적으로 전부 새로 생성

```python
import copy

original = [[1, 2], [3, 4]]
shallow = original[:]              # 또는 original.copy(), copy.copy(original)
deep = copy.deepcopy(original)

# 바깥 리스트 수정 → 얕은 복사도 독립
shallow.append([5, 6])
print(original)     # [[1, 2], [3, 4]]  (안 바뀜)

# 안쪽 리스트 수정 → 얕은 복사는 원본도 변경
shallow[0].append(999)
print(original)     # [[1, 2, 999], [3, 4]]  (바뀜)

# 깊은 복사는 어느 쪽이든 독립
deep[0].append(777)
print(original)     # [[1, 2, 999], [3, 4]]  (안 바뀜)
```

### 메모리 구조

```
[얕은 복사]
original ──▶ [ ref1, ref2 ]
                │      │
                ▼      ▼
             [1,2]  [3,4]        ← 이 객체들은 하나뿐
                ▲      ▲
                │      │
shallow  ──▶ [ ref1, ref2 ]      ← 껍데기만 새로 만듦

[깊은 복사]
original ──▶ [ ref1, ref2 ] ──▶ [1,2] [3,4]
deep     ──▶ [ ref3, ref4 ] ──▶ [1,2] [3,4]   ← 전부 별개 객체
```

### 언제 무엇을 쓰나

| 안쪽 원소 | 필요한 복사 | 이유 |
|---|---|---|
| 불변 (int, str, tuple) | 얕은 복사 | 공유해도 수정 불가라 안전 |
| 가변 (list, dict, set) | 깊은 복사 | 공유하면 한쪽 수정이 양쪽에 반영 |

> **주의**: 바깥이 아니라 **안쪽 원소**의 가변/불변 여부가 기준이다.

```python
# 안쪽이 int(불변) → 얕은 복사로 충분
nums = [1, 2, 3]
b = nums[:]
b[0] = 100
print(nums)         # [1, 2, 3]  안전
```

int는 불변이라 "바꿀" 수가 없고, 껍데기의 0번 칸이 다른 객체를 가리키게 될 뿐이다. 껍데기는 얕은 복사로도 독립이므로 원본이 안전하다.

### 문법 주의

```python
b = c.copy()            # 리스트/딕셔너리에 메서드로 존재
b = c.deepcopy()        # AttributeError, 이런 메서드는 없음

import copy
b = copy.deepcopy(c)    # deepcopy는 copy 모듈의 함수
```

---

## 3. 판다스의 view vs copy

### 핵심

판다스도 **껍데기(DataFrame 객체)와 알맹이(numpy 데이터 버퍼)**로 나뉜다. 파이썬 리스트와 구조가 같다.

| | 파이썬 리스트 | 판다스 |
|---|---|---|
| 껍데기 | `[ref, ref, ref]` | DataFrame 객체 (컬럼명, 인덱스) |
| 알맹이 | 안에 든 실제 객체들 | 실제 값이 담긴 numpy 배열 |

### 3단 대응 관계

| 파이썬 | 판다스 | 원본이 바뀌나 |
|---|---|---|
| `b = c` (복사 없음) | `subset = df["foo"]` (view) | 바뀜 |
| `b = c.copy()` (얕은 복사) | `df.copy(deep=False)` | 바뀔 수 있음 |
| `copy.deepcopy(c)` (깊은 복사) | `df.copy()` | 안 바뀜 |

> **view는 얕은 복사가 아니다.** 얕은 복사는 껍데기라도 새로 만들지만, view는 복사 자체를 하지 않고 기존 메모리를 가리키기만 한다. 파이썬의 `b = c`에 해당한다.

### 문제 상황 (pandas 2.x 이하)

```python
df = pd.DataFrame({"foo": [1, 2, 3], "bar": [4, 5, 6]})
subset = df["foo"]       # view, 메모리 공유
subset.iloc[0] = 100
print(df)                # foo가 [100, 2, 3]으로 변경됨
```

`df["foo"]`가 새 데이터를 만드는 게 아니라 `df` 내부 메모리의 해당 구간을 그대로 가리키기 때문이다.

### 판다스가 더 어려운 이유

파이썬에서는 공유 여부가 코드에 드러난다. `b = c`면 무조건 공유다.
판다스는 **인덱싱 문법에 따라 결과가 갈리는데 눈에 보이지 않는다.**

```python
df["foo"]        # view (공유)
df[["foo"]]      # copy (독립)
df.loc[0:1]      # 상황에 따라 다름
```

판다스는 같은 dtype 컬럼들을 하나의 numpy 블록으로 묶어 관리한다. 어떤 선택이 그 블록의 슬라이스(공유)가 되고 어떤 선택이 새 배열(독립)이 되는지가 데이터 구성에 따라 달라진다. 이 예측 불가능성 때문에 나온 경고가 `SettingWithCopyWarning`이다.

```python
df[df["bar"] > 4]["foo"] = 0    # 체이닝 인덱싱, 경고 발생 + 원본 반영 안 됨
```

### pandas 3.0의 Copy-on-Write (CoW)

pandas 3.0부터 CoW가 기본이자 유일한 모드가 되었다.

- 모든 DataFrame/Series가 **겉보기에는 독립된 것처럼** 동작한다
- 실제 복사는 **쓰기 직전까지 미뤄진다** (읽기만 할 때는 메모리 공유 → 성능/메모리 이득)
- 체이닝 대입은 더 이상 동작하지 않는다
- 모호함이 사라졌으므로 `SettingWithCopyWarning`도 제거되었다
- 경고를 없애려고 넣던 방어적 `.copy()`가 불필요해졌다

```python
# pandas 3.0
df = pd.DataFrame({"foo": [1, 2, 3], "bar": [4, 5, 6]})
subset = df["foo"]
subset.iloc[0] = 100
print(df)            # df는 그대로, subset만 변경
```

파이썬 문자열이 불변이라 안전하게 공유되는 것과 같은 전략이다.

```python
s = "hello"
t = s          # 공유 (불변이라 안전)
t += "!"       # 바꾸는 순간 새 객체 생성
print(s)       # "hello"
```

### pandas 2.x에서 CoW 켜기

```python
pd.options.mode.copy_on_write = True    # pandas 2.0부터 지원
```

---

## 4. 실무 규칙

```python
# 원본을 바꾸고 싶다 → .loc으로 한 번에
df.loc[df["bar"] > 4, "foo"] = 0

# 독립된 사본이 필요하다 → 명시적으로 copy()
work = df[df["bar"] > 4].copy()
work["foo"] = 0         # df에 영향 없음
```

- 체이닝 인덱싱(`df[...][...] = ...`)은 어느 버전에서도 쓰지 않는다
- 독립이 필요하면 `.copy()`를 습관적으로 명시한다
- 확신이 안 서면 `id()`로 직접 확인한다

```python
print(id(original[0]) == id(shallow[0]))   # True  같은 객체
print(id(original[0]) == id(deep[0]))      # False 다른 객체
```

### 판다스 deep copy의 한계

`df.copy()`는 데이터 버퍼는 복제하지만, 셀 안에 담긴 파이썬 객체까지 재귀적으로 복제하지는 않는다.

```python
df = pd.DataFrame({"a": [[1, 2], [3, 4]]})
df2 = df.copy()
df2.loc[0, "a"].append(999)
print(df.loc[0, "a"])     # [1, 2, 999]  셀 안의 리스트는 여전히 공유
```

---

## 5. 한 문장 요약

**판다스의 view/copy는 파이썬 가변 객체 공유 문제가 한 겹 아래(numpy 버퍼)에서 반복되는 것이다.** 차이는 파이썬에서는 공유 여부가 코드에 드러나지만 판다스에서는 인덱싱 문법에 숨어 있다는 점이고, pandas 3.0의 CoW는 이를 "기본은 독립, 복사는 쓰기 시점에"로 통일해 해결했다.

---

## 부록: 자주 나오는 함정

### 기본 인자값

기본값은 함수 정의 시점에 한 번만 생성된다. 가변 객체를 기본값으로 쓰면 호출마다 누적된다.

```python
def bad(item, bucket=[]):       # 위험
    bucket.append(item)
    return bucket

print(bad(1))   # [1]
print(bad(2))   # [1, 2]  의도와 다름

def good(item, bucket=None):    # 올바른 패턴
    if bucket is None:
        bucket = []
    bucket.append(item)
    return bucket
```

### 함수 인자 전달

파이썬은 객체 참조를 전달한다.

```python
def add_item(lst):
    lst.append(99)      # 원본 변경됨

def rebind(lst):
    lst = [0, 0]        # 지역 변수만 새 객체를 가리킴, 원본 그대로
```

### 해시 가능 여부

딕셔너리 키와 set 원소는 불변 객체만 가능하다.

```python
d = {(1, 2): "ok"}      # 튜플 가능
d = {[1, 2]: "error"}   # TypeError: unhashable type: 'list'
```

튜플 안에 리스트가 있으면 그 튜플은 해시 불가다. 튜플의 불변성은 "안에 든 참조를 바꿀 수 없다"는 뜻이지 참조된 객체까지 얼어붙는다는 뜻이 아니다.

```python
t = (1, [2, 3])
t[1].append(4)
print(t)            # (1, [2, 3, 4])  가능
t[1] = [9]          # TypeError       불가능
```
