# 学生选课管理系统 ER 图说明 V2（易读版）

## 1. 先说明：这份图怎么看

这份文档不是数据库建表 SQL，而是“概念 ER 图”的读图说明。  
重点是让你能一眼看出：

- 有哪些实体
- 哪些是强实体，哪些是弱实体
- 哪些关系是 1:N、1:1、M:N
- 哪些地方用 `ISA`
- 哪些实体是必须参与关系的，哪些是可选参与的

## 2. 本图采用的符号约定

为了方便在 Markdown 文档中表达，统一使用下面的简化符号：

| 记号 | 含义 |
| --- | --- |
| `[实体]` | 强实体 |
| `[[弱实体]]` | 弱实体 |
| `(属性)` | 普通属性 |
| `((主键))` | 主键属性 |
| `{多值}` | 多值属性，当前模型中基本不用 |
| `<派生>` | 派生属性，当前模型中可选 |
| `A -- B` | 单线参与，表示可选参与 |
| `A == B` | 双线参与，表示全参与/必须参与 |
| `A --> B` | 箭头指向 “1” 的一端 |
| `A --< B` | 一对多，`A` 是 1 端，`B` 是 N 端 |
| `A >--< B` | 多对多 |
| `ISA` | 泛化/特化 |

## 3. 先看最关键的结构

### 3.1 账号角色结构

```text
               [user]
                  |
                 ISA
         _________|_________
        /         |         \
 [student]   [teacher]   [admin]
```

解释：

- `user` 是账号层的父实体。
- `student`、`teacher`、`admin` 是三种角色资料实体。
- 在概念模型里，可以用 `ISA` 表达“账号角色分化”。
- 在逻辑表设计里，仍然保留 `t_user` 与三张角色表分离。

### 3.2 教学组织主链路

```text
[department] --< [major] --< [student]

[department] --< [teacher]

[department] --< [course] --< [course_section]
                                 ^
                                 |
                         [classroom] --<
```

解释：

- 一个院系有多个专业。
- 一个专业有多个学生。
- 一个院系有多个教师。
- 一个院系有多门课程。
- 一门课程可以开出多个教学班。
- 一个教室可以承载多个教学班。

### 3.3 选课与成绩主链路

```text
[student] --< [enrollment] >-- [course_section]

[enrollment] == [[score]]
```

解释：

- `student` 与 `course_section` 的多对多关系，通过 `enrollment` 拆开。
- `score` 依附于 `enrollment` 存在，因此在概念 ER 中把它视作弱实体更容易讲清楚。
- `score` 不能脱离 `enrollment` 独立存在。

## 4. 完整 ER 图（文本版）

```text
                               [department]
                               /    |     \
                              /     |      \
                             /      |       \
                            v       v        v
                        [major]  [teacher] [course]
                           |                    |
                           v                    v
                       [student]         [course_section] <---- [classroom]
                           |                    ^
                           |                    |
                           +---- [enrollment] --+
                                      |
                                      ||
                                      vv
                                   [[score]]


                               [user]
                                  |
                                 ISA
                     _____________|_____________
                    /             |             \
                   v              v              v
              [student]      [teacher]       [admin]
```

## 5. 每个实体的属性归属

### 5.1 `[user]`

```text
[user]
|- ((id))
|- (username)
|- (password)
|- (role)
|- (status)
|- (created_at)
```

### 5.2 `[student]`

```text
[student]
|- ((student_id))
|- (user_id)
|- (major_id)
|- (student_no)
|- (student_name)
|- (gender)
|- (phone)
|- (email)
|- (enrollment_year)
|- (status)
```

### 5.3 `[teacher]`

```text
[teacher]
|- ((teacher_id))
|- (user_id)
|- (department_id)
|- (teacher_no)
|- (teacher_name)
|- (title)
|- (phone)
|- (email)
|- (status)
```

### 5.4 `[admin]`

```text
[admin]
|- ((admin_id))
|- (user_id)
|- (admin_no)
|- (admin_name)
|- (phone)
|- (email)
|- (status)
```

### 5.5 `[department]`

```text
[department]
|- ((department_id))
|- (department_code)
|- (department_name)
|- (office_phone)
|- (status)
```

### 5.6 `[major]`

```text
[major]
|- ((major_id))
|- (department_id)
|- (major_code)
|- (major_name)
|- (status)
```

