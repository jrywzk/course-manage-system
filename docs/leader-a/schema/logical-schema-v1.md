# 学生选课管理系统逻辑字段草案 V1

## 1. 说明

本草案基于 `11` 个实体的 ER 模型，用于指导后续关系模式设计、`schema_v2.sql` 编写和答辩材料整理。  
本轮只确定逻辑字段、主键、外键和关键约束，不直接改动现有代码或数据库脚本。

## 2. 实体字段草案

### 2.1 `t_user`

| 字段 | 类型建议 | 说明 |
| --- | --- | --- |
| `id` | bigint | 主键，账号唯一标识 |
| `username` | varchar(50) | 登录名，可选是否与学号/工号分离 |
| `password` | varchar(100) | 密码 |
| `role` | varchar(20) | 角色：student / teacher / admin |
| `status` | tinyint | 账号状态，如启用/禁用 |
| `created_at` | datetime | 创建时间 |

约束建议：

- 主键：`id`
- 唯一约束：`username`

### 2.2 `t_department`

| 字段 | 类型建议 | 说明 |
| --- | --- | --- |
| `department_id` | bigint | 主键 |
| `department_code` | varchar(30) | 院系编号 |
| `department_name` | varchar(100) | 院系名称 |
| `office_phone` | varchar(30) | 联系电话 |
| `status` | tinyint | 状态 |

约束建议：

- 主键：`department_id`
- 唯一约束：`department_code`

### 2.3 `t_major`

| 字段 | 类型建议 | 说明 |
| --- | --- | --- |
| `major_id` | bigint | 主键 |
| `department_id` | bigint | 外键，所属院系 |
| `major_code` | varchar(30) | 专业编号 |
| `major_name` | varchar(100) | 专业名称 |
| `status` | tinyint | 状态 |

约束建议：

- 主键：`major_id`
- 外键：`department_id -> t_department.department_id`
- 唯一约束：`major_code`

### 2.4 `t_student`

| 字段 | 类型建议 | 说明 |
| --- | --- | --- |
| `student_id` | bigint | 主键，可与用户 ID 保持一致或独立 |
| `user_id` | bigint | 外键，对应账号 |
| `major_id` | bigint | 外键，所属专业 |
| `student_no` | varchar(30) | 学号 |
| `student_name` | varchar(50) | 姓名 |
| `gender` | varchar(10) | 性别 |
| `phone` | varchar(30) | 电话 |
| `email` | varchar(100) | 邮箱 |
| `enrollment_year` | int | 入学年份 |
| `status` | tinyint | 状态 |

约束建议：

- 主键：`student_id`
- 外键：`user_id -> t_user.id`
- 外键：`major_id -> t_major.major_id`
- 唯一约束：`student_no`

### 2.5 `t_teacher`

| 字段 | 类型建议 | 说明 |
| --- | --- | --- |
| `teacher_id` | bigint | 主键 |
| `user_id` | bigint | 外键，对应账号 |
| `department_id` | bigint | 外键，所属院系 |
| `teacher_no` | varchar(30) | 工号 |
| `teacher_name` | varchar(50) | 姓名 |
| `title` | varchar(50) | 职称 |
| `phone` | varchar(30) | 电话 |
| `email` | varchar(100) | 邮箱 |
| `status` | tinyint | 状态 |

约束建议：

- 主键：`teacher_id`
- 外键：`user_id -> t_user.id`
- 外键：`department_id -> t_department.department_id`
- 唯一约束：`teacher_no`

### 2.6 `t_admin`

| 字段 | 类型建议 | 说明 |
| --- | --- | --- |
| `admin_id` | bigint | 主键 |
| `user_id` | bigint | 外键，对应账号 |
| `admin_no` | varchar(30) | 管理员编号 |
| `admin_name` | varchar(50) | 姓名 |
| `phone` | varchar(30) | 电话 |
| `email` | varchar(100) | 邮箱 |
| `status` | tinyint | 状态 |

约束建议：

- 主键：`admin_id`
- 外键：`user_id -> t_user.id`
- 唯一约束：`admin_no`

### 2.7 `t_course`

| 字段 | 类型建议 | 说明 |
| --- | --- | --- |
| `course_id` | bigint | 主键 |
| `department_id` | bigint | 外键，所属院系 |
| `course_code` | varchar(30) | 课程编号 |
| `course_name` | varchar(100) | 课程名称 |
| `credit` | decimal(4,1) | 学分 |
| `total_hours` | int | 总学时 |
| `course_type` | varchar(30) | 课程类型 |
| `description` | varchar(255) | 课程说明 |
| `status` | tinyint | 状态 |

