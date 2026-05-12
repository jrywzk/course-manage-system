# 组员C → 组员D 联调信息 — Day 1

## 一、当前可调用接口列表

| 序号 | 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|------|
| 1 | POST | /api/auth/login | 用户登录（统一入口） | 可调用 |
| 2 | GET | /api/auth/info | 获取当前用户信息 | 可调用 |
| 3 | GET | /api/sections | 教学班列表（学生浏览可选课） | 可调用 |
| 4 | GET | /api/sections/{id} | 教学班详情 | 可调用 |
| 5 | POST | /api/enrollments | 学生选课 | 可调用 |
| 6 | DELETE | /api/enrollments/{id} | 学生退课 | 可调用 |
| 7 | GET | /api/students/{studentId}/enrollments | 学生已选教学班列表（我的课程） | 可调用 |
| 8 | GET | /api/sections/{sectionId}/students | 教学班学生名单（教师端） | 可调用 |
| 9 | GET | /api/teachers/{teacherId}/sections | 教师授课教学班列表 | 可调用 |
| 10 | POST | /api/scores | 录入/修改成绩 | 可调用 |
| 11 | GET | /api/sections/{sectionId}/scores | 教学班成绩汇总 | 可调用 |

## 二、各接口对应的业务流程与关键字段

### 1. POST /api/auth/login — 登录
- **对应页面**：登录页
- **请求体**：
  ```json
  { "username": "stu001", "password": "123456" }
  ```
- **响应体**：
  ```json
  {
    "code": 200,
    "message": "登录成功",
    "data": {
      "token": "32位UUID字符串",
      "role": "student",
      "userId": 7,
      "username": "stu001",
      "realName": "赵小明"
    }
  }
  ```
- **关键字段**：token 用于后续请求 Header `Authorization: Bearer {token}`，role 决定页面路由

### 2. GET /api/auth/info — 获取用户信息
- **对应页面**：登录态校验、页面初始化
- **请求头**：`Authorization: Bearer {token}`
- **响应关键字段**：userId, username, role, realName, status

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
- **注**：remaining = capacityLimit - selectedCount，前端可用于判断"可选/已满"标识

### 4. GET /api/sections/{id} — 教学班详情
- **对应页面**：学生点击教学班进入详情页
- **路径参数**：sectionId
- **响应额外字段**：totalHours, description, title(教师职称)

### 5. POST /api/enrollments — 选课
- **对应页面**：学生点击"选课"按钮
- **请求体**：
  ```json
  { "studentId": 1, "sectionId": 1, "source": "自主选课" }
  ```
- **响应体**：
  ```json
  {
    "code": 200,
    "data": { "enrollmentId": 22, "result": 1 }
  }
  ```
- **result 含义**：1=选课成功, 0=容量已满, 2=已选过该课程, -1=系统错误
- **错误码**：code=200+result!=1 时在 message 中给出提示；code!=200 时是系统级错误

### 6. DELETE /api/enrollments/{id} — 退课
- **对应页面**：学生点击"退课"按钮
- **路径参数**：enrollmentId（来自已选课程列表的 enrollmentId）
- **响应**：`{ "code": 200, "data": { "result": 1 } }` — 1=成功, 0=记录不存在或已退课

### 7. GET /api/students/{studentId}/enrollments — 我的课程
- **对应页面**：学生端"我的课程"页
- **请求参数**：`?semester=2025-2026-1&status=1`
- **响应关键字段**：
  ```
  enrollmentId, sectionId, sectionCode, semester,
  courseCode, courseName, credit,
  teacherName, scheduleText, building, roomNo,
  selectTime, status,
  hasScore, finalScore, gpaPoint, isPassed
  ```
- **注**：status=1 返回在读，不传返回全部；hasScore 为 true 时 finalScore/gpaPoint/isPassed 才有值

### 8. GET /api/sections/{sectionId}/students — 教学班学生名单
- **对应页面**：教师点击某教学班查看学生名单
- **响应关键字段**：
  ```
  enrollmentId, studentId, studentNo, studentName,
  gender, majorName, selectTime,
  hasScore, finalScore, isPassed
  ```
- **注**：enrollmentId 是后续录入成绩时必须使用的参数

### 9. GET /api/teachers/{teacherId}/sections — 教师授课列表
- **对应页面**：教师登录首页
- **响应关键字段**：sectionId, sectionCode, semester, courseName, credit, scheduleText, capacityLimit, selectedCount, enrolledCount

### 10. POST /api/scores — 录入/修改成绩
- **对应页面**：教师点击某学生录入或修改成绩
- **请求体**：
  ```json
  { "enrollmentId": 1, "usualScore": 85, "examScore": 90 }
  ```
- **响应体**：
  ```json
  {
    "code": 200,
    "data": {
      "enrollmentId": 1,
      "usualScore": 85, "examScore": 90,
      "finalScore": 88.5,
      "gpaPoint": 3.7,
      "isPassed": 1
    }
  }
  ```
- **注**：finalScore = usual*0.3 + exam*0.7（自动计算）；已有成绩时自动覆盖

### 11. GET /api/sections/{sectionId}/scores — 教学班成绩汇总
- **对应页面**：教师查看教学班成绩总览
- **响应**：
  ```json
  {
    "code": 200,
    "data": {
      "totalStudents": 10,
      "gradedCount": 7,
      "ungradedCount": 3,
      "students": [{ "enrollmentId": 1, "studentNo": "S2025001", "studentName": "赵小明", "finalScore": 88.5, ... }]
    }
  }
  ```

## 三、全局约定

- **Base URL**: `http://localhost:9090/api`
- **认证方式**：登录后获取 token，后续请求 Header 携带 `Authorization: Bearer {token}`
- **统一响应格式**：`{ "code": 200, "message": "...", "data": {...} }`
- **分页格式**：请求 `page=1&pageSize=10`，响应 `{ "total": N, "page": 1, "pageSize": 10, "list": [...] }`
- **API文档地址**：启动后端后访问 `http://localhost:9090/api/doc.html`（Knife4j/Swagger）

## 四、测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 学生 | stu001 ~ stu008 | 123456 |
| 教师 | tch001 ~ tch004 | 123456 |
| 管理员 | admin01, admin02 | 123456 |
