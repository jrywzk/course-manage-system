package com.agiantii.backend.controller;

import com.agiantii.backend.common.R;
import com.agiantii.backend.mapper.V2ScoreMapper;
import com.agiantii.backend.pojo.ScoreV2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Api(tags = "P0-成绩管理")
public class ScoreV2Controller {

    @Resource
    private V2ScoreMapper v2ScoreMapper;

    @Resource
    private com.agiantii.backend.mapper.EnrollmentMapper enrollmentMapper;

    @Resource
    private com.agiantii.backend.mapper.TeacherMapper teacherMapper;

    @PostMapping("/scores")
    @ApiOperation("录入/修改成绩（依附 enrollment）")
    public R<Map<String, Object>> grade(@RequestBody Map<String, Object> body) {
        Integer enrollmentId = (Integer) body.get("enrollmentId");
        Integer teacherId = (Integer) body.get("teacherId");
        Object usualObj = body.get("usualScore");
        Object examObj = body.get("examScore");

        if (enrollmentId == null || teacherId == null || usualObj == null || examObj == null) {
            return R.error("enrollmentId、teacherId、usualScore、examScore不能为空");
        }

        BigDecimal usualScore = new BigDecimal(usualObj.toString());
        BigDecimal examScore = new BigDecimal(examObj.toString());

        if (usualScore.compareTo(BigDecimal.ZERO) < 0 || usualScore.compareTo(new BigDecimal("100")) > 0
                || examScore.compareTo(BigDecimal.ZERO) < 0 || examScore.compareTo(new BigDecimal("100")) > 0) {
            return R.error("成绩超出范围 0-100");
        }

        if (enrollmentMapper.selectById(enrollmentId) == null) {
            return R.error("选课记录不存在", 404);
        }

        // 校验该 enrollment 是否属于当前教师的教学班
        Integer matchedSectionId = enrollmentMapper.findSectionIdByEnrollmentAndTeacher(enrollmentId, teacherId);
        if (matchedSectionId == null) {
            return R.error("该选课记录不属于您的教学班", 403);
        }

        // 获取教师姓名作为 gradedBy
        String teacherName = teacherMapper.selectNameByTeacherId(teacherId);
        if (teacherName == null) {
            teacherName = "未知教师";
        }

        log.info("POST /scores: enrollmentId={}, teacherId={}, usualScore={}, examScore={}", enrollmentId, teacherId, usualScore, examScore);

        try {
            BigDecimal finalScore = usualScore.multiply(new BigDecimal("0.3"))
                    .add(examScore.multiply(new BigDecimal("0.7")))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal gpaPoint = calculateGpa(finalScore);
            int isPassed = finalScore.compareTo(new BigDecimal("60")) >= 0 ? 1 : 0;

            ScoreV2 score = new ScoreV2();
            score.setEnrollmentId(enrollmentId);
            score.setUsualScore(usualScore);
            score.setExamScore(examScore);
            score.setFinalScore(finalScore);
            score.setGpaPoint(gpaPoint);
            score.setIsPassed(isPassed);
            score.setGradedBy(teacherName);

            ScoreV2 existing = v2ScoreMapper.selectByEnrollmentId(enrollmentId);
            if (existing != null) {
                v2ScoreMapper.updateByEnrollmentId(score);
            } else {
                v2ScoreMapper.insert(score);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("scoreId", score.getScoreId());
            data.put("enrollmentId", enrollmentId);
            data.put("usualScore", usualScore);
            data.put("examScore", examScore);
            data.put("finalScore", finalScore);
            data.put("gpaPoint", gpaPoint);
            data.put("isPassed", isPassed);

            String message = existing != null ? "成绩修改成功" : "成绩录入成功";
            return R.success(data, message);
        } catch (Exception e) {
            log.error("grade error: {}", e.getMessage(), e);
            return R.error("系统错误", 500);
        }
    }

    @GetMapping("/sections/{sectionId}/scores")
    @ApiOperation("教学班成绩汇总")
    @ApiImplicitParam(name = "sectionId", value = "教学班ID", required = true, paramType = "path")
    public R<Map<String, Object>> sectionScores(@PathVariable Integer sectionId) {
        log.info("GET /sections/{}/scores", sectionId);
        List<Map<String, Object>> students = v2ScoreMapper.selectSectionScores(sectionId);

        int totalStudents = students.size();
        int gradedCount = 0;
        for (Map<String, Object> s : students) {
            if (s.get("finalScore") != null) {
                gradedCount++;
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("totalStudents", totalStudents);
        data.put("gradedCount", gradedCount);
        data.put("ungradedCount", totalStudents - gradedCount);
        data.put("students", students);

        return R.success(data, "成功");
    }

    private BigDecimal calculateGpa(BigDecimal score) {
        double s = score.doubleValue();
        double gpa;
        if (s >= 90) gpa = 4.0;
        else if (s >= 85) gpa = 3.7;
        else if (s >= 82) gpa = 3.3;
        else if (s >= 78) gpa = 3.0;
        else if (s >= 75) gpa = 2.7;
        else if (s >= 72) gpa = 2.3;
        else if (s >= 68) gpa = 2.0;
        else if (s >= 64) gpa = 1.5;
        else if (s >= 60) gpa = 1.0;
        else gpa = 0.0;
        return new BigDecimal(String.valueOf(gpa));
    }
}