约束建议：

- 主键：`course_id`
- 外键：`department_id -> t_department.department_id`
- 唯一约束：`course_code`

### 2.8 `t_classroom`

| 字段 | 类型建议 | 说明 |
| --- | --- | --- |
| `classroom_id` | bigint | 主键 |
| `building` | varchar(50) | 教学楼 |
| `room_no` | varchar(30) | 教室编号 |
| `capacity` | int | 容量 |
| `status` | tinyint | 状态 |
| `remark` | varchar(255) | 备注 |

约束建议：

- 主键：`classroom_id`
- 唯一约束：`building + room_no`

### 2.9 `t_course_section`

| 字段 | 类型建议 | 说明 |
| --- | --- | --- |
| `section_id` | bigint | 主键 |
| `course_id` | bigint | 外键，对应课程定义 |
| `teacher_id` | bigint | 外键，对应授课教师 |
| `classroom_id` | bigint | 外键，对应教室 |
| `section_code` | varchar(30) | 教学班编号 |
| `semester` | varchar(30) | 学期，如 2026-Spring |
| `schedule_text` | varchar(100) | 上课时间描述 |
| `capacity_limit` | int | 选课人数上限 |
| `selected_count` | int | 已选人数，可冗余维护 |
| `status` | tinyint | 状态 |

约束建议：

- 主键：`section_id`
- 外键：`course_id -> t_course.course_id`
- 外键：`teacher_id -> t_teacher.teacher_id`
- 外键：`classroom_id -> t_classroom.classroom_id`
- 唯一约束：`section_code`

### 2.10 `t_enrollment`

| 字段 | 类型建议 | 说明 |
| --- | --- | --- |
| `enrollment_id` | bigint | 主键 |
| `student_id` | bigint | 外键，对应学生 |
| `section_id` | bigint | 外键，对应教学班 |
| `select_time` | datetime | 选课时间 |
| `status` | varchar(20) | 选课状态，如 selected / dropped / completed |
| `source` | varchar(20) | 选课来源，可选 |
| `remark` | varchar(255) | 备注 |

约束建议：

- 主键：`enrollment_id`
- 外键：`student_id -> t_student.student_id`
- 外键：`section_id -> t_course_section.section_id`
- 唯一约束：`student_id + section_id`

### 2.11 `t_score`

| 字段 | 类型建议 | 说明 |
| --- | --- | --- |
| `score_id` | bigint | 主键 |
| `enrollment_id` | bigint | 外键，对应选课记录 |
| `usual_score` | decimal(5,2) | 平时成绩 |
| `exam_score` | decimal(5,2) | 考试成绩 |
| `final_score` | decimal(5,2) | 总评成绩 |
| `gpa_point` | decimal(3,2) | 绩点，可选 |
| `is_passed` | tinyint | 是否通过 |
| `graded_at` | datetime | 录入时间 |
| `graded_by` | bigint | 录入教师 ID，可选 |

约束建议：

- 主键：`score_id`
- 外键：`enrollment_id -> t_enrollment.enrollment_id`
- 唯一约束：`enrollment_id`

## 3. 核心设计决策

### 3.1 `course` 与 `course_section` 的拆分边界

- `course` 只负责静态课程定义。
- `course_section` 负责某学期的具体开课信息。
- 学生不能直接选 `course`，而是选 `course_section`。

### 3.2 `enrollment` 是否记录选课状态与选课时间

结论：记录。

原因：

- 便于表示“已选”“已退”“已修完”等状态。
- 便于支撑退课、补选、统计选课时间等业务。

### 3.3 `score` 是否直接关联 `enrollment_id`

结论：直接关联。

原因：

- 关系最清晰。
- 能自然表达“一条选课记录最多对应一条成绩记录”。
- 不需要重复保存学生、教师、课程等冗余键。

## 4. 最小可讲清的关系模式口径

后续答辩时可统一表述为：

- `t_user` 管账号。
- `t_student`、`t_teacher`、`t_admin` 管角色资料。
- `t_department`、`t_major` 管组织结构。
- `t_course` 管课程定义。
- `t_course_section` 管具体开课班次。
- `t_enrollment` 管学生选班关系。
- `t_score` 管选班后的成绩结果。

## 5. 下一步建议

1. 基于本草案绘制正式 ER 图。
2. 将字段草案进一步收敛为关系模式与外键清单。
3. 再决定是否生成 `schema_v2.sql` 草稿。
