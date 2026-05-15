# 组员C → 组员D 联调信息 — Day 2 教师主链安全加固（2026-05-14）

## 一、今日变更总览

| # | 变更项 | Day1 | Day2 |
|---|--------|------|------|
| 1 | 教师接口身份校验 | ❌ 无 | ✅ `Authorization: Bearer <token>` 必填 |
| 2 | 名单查询 teacherId | 可选 | **必填**，不传直接报错 |
| 3 | 成绩汇总 teacherId | 可选 | **必填** + Token 双校验 |
| 4 | Token→teacherId 绑定 | ❌ 信任前端传参 | ✅ `entityId` 必须等于 `teacherId` |
| 5 | Mapper 列名 | V1/V2 混用 | 全部统一 V2 列名 |

---

## 二、当前可调用接口列表（正式口径）

| 序号 | 方法 | 路径 | 需要 Token | teacherId | 说明 |
|------|------|------|-----------|-----------|------|
| 1 | POST | `/api/auth/login` | 否 | — | 登录 |
| 2 | GET | `/api/auth/info` | 是 | — | 用户信息 |
| 3 | GET | `/api/sections` | 否 | — | 教学班列表 |
| 4 | GET | `/api/sections/{id}` | 否 | — | 教学班详情 |
| 5 | POST | `/api/enrollments` | 否 | — | 学生选课 |
| 6 | DELETE | `/api/enrollments/{id}` | 否 | — | 学生退课 |
| 7 | GET | `/api/students/{studentId}/enrollments` | 否 | — | 我的课程 |
| **8** | **GET** | **`/api/sections/{sectionId}/students`** | **是** | **必填 Query** | 教学班学生名单 |
| **9** | **GET** | **`/api/teachers/{teacherId}/sections`** | **是** | 路径参数 | 教师授课列表 |
| **10** | **POST** | **`/api/scores`** | **是** | **必填 Body** | 录入/修改成绩 |
| **11** | **GET** | **`/api/sections/{sectionId}/scores`** | **是** | **必填 Query** | 成绩汇总 |

> 接口 1-7 为学生侧，8-11 为教师侧（**加粗**表示 Day2 加了 Token 强校验）。

---

## 三、教师接口详细说明（Day2 更新版）

### 3.1 登录 — 不变

```
POST /api/auth/login
Body: {"username":"tch001","password":"123456"}
```

**响应关键字段不变：**
```json
{
  "status": 200, "msg": "登录成功",
  "data": {
    "token": "abc...",
    "role": "teacher",
    "userId": 3,
    "username": "tch001",
    "realName": "王教授",
    "entityId": 1,
    "teacherId": 1
  }
}
```

前端保存 `token` 和 `teacherId`，后续 **4 个教师接口全部需要**。

---

### 3.2 GET /api/teachers/{teacherId}/sections — 教师教学班列表 ⚠️ Day2 更新

| 项目 | Day1 | Day2 |
|------|------|------|
| Header | 无要求 | **`Authorization: Bearer <token>` 必填** |
| teacherId | 路径参数 | 路径参数 + **Token entityId 必须等于 teacherId** |

```http
GET /api/teachers/1/sections
Authorization: Bearer abc...
```

- Token 中 `entityId=1` 不等于路径 `teacherId=2` → **401** "未登录或无权查看该教师的教学班"
- 校验通过 → 返回该教师所有 status=1 的教学班

---

### 3.3 GET /api/sections/{sectionId}/students — 学生名单 ⚠️ Day2 更新

| 项目 | Day1 | Day2 |
|------|------|------|
| Header | 无要求 | **`Authorization: Bearer <token>` 必填** |
| teacherId | `required=false` | **必填 Query** |

```http
GET /api/sections/1/students?teacherId=1
Authorization: Bearer abc...
```

**三层防护顺序：**
```
1. Token 校验       → entityId == teacherId?      → 否则 401
2. section 存在校验  → sectionId 有效?              → 否则 404
3. 教学班归属校验    → section.teacher_id == teacherId? → 否则 403
```

**响应字段（2026-05-15 更新，新增 usualScore/examScore）：**
```json
{
  "data": [
    {
      "enrollmentId": 19,
      "studentId": 8,
      "studentNo": "S2025008",
      "studentName": "冯小文",
      "gender": "M",
      "majorName": "计算机科学与技术",
      "selectTime": "2025-09-03 09:00:00",
      "hasScore": true,
      "usualScore": 85.00,
      "examScore": 82.00,
      "finalScore": 82.90,
      "isPassed": 1
    }
  ]
}
```

---

### 3.4 POST /api/scores — 录入/修改成绩 ⚠️ Day2 更新

| 项目 | Day1 | Day2 |
|------|------|------|
| Header | 无要求 | **`Authorization: Bearer <token>` 必填** |
| teacherId(Body) | 必填 | 必填 + **Token entityId 必须等于 teacherId** |

```http
POST /api/scores
Authorization: Bearer abc...
Content-Type: application/json

{
  "enrollmentId": 19,
  "teacherId": 1,
  "usualScore": 80,
  "examScore": 75
}
```

