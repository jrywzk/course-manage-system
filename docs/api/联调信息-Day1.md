# 组员C → 组员D 联调信息 — Day 2 教师主链（2026-05-13）

## 一、当前可调用接口列表（正式口径，9090 doc.html 仅有这 11 个）

| 序号 | 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|------|
| 1 | POST | /api/auth/login | 用户登录（统一入口） | 已验证 |
| 2 | GET | /api/auth/info | 获取当前用户信息 | 已验证 |
| 3 | GET | /api/sections | 教学班列表（学生浏览可选课） | 已验证 |
| 4 | GET | /api/sections/{id} | 教学班详情 | 已实现 |
| 5 | POST | /api/enrollments | 学生选课 | 已验证 |
| 6 | DELETE | /api/enrollments/{id} | 学生退课 | 已验证 |
| 7 | GET | /api/students/{studentId}/enrollments | 学生已选教学班列表（我的课程） | 已验证 |
| 8 | GET | /api/sections/{sectionId}/students | 教学班学生名单（教师端） | 已验证 |
| 9 | GET | /api/teachers/{teacherId}/sections | 教师授课教学班列表 | 已验证 |
| 10 | POST | /api/scores | 录入/修改成绩（自动判断新增/覆盖） | 已验证 |
| 11 | GET | /api/sections/{sectionId}/scores | 教学班成绩汇总 | 已验证 |

## 二、各接口对应的业务流程与关键字段

### 1. POST /api/auth/login — 登录
- **请求体**：
  ```json
  { "username": "stu001", "password": "123456" }   // 学生
  { "username": "tch001", "password": "123456" }   // 教师
  ```
- **响应体（学生登录）**：
  ```json
  {
    "status": 200, "msg": "登录成功",
    "data": { "token": "...", "role": "student", "userId": 7, "username": "stu001", "realName": "赵小明", "entityId": 1, "studentId": 1 }
  }
  ```
- **响应体（教师登录）**：
  ```json
  {
    "status": 200, "msg": "登录成功",
    "data": { "token": "...", "role": "teacher", "userId": 3, "username": "tch001", "realName": "王教授", "entityId": 1, "teacherId": 1 }
  }
  ```
- **关键字段**：
  - `token` → Header `Authorization: Bearer {token}`
  - `role` → 决定页面路由（student/teacher/admin）
  - `studentId` / `teacherId` / `adminId` → 角色专属字段，直接用于后续接口拼接路径
  - `entityId` → 始终等于角色专属 ID（已修复）

### 2. GET /api/auth/info — 获取用户信息
- **请求头**：`Authorization: Bearer {token}`
- **响应关键字段**：userId, username, role, realName, entityId, status

### 3. GET /api/sections — 教学班列表
- **对应页面**：学生端首页（选课列表页）
- **请求参数**：`?page=1&pageSize=10&status=1`
- **响应关键字段**：
  ```
  sectionId, sectionCode, semester,
  courseName, credit, courseType,
  teacherName, building, roomNo, scheduleText,
  capacityLimit, selectedCount, remaining, status
  ```
- **注**：remaining = capacityLimit - selectedCount

### 4. GET /api/sections/{id} — 教学班详情
- **路径参数**：sectionId
- **响应额外字段**：totalHours, description, title(教师职称)

### 5. POST /api/enrollments — 选课
- **请求体**：
  ```json
  { "studentId": 1, "sectionId": 2, "source": "自主选课" }
  ```
- **成功响应**（status=200）：
  ```json
  {
    "status": 200,
    "msg": "选课成功",
    "data": { "enrollmentId": 23, "result": 1 }
  }
  ```
- **失败响应**（status=400，data 中仍有 result 可判断）：
  ```json
  { "status": 400, "msg": "已选过该课程", "data": { "result": 2, "enrollmentId": 1 } }
  { "status": 400, "msg": "容量已满",        "data": { "result": 0 } }
  { "status": 400, "msg": "系统错误",        "data": { "result": -1 } }
  ```
- **result 含义**：1=选课/退课成功, 0=容量满/记录不存在, 2=已选过, -1=系统错误
- **重要**：无论成功还是失败，都可通过 `data.result` 判断业务状态

### 6. DELETE /api/enrollments/{id} — 退课
- **路径参数**：enrollmentId（来自已选课程列表）
- **响应**：
  ```json
  { "status": 200, "msg": "退课成功", "data": { "result": 1 } }
  { "status": 400, "msg": "记录不存在或已退课", "data": { "result": 0 } }
  ```

### 7. GET /api/students/{studentId}/enrollments — 我的课程
- **请求参数**：`?semester=2025-2026-1&status=1`
- **响应关键字段**：
  ```
  enrollmentId, sectionId, sectionCode, semester,
  courseCode, courseName, credit,
  teacherName, scheduleText, building, roomNo,
  selectTime, status,
  hasScore(boolean), usualScore, examScore, finalScore, gpaPoint, isPassed
  ```

### 8. GET /api/sections/{sectionId}/students — 教学班学生名单
- **响应关键字段**：
  ```
  enrollmentId, studentId, studentNo, studentName,
  gender, majorName, selectTime,
  hasScore, usualScore, examScore, finalScore, isPassed
  ```

