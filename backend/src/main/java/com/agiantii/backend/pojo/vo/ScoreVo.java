package com.agiantii.backend.pojo.vo;

import com.agiantii.backend.pojo.Score;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ScoreVo extends Score{
    private String teacherName;
    private String studentName;
    private String courseName;
 //  新增：新版业务链需要的字段
    //private String sectionCode;    教学班编号
    //private String semester;       学期
}