**四层防护顺序：**
```
1. Token 校验          → entityId == teacherId?          → 否则 401
2. 分数范围校验         → 0 <= score <= 100               → 否则 400
3. enrollment 存在校验  → enrollmentId 有效?              → 否则 404
4. 教学班归属校验       → enrollment.section.teacher == teacherId? → 否则 403
```

**首次录入：** `selectByEnrollmentId` → null → `INSERT` → `"成绩录入成功"`
**再次修改：** `selectByEnrollmentId` → existing → `UPDATE` → `"成绩修改成功"`

**错误响应速查：**

| 错误场景 | status | msg |
|----------|--------|-----|
| 不传 Token 或 Token 无效 | 401 | "未登录或无权操作" |
| Token 的 entityId != teacherId | 401 | "未登录或无权操作" |
| 分数 <0 或 >100 | 400 | "成绩超出范围 0-100" |
| enrollmentId 不存在 | 404 | "选课记录不存在" |
| enrollment 不属于该教师 | 403 | "该选课记录不属于您的教学班" |

---

### 3.5 GET /api/sections/{sectionId}/scores — 成绩汇总 ⚠️ Day2 更新

| 项目 | Day1 | Day2 |
|------|------|------|
| Header | 无要求 | **`Authorization: Bearer <token>` 必填** |
| teacherId | "可选" | **必填 Query** + Token 双校验 |

```http
GET /api/sections/1/scores?teacherId=1
Authorization: Bearer abc...
```

**三层防护顺序：**
```
1. Token 校验       → entityId == teacherId?      → 否则 401
2. section 存在校验  → sectionId 有效?              → 否则 404
3. 教学班归属校验    → section.teacher_id == teacherId? → 否则 403
```

**响应字段不变：**
```json
{
  "data": {
    "sectionCode": "CS-101-01",
    "courseName": "程序设计基础",
    "totalStudents": 3,
    "gradedCount": 3,
    "ungradedCount": 0,
    "students": [...]
  }
}
```

---

## 四、越权攻击防护验证（Day2 新增）

以下场景均已通过代码验证，前端无需特殊处理但可用于自测：

| 攻击场景 | 请求示例 | 返回 |
|----------|---------|------|
| 王教授 Token 查刘副教授的教学班 | `GET /teachers/2/sections` + 王教授Token | **401** |
| 王教授 Token 查刘副教授的名单 | `GET /sections/2/students?teacherId=2` + 王教授Token | **401** (Token层拦截) |
| 王教授 Token 查刘副教授的名单(传自己teacherId) | `GET /sections/2/students?teacherId=1` + 王教授Token | **403** (归属层拦截) |
| 王教授 Token 给刘副教授的 enrollment 录分 | `POST /scores {enrollmentId:5, teacherId:2}` + 王教授Token | **401** (Token层拦截) |
| 王教授 Token 给刘副教授的 enrollment 录分(伪造成自己) | `POST /scores {enrollmentId:5, teacherId:1}` + 王教授Token | **403** (归属层拦截) |
| 越界分数 | `usualScore: 120` 或 `-5` | **400** |

---

## 五、教师端完整调用流程（Day2 更新版）

```
1. POST /api/auth/login
   Body: {"username":"tch001","password":"123456"}
   ← 保存：token, teacherId=1

2. GET /api/teachers/1/sections
   Header: Authorization: Bearer <token>
   ← 拿到教学班列表（含 sectionId）

3. GET /api/sections/1/students?teacherId=1
   Header: Authorization: Bearer <token>
   ← 拿到学生名单（含 enrollmentId）

4. POST /api/scores
   Header: Authorization: Bearer <token>
   Body: {"enrollmentId":19,"teacherId":1,"usualScore":80,"examScore":75}
   ← 首次→"成绩录入成功"，再次→"成绩修改成功"

5. GET /api/sections/1/scores?teacherId=1
   Header: Authorization: Bearer <token>
   ← 拿到成绩汇总（totalStudents/gradedCount/ungradedCount/students[]）
```

**每一步都带 `Authorization: Bearer <token>` 和正确的 `teacherId`，两者缺一立即 401。**

---

## 六、全局约定（不变）

- **Base URL**: `http://localhost:9090/api`
- **认证方式**：`Authorization: Bearer <token>`
- **统一响应格式**：`{"status":200,"msg":"...","data":{...}}`
- **API 文档**：`http://localhost:9090/api/doc.html`

## 七、测试账号（不变）

| 角色 | 用户名 | 密码 | 实体 ID |
|------|--------|------|---------|
| 教师 | tch001 | 123456 | teacherId=1 (王教授) |
| 教师 | tch002 | 123456 | teacherId=2 (刘副教授) |
| 教师 | tch003 | 123456 | teacherId=3 (陈讲师) |
| 教师 | tch004 | 123456 | teacherId=4 (赵教授) |
| 学生 | stu001 ~ stu008 | 123456 | studentId=1~8 |