### 5.7 `[course]`

```text
[course]
|- ((course_id))
|- (department_id)
|- (course_code)
|- (course_name)
|- (credit)
|- (total_hours)
|- (course_type)
|- (description)
|- (status)
```

### 5.8 `[classroom]`

```text
[classroom]
|- ((classroom_id))
|- (building)
|- (room_no)
|- (capacity)
|- (status)
|- (remark)
```

### 5.9 `[course_section]`

```text
[course_section]
|- ((section_id))
|- (course_id)
|- (teacher_id)
|- (classroom_id)
|- (section_code)
|- (semester)
|- (schedule_text)
|- (capacity_limit)
|- (selected_count)
|- (status)
```

### 5.10 `[enrollment]`

```text
[enrollment]
|- ((enrollment_id))
|- (student_id)
|- (section_id)
|- (select_time)
|- (status)
|- (source)
|- (remark)
```

### 5.11 `[[score]]`

```text
[[score]]
|- ((score_id))
|- (enrollment_id)
|- (usual_score)
|- (exam_score)
|- (final_score)
|- (gpa_point)
|- (is_passed)
|- (graded_at)
|- (graded_by)
```

说明：

- 在逻辑表里 `score` 可以有自己的主键。
- 但在概念层，为了强调“成绩必须依附选课记录”，这里仍把它作为弱实体来讲解。

## 6. 关系逐条说明

### 6.1 `[department] --< [major]`

- 一个院系包含多个专业。
- 一个专业必须属于一个院系。
- `major` 对该关系是全参与。

### 6.2 `[major] --< [student]`

- 一个专业下有多个学生。
- 一个学生必须属于一个专业。
- `student` 对该关系是全参与。

### 6.3 `[department] --< [teacher]`

- 一个院系下有多个教师。
- 一个教师必须属于一个院系。

### 6.4 `[department] --< [course]`

- 一个院系可开设多门课程。
- 一门课程必须归属一个院系。

### 6.5 `[course] --< [course_section]`

- 一门课程可在不同学期开出多个教学班。
- 一个教学班必须对应一门课程。

### 6.6 `[teacher] --< [course_section]`

- 一个教师可教授多个教学班。
- 一个教学班必须有一个授课教师。

### 6.7 `[classroom] --< [course_section]`

- 一个教室可承载多个教学班。
- 一个教学班应安排在一个教室中。

### 6.8 `[student] --< [enrollment] >-- [course_section]`

- 本质上表示 `student` 与 `course_section` 的多对多关系。
- 为了数据库实现，将其拆为中间实体 `enrollment`。
- 一个学生可有多条选课记录。
- 一个教学班也可对应多条选课记录。

### 6.9 `[enrollment] == [[score]]`

- 一条选课记录最多对应一条成绩记录。
- 成绩必须依附于选课记录。
- `score` 对该关系是存在依赖。

### 6.10 `[user] ISA [student|teacher|admin]`

- 账号实体向三类角色资料特化。
- 概念图中使用 `ISA`，逻辑实现中使用分表加外键绑定。

## 7. 哪些是强实体，哪些是弱实体

### 强实体

- `user`
- `student`
- `teacher`
- `admin`
- `department`
- `major`
- `course`
- `classroom`
- `course_section`
- `enrollment`

### 弱实体

- `score`

原因：

- `score` 的存在依赖于 `enrollment`。
- 如果没有选课记录，就不应该存在成绩记录。

## 8. 如果你要手绘正式 ER 图，推荐这样画

1. 先画 11 个实体框。
2. `score` 用双矩形表示弱实体。
3. `user` 上方画父实体，下面分出 `student`、`teacher`、`admin`，中间写 `ISA`。
4. `student` 和 `course_section` 不要直接连 M:N，必须通过 `enrollment`。
5. `enrollment` 和 `score` 的关系用双线强调依赖。
6. 每个实体旁边只保留 4 到 6 个最关键属性，避免图过于拥挤。

## 9. 一句话答辩口径

这张 ER 图把系统分成“账号角色层、组织结构层、教学安排层、选课成绩层”四个部分，并通过 `course_section`、`enrollment`、`score` 三个核心实体，清楚地区分了课程定义、具体开课、选课事实和成绩结果。
