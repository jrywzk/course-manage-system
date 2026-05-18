package com.agiantii.backend.controller;

import com.agiantii.backend.common.R;
import com.agiantii.backend.common.TokenStore;
import com.agiantii.backend.mapper.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin")
@Api(tags = "P0-管理员端管理接口")
public class AdminApiController {

    @Resource
    private TokenStore tokenStore;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private CourseSectionMapper courseSectionMapper;

    @Resource
    private DepartmentMapper departmentMapper;

    @Resource
    private MajorMapper majorMapper;

    @Resource
    private ClassroomMapper classroomMapper;

    @Resource
    private TeacherMapper teacherMapper;

    // ==================== Token 校验 ====================

    private Map<String, Object> requireAdmin(String authHeader) {
        Map<String, Object> info = tokenStore.resolve(authHeader);
        if (info == null) return null;
        if (!"admin".equals(info.get("role"))) return null;
        return info;
    }

    // ==================== 课程管理 ====================

    @GetMapping("/courses")
    @ApiOperation("管理员-查询课程列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<List<Map<String, Object>>> listCourses(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("GET /admin/courses");
        List<Map<String, Object>> list = courseMapper.selectAllForAdmin();
        return R.success(list, "成功");
    }

    @GetMapping("/courses/{id}")
    @ApiOperation("管理员-查询课程详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "课程ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<Map<String, Object>> getCourse(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("GET /admin/courses/{}", id);
        Map<String, Object> course = courseMapper.selectByIdForAdmin(id);
        if (course == null) return R.error("课程不存在", 404);
        return R.success(course, "成功");
    }

    @PostMapping("/courses")
    @ApiOperation("管理员-新增课程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<Map<String, Object>> addCourse(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("POST /admin/courses: {}", body);

        String courseCode = (String) body.get("courseCode");
        String name = (String) body.get("name");
        if (courseCode == null || courseCode.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            return R.error("课程编码和名称不能为空", 400);
        }
        body.putIfAbsent("status", 1);
        courseMapper.insertCourse(body);

        Integer newId = toInt(body.get("id"));
        Map<String, Object> course = courseMapper.selectByIdForAdmin(newId);
        return R.success(course, "课程新增成功");
    }

    @PutMapping("/courses/{id}")
    @ApiOperation("管理员-修改课程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "课程ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<Map<String, Object>> updateCourse(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("PUT /admin/courses/{}: {}", id, body);

        Map<String, Object> existing = courseMapper.selectByIdForAdmin(id);
        if (existing == null) return R.error("课程不存在", 404);

        // 只更新前端传入的字段，未传字段从已有记录回填
        for (Map.Entry<String, Object> entry : existing.entrySet()) {
            body.putIfAbsent(entry.getKey(), entry.getValue());
        }

        body.put("id", id);
        courseMapper.updateCourse(body);

        Map<String, Object> updated = courseMapper.selectByIdForAdmin(id);
        return R.success(updated, "课程修改成功");
    }

    @PutMapping("/courses/{id}/disable")
    @ApiOperation("管理员-停用课程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "课程ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<String> disableCourse(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("PUT /admin/courses/{}/disable", id);

        Map<String, Object> course = courseMapper.selectByIdForAdmin(id);
        if (course == null) return R.error("课程不存在", 404);

        int refSections = courseMapper.countReferencedSections(id);
        if (refSections > 0) {
            return R.error("该课程下存在 " + refSections + " 个运行中的教学班，无法停用", 400);
        }

        courseMapper.disableCourse(id);
        return R.success("课程已停用");
    }

    // ==================== 教学班管理 ====================

    @GetMapping("/sections")
    @ApiOperation("管理员-查询教学班列表（支持按课程/教师/状态筛选）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程ID（可选）", paramType = "query"),
            @ApiImplicitParam(name = "teacherId", value = "教师ID（可选）", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态 1=运行中 0=已关闭（可选）", paramType = "query"),
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<List<Map<String, Object>>> listSections(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) Integer teacherId,
            @RequestParam(required = false) Integer status,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("GET /admin/sections: courseId={}, teacherId={}, status={}", courseId, teacherId, status);
        List<Map<String, Object>> list = courseSectionMapper.selectAllForAdmin(courseId, teacherId, status);
        return R.success(list, "成功");
    }

    @GetMapping("/sections/{id}")
    @ApiOperation("管理员-查询教学班详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "教学班ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<Map<String, Object>> getSection(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("GET /admin/sections/{}", id);
        Map<String, Object> section = courseSectionMapper.selectByIdForAdmin(id);
        if (section == null) return R.error("教学班不存在", 404);
        return R.success(section, "成功");
    }

    @PostMapping("/sections")
    @ApiOperation("管理员-新增教学班")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<Map<String, Object>> addSection(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("POST /admin/sections: {}", body);

        // 基础校验
        String msg = validateSection(body, null);
        if (msg != null) return R.error(msg, 400);

        body.putIfAbsent("status", 1);
        body.putIfAbsent("selectedCount", 0);
        courseSectionMapper.insertSection(body);

        Integer newId = toInt(body.get("sectionId"));
        Map<String, Object> section = courseSectionMapper.selectByIdForAdmin(newId);
        return R.success(section, "教学班新增成功");
    }

    @PutMapping("/sections/{id}")
    @ApiOperation("管理员-修改教学班")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "教学班ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<Map<String, Object>> updateSection(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("PUT /admin/sections/{}: {}", id, body);

        Map<String, Object> existing = courseSectionMapper.selectByIdForAdmin(id);
        if (existing == null) return R.error("教学班不存在", 404);

        String msg = validateSection(body, existing);
        if (msg != null) return R.error(msg, 400);

        // 只更新前端传入的字段，未传字段从已有记录回填
        for (Map.Entry<String, Object> entry : existing.entrySet()) {
            body.putIfAbsent(entry.getKey(), entry.getValue());
        }

        body.put("sectionId", id);
        courseSectionMapper.updateSection(body);

        Map<String, Object> updated = courseSectionMapper.selectByIdForAdmin(id);
        return R.success(updated, "教学班修改成功");
    }

    @PutMapping("/sections/{id}/close")
    @ApiOperation("管理员-关闭教学班")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "教学班ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<String> closeSection(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("PUT /admin/sections/{}/close", id);

        Map<String, Object> section = courseSectionMapper.selectByIdForAdmin(id);
        if (section == null) return R.error("教学班不存在", 404);

        courseSectionMapper.closeSection(id);
        return R.success("教学班已关闭");
    }

    private String validateSection(Map<String, Object> body, Map<String, Object> existing) {
        // courseId、teacherId、classroomId 必须存在
        Object courseIdObj = body.getOrDefault("courseId", existing != null ? existing.get("courseId") : null);
        Object teacherIdObj = body.getOrDefault("teacherId", existing != null ? existing.get("teacherId") : null);
        Object classroomIdObj = body.getOrDefault("classroomId", existing != null ? existing.get("classroomId") : null);

        if (courseIdObj == null) return "课程ID不能为空";
        if (teacherIdObj == null) return "教师ID不能为空";
        if (classroomIdObj == null) return "教室ID不能为空";

        Integer courseId = safeInt(courseIdObj);
        if (courseId == null) return "courseId 格式错误，必须是数字";
        Integer teacherId = safeInt(teacherIdObj);
        if (teacherId == null) return "teacherId 格式错误，必须是数字";
        Integer classroomId = safeInt(classroomIdObj);
        if (classroomId == null) return "classroomId 格式错误，必须是数字";

        if (courseMapper.selectByIdForAdmin(courseId) == null) return "课程不存在";
        if (teacherMapper.selectByTeacherId(teacherId) == null) return "教师不存在";
        if (classroomMapper.selectById(classroomId) == null) return "教室不存在";

        // capacity > 0
        Object capObj = body.getOrDefault("capacityLimit", existing != null ? existing.get("capacityLimit") : null);
        if (capObj != null) {
            Integer cap = safeInt(capObj);
            if (cap == null) return "capacityLimit 格式错误，必须是数字";
            if (cap <= 0) return "容量必须大于0";
            // capacity >= selectedCount
            int selCount = existing != null ? toInt(existing.get("selectedCount")) : 0;
            if (cap < selCount) return "容量不能小于已选人数(" + selCount + ")";
        }

        return null;
    }

    /**
     * 安全解析整数，非法输入返回 null（避免抛 500）
     */
    private Integer safeInt(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof Number) return ((Number) obj).intValue();
        if (obj instanceof String) {
            String s = ((String) obj).trim();
            if (s.isEmpty()) return null;
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private int toInt(Object obj) {
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof Number) return ((Number) obj).intValue();
        if (obj instanceof String) return Integer.parseInt((String) obj);
        return 0;
    }

    // ==================== 院系管理 ====================

    @GetMapping("/departments")
    @ApiOperation("管理员-查询院系列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<List<com.agiantii.backend.pojo.Department>> listDepartments(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("GET /admin/departments");
        return R.success(departmentMapper.selectAll(), "成功");
    }

    @GetMapping("/departments/{id}")
    @ApiOperation("管理员-查询院系详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "院系ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<com.agiantii.backend.pojo.Department> getDepartment(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("GET /admin/departments/{}", id);
        com.agiantii.backend.pojo.Department dept = departmentMapper.selectById(id);
        if (dept == null) return R.error("院系不存在", 404);
        return R.success(dept, "成功");
    }

    @PostMapping("/departments")
    @ApiOperation("管理员-新增院系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<com.agiantii.backend.pojo.Department> addDepartment(
            @RequestBody com.agiantii.backend.pojo.Department department,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("POST /admin/departments: {}", department);
        if (department.getDepartmentName() == null || department.getDepartmentName().trim().isEmpty()) {
            return R.error("院系名称不能为空", 400);
        }
        if (department.getStatus() == null) department.setStatus(1);
        departmentMapper.insert(department);
        return R.success(department, "院系新增成功");
    }

    @PutMapping("/departments/{id}")
    @ApiOperation("管理员-修改院系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "院系ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<com.agiantii.backend.pojo.Department> updateDepartment(
            @PathVariable Integer id,
            @RequestBody com.agiantii.backend.pojo.Department department,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("PUT /admin/departments/{}: {}", id, department);
        if (departmentMapper.selectById(id) == null) return R.error("院系不存在", 404);

        // 只更新前端传入的字段，未传字段从已有记录回填
        com.agiantii.backend.pojo.Department existing = departmentMapper.selectById(id);
        if (department.getDepartmentCode() == null) department.setDepartmentCode(existing.getDepartmentCode());
        if (department.getDepartmentName() == null) department.setDepartmentName(existing.getDepartmentName());
        if (department.getOfficePhone() == null) department.setOfficePhone(existing.getOfficePhone());

        department.setDepartmentId(id);
        departmentMapper.update(department);
        return R.success(departmentMapper.selectById(id), "院系修改成功");
    }

    @PutMapping("/departments/{id}/disable")
    @ApiOperation("管理员-停用院系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "院系ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<String> disableDepartment(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("PUT /admin/departments/{}/disable", id);
        if (departmentMapper.selectById(id) == null) return R.error("院系不存在", 404);

        int refMajors = departmentMapper.countReferencedMajors(id);
        int refTeachers = departmentMapper.countReferencedTeachers(id);
        if (refMajors > 0 || refTeachers > 0) {
            return R.error("该院系下存在 " + refMajors + " 个专业、" + refTeachers + " 名教师，无法停用", 400);
        }

        departmentMapper.disable(id);
        return R.success("院系已停用");
    }

    // ==================== 专业管理 ====================

    @GetMapping("/majors")
    @ApiOperation("管理员-查询专业列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<List<Map<String, Object>>> listMajors(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("GET /admin/majors");
        return R.success(majorMapper.selectAllWithDept(), "成功");
    }

    @GetMapping("/majors/{id}")
    @ApiOperation("管理员-查询专业详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "专业ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<Map<String, Object>> getMajor(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("GET /admin/majors/{}", id);
        Map<String, Object> major = majorMapper.selectByIdWithDept(id);
        if (major == null) return R.error("专业不存在", 404);
        return R.success(major, "成功");
    }

    @PostMapping("/majors")
    @ApiOperation("管理员-新增专业")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<com.agiantii.backend.pojo.Major> addMajor(
            @RequestBody com.agiantii.backend.pojo.Major major,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("POST /admin/majors: {}", major);
        if (major.getMajorName() == null || major.getMajorName().trim().isEmpty()) {
            return R.error("专业名称不能为空", 400);
        }
        if (major.getDepartmentId() == null) {
            return R.error("所属院系不能为空", 400);
        }
        if (departmentMapper.selectById(major.getDepartmentId()) == null) {
            return R.error("所属院系不存在", 400);
        }
        if (major.getStatus() == null) major.setStatus(1);
        majorMapper.insert(major);
        return R.success(major, "专业新增成功");
    }

    @PutMapping("/majors/{id}")
    @ApiOperation("管理员-修改专业")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "专业ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<Map<String, Object>> updateMajor(
            @PathVariable Integer id,
            @RequestBody com.agiantii.backend.pojo.Major major,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("PUT /admin/majors/{}: {}", id, major);
        if (majorMapper.selectById(id) == null) return R.error("专业不存在", 404);
        if (major.getDepartmentId() != null && departmentMapper.selectById(major.getDepartmentId()) == null) {
            return R.error("所属院系不存在", 400);
        }

        // 只更新前端传入的字段，未传字段从已有记录回填
        com.agiantii.backend.pojo.Major existingMajor = majorMapper.selectById(id);
        if (major.getDepartmentId() == null) major.setDepartmentId(existingMajor.getDepartmentId());
        if (major.getMajorCode() == null) major.setMajorCode(existingMajor.getMajorCode());
        if (major.getMajorName() == null) major.setMajorName(existingMajor.getMajorName());

        major.setMajorId(id);
        majorMapper.update(major);
        return R.success(majorMapper.selectByIdWithDept(id), "专业修改成功");
    }

    @PutMapping("/majors/{id}/disable")
    @ApiOperation("管理员-停用专业")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "专业ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<String> disableMajor(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("PUT /admin/majors/{}/disable", id);
        if (majorMapper.selectById(id) == null) return R.error("专业不存在", 404);

        int refStudents = majorMapper.countReferencedStudents(id);
        if (refStudents > 0) {
            return R.error("该专业下存在 " + refStudents + " 名学生，无法停用", 400);
        }

        majorMapper.disable(id);
        return R.success("专业已停用");
    }

    // ==================== 教室管理 ====================

    @GetMapping("/classrooms")
    @ApiOperation("管理员-查询教室列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<List<com.agiantii.backend.pojo.Classroom>> listClassrooms(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("GET /admin/classrooms");
        return R.success(classroomMapper.selectAll(), "成功");
    }

    @GetMapping("/classrooms/{id}")
    @ApiOperation("管理员-查询教室详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "教室ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<com.agiantii.backend.pojo.Classroom> getClassroom(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("GET /admin/classrooms/{}", id);
        com.agiantii.backend.pojo.Classroom room = classroomMapper.selectById(id);
        if (room == null) return R.error("教室不存在", 404);
        return R.success(room, "成功");
    }

    @PostMapping("/classrooms")
    @ApiOperation("管理员-新增教室")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<com.agiantii.backend.pojo.Classroom> addClassroom(
            @RequestBody com.agiantii.backend.pojo.Classroom classroom,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("POST /admin/classrooms: {}", classroom);
        if (classroom.getBuilding() == null || classroom.getBuilding().trim().isEmpty()) {
            return R.error("教学楼不能为空", 400);
        }
        if (classroom.getRoomNo() == null || classroom.getRoomNo().trim().isEmpty()) {
            return R.error("教室编号不能为空", 400);
        }
        if (classroom.getStatus() == null) classroom.setStatus(1);
        if (classroom.getCapacity() == null) classroom.setCapacity(50);
        classroomMapper.insert(classroom);
        return R.success(classroom, "教室新增成功");
    }

    @PutMapping("/classrooms/{id}")
    @ApiOperation("管理员-修改教室")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "教室ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<com.agiantii.backend.pojo.Classroom> updateClassroom(
            @PathVariable Integer id,
            @RequestBody com.agiantii.backend.pojo.Classroom classroom,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("PUT /admin/classrooms/{}: {}", id, classroom);
        if (classroomMapper.selectById(id) == null) return R.error("教室不存在", 404);

        // 只更新前端传入的字段，未传字段从已有记录回填
        com.agiantii.backend.pojo.Classroom existingRoom = classroomMapper.selectById(id);
        if (classroom.getBuilding() == null) classroom.setBuilding(existingRoom.getBuilding());
        if (classroom.getRoomNo() == null) classroom.setRoomNo(existingRoom.getRoomNo());
        if (classroom.getCapacity() == null) classroom.setCapacity(existingRoom.getCapacity());
        if (classroom.getRemark() == null) classroom.setRemark(existingRoom.getRemark());

        classroom.setClassroomId(id);
        classroomMapper.update(classroom);
        return R.success(classroomMapper.selectById(id), "教室修改成功");
    }

    @PutMapping("/classrooms/{id}/disable")
    @ApiOperation("管理员-停用教室")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "教室ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<String> disableClassroom(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("PUT /admin/classrooms/{}/disable", id);
        if (classroomMapper.selectById(id) == null) return R.error("教室不存在", 404);

        int refSections = classroomMapper.countReferencedSections(id);
        if (refSections > 0) {
            return R.error("该教室下存在 " + refSections + " 个运行中的教学班，无法停用", 400);
        }

        classroomMapper.disable(id);
        return R.success("教室已停用");
    }

    // ==================== 下拉数据接口 ====================

    @GetMapping("/courses/options")
    @ApiOperation("管理员-课程下拉列表（用于新增教学班表单）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<List<Map<String, Object>>> courseOptions(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("GET /admin/courses/options");
        return R.success(courseMapper.selectActiveForOptions(), "成功");
    }

    @GetMapping("/teachers/options")
    @ApiOperation("管理员-教师下拉列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<List<com.agiantii.backend.pojo.Teacher>> teacherOptions(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("GET /admin/teachers/options");
        return R.success(teacherMapper.selectAll(), "成功");
    }

    @GetMapping("/classrooms/options")
    @ApiOperation("管理员-教室下拉列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<List<com.agiantii.backend.pojo.Classroom>> classroomOptions(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (requireAdmin(authHeader) == null) return R.error("未登录或非管理员", 401);
        log.info("GET /admin/classrooms/options");
        return R.success(classroomMapper.selectActiveAll(), "成功");
    }
}