### 9. GET /api/teachers/{teacherId}/sections — 教师授课列表
- **响应关键字段**：sectionId, sectionCode, semester, courseName, credit, scheduleText, building, roomNo, capacityLimit, selectedCount, enrolledCount

### 10. POST /api/scores — 录入/修改成绩
- **请求体**（⚠️ teacherId 必填，系统会校验 enrollment 是否属于该教师）：
  ```json
  { "enrollmentId": 18, "teacherId": 1, "usualScore": 85, "examScore": 90 }
  ```
- **首次录入响应**（status=200, msg="成绩录入成功"）：
  ```json
  { "status": 200, "msg": "成绩录入成功", "data": { "scoreId": 1, "enrollmentId": 18, "usualScore": 85, "examScore": 90, "finalScore": 88.5, "gpaPoint": 3.7, "isPassed": 1, "gradedAt": "2026-05-13 10:00:00" } }
  ```
- **修改成绩响应**（status=200, msg="成绩修改成功"）：
  ```json
  { "status": 200, "msg": "成绩修改成功", "data": { "scoreId": 1, "enrollmentId": 18, "usualScore": 95, "examScore": 95, "finalScore": 95.0, "gpaPoint": 4.0, "isPassed": 1, "gradedAt": "2026-05-13 10:05:00" } }
  ```
- **越权响应**（status=403, teacherId 不对）：
  ```json
  { "status": 403, "msg": "该选课记录不属于您的教学班", "data": null }
  ```
- **注**：系统自动判断首次录入 or 覆盖修改（同一 enrollmentId 提交两次即为修改）；finalScore = usualScore×0.3 + examScore×0.7

### 11. GET /api/sections/{sectionId}/scores — 教学班成绩汇总
- **请求参数**：`?teacherId=1`（可选，传入则校验是否属于该教师的教学班）
- **正常响应**：
  ```json
  { "status": 200, "data": { "sectionCode": "CS-101-01", "courseName": "程序设计基础", "totalStudents": 3, "gradedCount": 3, "ungradedCount": 0, "students": [...] } }
  ```
- **越权响应**：`{ "status": 403, "msg": "该教学班不属于您" }`

## 三、已废弃的旧控制器（9090 doc.html 已完全移除，所有路径返回 404）

| 旧控制器 | 旧路径前缀 | 替代方案 |
|----------|-----------|---------|
| LoginController | /api/login | → /api/auth/login |
| UserController | /api/user/* | → /api/auth/login + 新架构 |
| CourseController | /api/course/* | → /api/sections/* |
| StudentController | /api/student/* | → 新架构（学生数据通过 enrollment 查询） |
| TeacherController | /api/teacher/* | → /api/teachers/{teacherId}/sections |
| AdminController | /api/admin/* | → 新架构（待后续开发） |
| ScoreController | /api/score/* | → POST /api/scores + GET /api/sections/{id}/scores |
| TestController | /api/test | → 已移除 |

## 四、全局约定

- **Base URL**: `http://localhost:9090/api`
- **认证方式**：登录后获取 token，后续请求 Header 携带 `Authorization: Bearer {token}`
- **统一响应格式**：`{ "status": 200, "msg": "...", "data": {...} }`
  - ⚠️ 字段名是 `status` 不是 `code`，是 `msg` 不是 `message`
- **分页格式**：请求 `page=1&pageSize=10`，响应 `{ "total": N, "page": 1, "pageSize": 10, "list": [...] }`
- **API文档地址**：启动后端后访问 `http://localhost:9090/api/doc.html`

## 五、测试账号

| 角色 | 用户名 | 密码 | 对应 ID |
|------|--------|------|---------|
| 学生 | stu001 ~ stu008 | 123456 | studentId=1~8 |
| 教师 | tch001 ~ tch004 | 123456 | teacherId=1~4 |
| 管理员 | admin01, admin02 | 123456 | adminId=1~2 |

## 六、学生端完整调用流程

```
1. POST /api/auth/login {"username":"stu001","password":"123456"}
   → 获取 token + studentId

2. GET /api/sections
   → 浏览可选教学班列表

3. POST /api/enrollments {"studentId":1,"sectionId":2,"source":"自主选课"}
   → 选课，获取 enrollmentId

4. GET /api/students/1/enrollments
   → 查看已选课程（含 enrollmentId）

5. DELETE /api/enrollments/{enrollmentId}
   → 退课
```

## 七、教师端完整调用流程

```
1. POST /api/auth/login {"username":"tch001","password":"123456"}
   → 获取 token + teacherId（如 teacherId=1）

2. GET /api/teachers/1/sections
   → 查看自己的教学班列表，获取 sectionId

3. GET /api/sections/{sectionId}/students
   → 查看某教学班的学生名单，获取 enrollmentId

4. POST /api/scores {"enrollmentId":18,"teacherId":1,"usualScore":85,"examScore":90}
   → 录入成绩（同一 enrollmentId 再次提交即为修改）

5. GET /api/sections/{sectionId}/scores?teacherId=1
   → 查看教学班成绩汇总（teacherId 校验通过才返回）
```
