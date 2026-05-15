package com.agiantii.backend.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("选课记录视图（含教学班/课程/教师/成绩信息）")
public class EnrollmentVo {
    @ApiModelProperty("选课记录ID")
    private Integer enrollmentId;

    @ApiModelProperty("教学班ID")
    private Integer sectionId;

    @ApiModelProperty("教学班编码")
    private String sectionCode;

    @ApiModelProperty("学期")
    private String semester;

    @ApiModelProperty("课程编码")
    private String courseCode;

    @ApiModelProperty("课程名称")
    private String courseName;

    @ApiModelProperty("学分")
    private BigDecimal credit;

    @ApiModelProperty("授课教师")
    private String teacherName;

    @ApiModelProperty("上课时间")
    private String scheduleText;

    @ApiModelProperty("教学楼")
    private String building;

    @ApiModelProperty("教室号")
    private String roomNo;

    @ApiModelProperty("选课时间")
    private String selectTime;

    @ApiModelProperty("选课状态 1=在读 0=已退课")
    private Integer status;

    @ApiModelProperty("是否已有成绩")
    private Boolean hasScore;

    @ApiModelProperty("平时成绩（有成绩时返回）")
    private BigDecimal usualScore;

    @ApiModelProperty("考试成绩（有成绩时返回）")
    private BigDecimal examScore;

    @ApiModelProperty("综合成绩（有成绩时返回）")
    private BigDecimal finalScore;

    @ApiModelProperty("GPA绩点（有成绩时返回）")
    private BigDecimal gpaPoint;

    @ApiModelProperty("是否通过（有成绩时返回）")
    private Integer isPassed;
}
