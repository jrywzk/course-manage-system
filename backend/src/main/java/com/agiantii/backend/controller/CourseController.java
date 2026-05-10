package com.agiantii.backend.controller;

import com.agiantii.backend.common.R;
import com.agiantii.backend.mapper.CourseMapper;
import com.agiantii.backend.pojo.Course;
import com.agiantii.backend.pojo.vo.CourseVo;
import lombok.extern.slf4j.Slf4j;

import org.apache.ibatis.annotations.Param;// 【删除】这个 import 是错的，应该删掉
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.SQLException;// 【删除】没用到的 import，应该删掉
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/course")
public class CourseController {
    @Resource
    private CourseMapper courseMapper;

    @PostMapping("insert")
     public R<String> insertCourse(@RequestBody Course course) {
        log.info("insertCourse {}",course);
        try {
            courseMapper.insert(course);
            return R.success("添加课程成功");
        }
        catch (Exception e) {
            // 【新增】下面这行原来没有
            // log.error("insertCourse error: {}", e.getMessage());
            return R.error("该课程已存在");
        }
    }
    @PostMapping("update")
    public R<String> updateCourse(@RequestBody Course course) {
        log.info("updateCourse {}",course);
        try {
            courseMapper.update(course);
            return R.success("更新课程成功");
        }
        catch (Exception e) {
            // 【新增】下面这行原来没有
            // log.error("updateCourse error: {}", e.getMessage());
            return R.error(e.getMessage());
        }
    }
//    @GetMapping("deleteByCourseId")
//    public R<String>deleteByCourseId(@RequestParam("courseId") Integer courseId) {
//        log.info("deleteByCourseId: {}", courseId);
//        try{
//            courseMapper.deleteByCourseId(courseId);
//            return R.success("删除成功");
//        }
//        catch (Exception e) {
//            return R.error(e.getMessage());
//        }
//    }
    // 【改动】上面整段取消注释，恢复正常使用，并在 catch 里加 log.error
    // 取消注释后变成：
    // @GetMapping("deleteByCourseId")
    // public R<String> deleteByCourseId(@RequestParam("courseId") Integer courseId) {
    //     log.info("deleteByCourseId: {}", courseId);
    //     try {
    //         courseMapper.deleteByCourseId(courseId);
    //         return R.success("删除成功");
    //     } catch (Exception e) {
    //         log.error("deleteByCourseId error: {}", e.getMessage());  // ← 这行是新增
    //         return R.error(e.getMessage());
    //     }
    // }
    @GetMapping("deleteByCourseIdAndTeacherId")
    // 【改动】第二个参数 @Param → @RequestParam
    // 原来：@Param("teacherId") Integer teacherId
    // 改成：@RequestParam("teacherId") Integer teacherId
    public R<String> deleteByCourseIdAndTeacherId(@RequestParam("courseId") Integer courseId, @Param("teacherId") Integer teacherId){
        log.info("deleteByCourseIdAndTeacherId: teacherId:{}, courseId:{}", teacherId,courseId);
        try{
            courseMapper.deleteByCourseIdAndTeacherId(courseId,teacherId);
            return R.success("删除成功");
        }
        catch (Exception e) {
            // 【新增】下面这行原来没有
            // log.error("deleteByCourseIdAndTeacherId error: {}", e.getMessage());
            return R.error(e.getMessage());
        }
    }
    @GetMapping("selectByCourseName")
    public R<List<CourseVo>> selectCourseByName(String courseName) {
        log.info("selectByCourseName: {}", courseName );
        // 【改动】下面这行到 return 之间，整段用 try-catch 包起来
        // try {
        List<CourseVo> courseVos = courseMapper.selectByCourseName(courseName);
        return R.success(courseVos,"查询成功");
        // } catch (Exception e) {
        //     log.error("selectByCourseName error: {}", e.getMessage());
        //     return R.error("查询课程失败");
        // }
    }
    @GetMapping("selectByTeacherName")
    // 【改动】缺少 log.info，建议补上
    // 新增：log.info("selectByTeacherName: {}", teacherName);
    public R<List<CourseVo>> selectByTeacherName(String teacherName) {
        // 【改动】整段用 try-catch 包起来
        // try {
        List<CourseVo> courseVos = courseMapper.selectByTeacherName(teacherName);
        return R.success(courseVos,"查询成功");
        // } catch (Exception e) {
        //     log.error("selectByTeacherName error: {}", e.getMessage());
        //     return R.error("查询课程失败");
        // }
    }
    @GetMapping("selectByCourseId")
    public R<CourseVo> selectByCourseId(Integer courseId) {
        // 【改动】整段用 try-catch 包起来
        // try {
        CourseVo courseVo = courseMapper.selectByCourseId(courseId);
        return R.success(courseVo,"查询成功");
        // } catch (Exception e) {
        //     log.error("selectByCourseId error: {}", e.getMessage());
        //     return R.error("查询课程失败");
        // }
    }
    @GetMapping("selectByTeacherId")
    public R<List<CourseVo>> selectByTeacherId(Integer teacherId) {
        // 【改动】整段用 try-catch 包起来
        // try {
        List<CourseVo> courseVos = courseMapper.selectByTeacherId(teacherId);
        return R.success(courseVos,"查询成功");
        // } catch (Exception e) {
        //     log.error("selectByTeacherId error: {}", e.getMessage());
        //     return R.error("查询课程失败");
        // }
    }
    @GetMapping("selectAll")
    public R<List<CourseVo>> selectAll() {
        // 【改动】整段用 try-catch 包起来
        // try {
        List<CourseVo> courseVos = courseMapper.selectAll();
        return R.success(courseVos,"查询成功");
        // } catch (Exception e) {
        //     log.error("selectAll error: {}", e.getMessage());
        //     return R.error("查询课程失败");
        // }
    }
}
