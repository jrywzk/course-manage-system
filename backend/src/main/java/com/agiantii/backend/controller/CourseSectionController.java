package com.agiantii.backend.controller;

import com.agiantii.backend.common.R;
import com.agiantii.backend.common.TokenStore;
import com.agiantii.backend.mapper.CourseSectionMapper;
import com.agiantii.backend.pojo.vo.CourseSectionVo;
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
@RequestMapping("/sections")
@Api(tags = "P0-教学班管理")
public class CourseSectionController {

    @Resource
    private CourseSectionMapper courseSectionMapper;

    @Resource
    private com.agiantii.backend.mapper.EnrollmentMapper enrollmentMapper;

    @Resource
    private TokenStore tokenStore;

    @GetMapping
    @ApiOperation("教学班列表（学生浏览可选课程）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", defaultValue = "10", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态 1=开课 0=停开", defaultValue = "1", paramType = "query")
    })
    public R<Map<String, Object>> list(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(defaultValue = "1") Integer status) {
        log.info("GET /sections: page={}, pageSize={}, status={}", page, pageSize, status);

        List<CourseSectionVo> allSections = courseSectionMapper.selectAllWithDetails(status);

        // 简单分页
        int total = allSections.size();
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);

        List<CourseSectionVo> pageList;
        if (fromIndex >= total) {
            pageList = List.of();
        } else {
            pageList = allSections.subList(fromIndex, toIndex);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);
        data.put("list", pageList);

        return R.success(data, "成功");
    }

    @GetMapping("/{id}")
    @ApiOperation("教学班详情")
    @ApiImplicitParam(name = "id", value = "教学班ID", required = true, paramType = "path")
    public R<CourseSectionVo> detail(@PathVariable Integer id) {
        log.info("GET /sections/{}", id);
        CourseSectionVo vo = courseSectionMapper.selectSectionDetail(id);
        if (vo == null) {
            return R.error("教学班不存在", 404);
        }
        return R.success(vo, "成功");
    }

    @GetMapping("/{sectionId}/students")
    @ApiOperation("教学班学生名单（教师查看自己教学班的学生）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sectionId", value = "教学班ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "teacherId", value = "教师ID（必填，校验该教学班是否属于该教师）", required = true, paramType = "query"),
            @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    })
    public R<List<Map<String, Object>>> sectionStudents(
            @PathVariable Integer sectionId,
            @RequestParam Integer teacherId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Token 身份校验：当前登录教师 entityId 必须与查询参数 teacherId 一致
        Map<String, Object> tokenInfo = tokenStore.validateOwner(authHeader, teacherId);
        if (tokenInfo == null) {
            return R.error("未登录或无权查看该教学班的学生名单", 401);
        }

        log.info("GET /sections/{}/students: authenticated teacherId={}", sectionId, teacherId);

        // 校验教学班是否存在
        CourseSectionVo section = courseSectionMapper.selectSectionDetail(sectionId);
        if (section == null) {
            return R.error("教学班不存在", 404);
        }

        // 必须校验教师是否拥有该教学班
        Integer actualTeacherId = courseSectionMapper.selectTeacherIdBySectionId(sectionId);
        if (actualTeacherId == null) {
            return R.error("教学班不存在", 404);
        }
        if (!actualTeacherId.equals(teacherId)) {
            return R.error("该教学班不属于您", 403);
        }

        List<Map<String, Object>> students = enrollmentMapper.selectSectionStudents(sectionId);
        return R.success(students, "成功");
    }
}
