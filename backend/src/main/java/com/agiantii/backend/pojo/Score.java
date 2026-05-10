package com.agiantii.backend.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_score")
public class Score {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField(value = "course_id")
    private Integer courseId;
    @TableField(value = "student_id")
    private Integer studentId;
    @TableField(value = "teacher_id")
    private Integer teacherId;
    @TableField(value = "score")
    private Integer score;
         // ✅ 新增：成绩依附 enrollment
    // @TableField("enrollment_id")
    // private Integer enrollmentId;
    // ✅ 新增：分项成绩
    // @TableField("daily_score")
    // private Integer dailyScore;    // 平时成绩
    // @TableField("exam_score")
    // private Integer examScore;     // 考试成绩
    // @TableField("total_score")
    // private Integer totalScore;    // 总评成绩
    // @TableField("gpa")
    // private Double gpa;            // 绩点
    // @TableField("is_pass")
    // private Boolean isPass;        // 是否通过
}
