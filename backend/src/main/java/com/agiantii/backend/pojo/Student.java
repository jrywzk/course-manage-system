package com.agiantii.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("t_student")
@Data
public class Student {
    @TableId(value = "student_id", type = IdType.AUTO)
    private Integer studentId;

    @TableField("student_name")
    private String studentName;
}
