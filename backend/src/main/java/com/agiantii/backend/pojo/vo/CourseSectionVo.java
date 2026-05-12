package com.agiantii.backend.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("教学班视图（含课程/教师/教室信息）")
public class CourseSectionVo {
    @ApiModelProperty("教学班ID")
    private Integer sectionId;

    @ApiModelProperty("教学班编码")
    private String sectionCode;

    @ApiModelProperty("学期")
    private String semester;

    @ApiModelProperty("课程ID")
    private Integer courseId;

    @ApiModelProperty("课程编码")
    private String courseCode;

    @ApiModelProperty("课程名称")
    private String courseName;

    @ApiModelProperty("学分")
    private BigDecimal credit;

    @ApiModelProperty("总学时")
    private Integer totalHours;

    @ApiModelProperty("课程类型：必修/选修/公选")
    private String courseType;

    @ApiModelProperty("课程描述")
    private String description;

    @ApiModelProperty("授课教师姓名")
    private String teacherName;

    @ApiModelProperty("教师职称")
    private String title;

    @ApiModelProperty("教学楼")
    private String building;

    @ApiModelProperty("教室号")
    private String roomNo;

    @ApiModelProperty("上课时间描述")
    private String scheduleText;

    @ApiModelProperty("容量上限")
    private Integer capacityLimit;

    @ApiModelProperty("已选人数")
    private Integer selectedCount;

    @ApiModelProperty("剩余名额")
    private Integer remaining;

    @ApiModelProperty("状态 1=开课 0=停开")
    private Integer status;
}
