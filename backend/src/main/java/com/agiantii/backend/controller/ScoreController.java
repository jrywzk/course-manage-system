package com.agiantii.backend.controller;


import com.agiantii.backend.common.R;
import com.agiantii.backend.mapper.CourseMapper;
import com.agiantii.backend.mapper.ScoreMapper;
// 【新增】下面这行 import 原来没有
// import com.agiantii.backend.mapper.EnrollmentMapper;
import com.agiantii.backend.pojo.Score;
import com.agiantii.backend.pojo.vo.ScoreVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/score")
@Slf4j

public class ScoreController {
    @Resource
    private ScoreMapper scoreMapper;
      // 【新增】下面这行原来没有
    // @Resource
    // private EnrollmentMapper enrollmentMapper;
    // ================================================================
    // 【批注】以下 insertScore 方法本质是"选课"逻辑，不是"成绩录入"。
    //         新版架构中，选课逻辑迁移到 EnrollmentController.insert。
    //         此方法保留在此仅供对照，实际调用应走 EnrollmentController。
    // ================================================================
    @Resource
    private CourseMapper courseMapper;
  @PostMapping("insert")
    public R<String> insertScore(@RequestBody Score score) {
        log.info("insert course : {}",score);
        try{
            ScoreVo scoreVo = scoreMapper.selectByCourseIdAndStudentId(score.getCourseId(),score.getStudentId());
            if(scoreVo !=null){
                return R.error("该课程已经选修");
            }
            Integer nowCount = scoreMapper.countByCourseIdAndTeacherId(score.getCourseId(),score.getTeacherId());
            log.info("nowCount:{}",nowCount);
            Integer maxCount = courseMapper.countByCourseIdAndTeacherId(score.getCourseId(),score.getTeacherId());
            log.info("nowCount:{},maxCount:{}",nowCount,maxCount);
            if(nowCount >= maxCount){
                return R.error("该老师的课程数已满");
            }
            scoreMapper.insert(score);
            return R.success("选课成功");
        }catch (Exception e){
            log.error("insert course error : {}",e.getMessage());
            return R.error("insert course error");
        }
    }
    // ================================================================
    // 【批注】以下 deleteScore 方法本质是"退课"逻辑。
    //         新版架构中，退课逻辑迁移到 EnrollmentController.delete。
    //         此方法保留在此仅供对照，实际调用应走 EnrollmentController。
    // ================================================================
    @GetMapping("deleteByCourseIdAndStudentIdAndTeacherId")
    // 【改动】三个参数都缺少 @RequestParam，Spring MVC 可能无法正确绑定
    // 原来：Integer courseId,Integer studentId,Integer teacherId
    // 改成：@RequestParam("courseId") Integer courseId,
    //       @RequestParam("studentId") Integer studentId,
    //       @RequestParam("teacherId") Integer teacherId
    public R<String> deleteScore(Integer courseId,Integer studentId,Integer teacherId){
        log.info("delete course courseId:{},studentId:{},teacherId:{}",courseId,studentId,teacherId);
        try{
            scoreMapper.deleteByCourseIdAndStudentIdAndTeacherId(courseId,studentId,teacherId);
            return R.success("delete course success");
        }catch (Exception e){
            log.error("delete course error : {}",e.getMessage());
            return R.error("delete course error");
        }
    }
    // ================================================================
    // 【批注】以下 updateScore 是旧的成绩更新方式（基于 Score.id）。
    //         新版改为 grade 方法，基于 enrollmentId，自动判断新增/修改。
    //         此方法保留在此仅供对照。
    // ================================================================
    @PostMapping("update")
    public R<String> updateScore(@RequestBody Score score) {
        log.info("update course : {}",score);
        try{
            scoreMapper.update(score);
            return R.success("update course success");
        }catch (Exception e){
            log.error("update course error : {}",e.getMessage());
            return R.error("update course error");
        }
    }
    // ================================================================
    // 【新增】以下 grade 方法原来没有，是全新加的。
    //         用于成绩录入/修改，依附 enrollment_id 而非旧的 (courseId, studentId, teacherId)。
    //         自动判断 insert 还是 update。
    // ================================================================
    // @PostMapping("grade")
    // public R<String> grade(@RequestBody Score score,
    //                         @RequestParam("teacherId") Integer teacherId) {
    //     log.info("grade score: {}, teacherId: {}", score, teacherId);
    //     if (score.getEnrollmentId() == null ||
    //         enrollmentMapper.selectById(score.getEnrollmentId()) == null) {
    //         return R.error("选课记录不存在");
    //     }
    //     try {
    //         Score existing = scoreMapper.selectByEnrollmentId(score.getEnrollmentId());
    //         if (existing != null) {
    //             score.setId(existing.getId());
    //             scoreMapper.updateByEnrollmentId(score);
    //             return R.success("成绩修改成功");
    //         } else {
    //             scoreMapper.insert(score);
    //             return R.success("成绩录入成功");
    //         }
    //     } catch (Exception e) {
    //         log.error("grade error: {}", e.getMessage());
    //         return R.error("成绩录入失败");
    //     }
    // }
    // ================================================================
    // 【批注】以下查询方法无需改动，已经是最终形态。
    //         均有 @RequestParam、try-catch、log.error。
    // ================================================================
    @GetMapping("selectByCourseId")
    public R<List<ScoreVo>> selectScoreByCourseId(@RequestParam("courseId") int courseId) {
        log.info("getScoreByCourseId courseId:{}",courseId);
        try{
            List<ScoreVo> scoreVos = scoreMapper.selectByCourseId(courseId);
            return R.success(scoreVos,"查询 成绩 成功");
        }catch (Exception e){
            log.error("getScoreByCourseId error : {}",e.getMessage());
            return R.error("查询成绩 失败");
        }
    }
    @GetMapping("selectByStudentId")
    public R<List<ScoreVo>> selectScoreByStudentId(@RequestParam("studentId") int studentId) {
        log.info("getScoreByStudentId studentId:{}",studentId);
        try{
            List<ScoreVo> scoreVos = scoreMapper.selectByStudentId(studentId);
            return R.success(scoreVos,"查询 成绩 成功");
        }catch (Exception e) {
            log.error("getScoreByStudentId error : {}", e.getMessage());
            return R.error("查询成绩 失败");
        }
    }
    @GetMapping("selectByTeacherId")
    public R<List<ScoreVo>> selectScoreByTeacherId(@RequestParam("teacherId") int teacherId) {
        log.info("getScoreByTeacherId teacherId:{}",teacherId);
        try{
                 List<ScoreVo> scoreVos = scoreMapper.selectByTeacherId(teacherId);
                 return R.success(scoreVos,"查询 成绩 成功");
        }catch (Exception e) {
            log.error("getScoreByTeacherId error : {}", e.getMessage());
            return R.error("查询成绩 失败");
        }
    }
    @GetMapping("selectByTeacherIdAndCourseId")
    public R<List<ScoreVo>> selectScoreByTeacherIdAndCourseId(@RequestParam("teacherId") int teacherId, @RequestParam("courseId") int courseId) {
        log.info("getScoreByTeacherIdAndCourseId teacherId:{},courseId:{}",teacherId,courseId);
        try{
            List<ScoreVo> scoreVos = scoreMapper.selectByTeacherIdAndCourseId(teacherId,courseId);
            return R.success(scoreVos,"查询 成绩 成功");
            }
        catch (Exception e) {
                    log.error("getScoreByTeacherIdAndCourseId error : {}", e.getMessage());
                    return R.error("查询成绩 失败");
        }
    }
    @GetMapping("selectByStudentIdAndTerm")
    public R<List<ScoreVo>> selectScoreByStudentIdAndTerm(@RequestParam("studentId") int studentId, @RequestParam("term") String term) {
        log.info("getScoreByStudentIdAndTerm term:{} studentId:{}",term,studentId);
        try{
            List<ScoreVo> scoreVos = scoreMapper.selectByStudentIdAndTerm(studentId,term);
            return R.success(scoreVos,"查询 成绩 成功");
        }catch (Exception e) {
            log.error("getScoreByStudentIdAndTerm error : {}", e.getMessage());
            return R.error("查询成绩 失败");
        }
    }
    @GetMapping("selectByCourseName")
    public R<List<ScoreVo>> selectScoreByCourseName(@RequestParam("courseName") String courseName) {
        log.info("getScoreByCourseName courseName:{}",courseName);
        try{
            List<ScoreVo> scoreVos = scoreMapper.selectByCourseName(courseName);
            return R.success(scoreVos,"查询 成绩 成功");
            }
        catch (Exception e) {
            log.error("getScoreByCourseName error : {}", e.getMessage());
            return R.error("查询成绩 失败");
        }
    }
}

