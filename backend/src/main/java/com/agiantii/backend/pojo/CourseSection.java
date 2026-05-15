package com.agiantii.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("t_course_section")
@ApiModel("教学班")
public class CourseSection {
    @TableId(value = "section_id", type = IdType.AUTO)
    @ApiModelProperty("教学班ID")
    private Integer sectionId;

    @TableField("course_id")
    @ApiModelProperty("课程ID")
    private Integer courseId;

    @TableField("teacher_id")
    @ApiModelProperty("教师ID")
    private Integer teacherId;

    @TableField("classroom_id")
    @ApiModelProperty("教室ID")
    private Integer classroomId;

    @TableField("section_code")
    @ApiModelProperty("教学班编码")
    private String sectionCode;

    @TableField("semester")
    @ApiModelProperty("学期")
    private String semester;

    @TableField("schedule_text")
    @ApiModelProperty("上课时间描述")
    private String scheduleText;

    @TableField("capacity_limit")
    @ApiModelProperty("容量上限")
    private Integer capacityLimit;

    @TableField("selected_count")
    @ApiModelProperty("已选人数（触发器维护）")
    private Integer selectedCount;

    @TableField("status")
    @ApiModelProperty("状态 1=开课 0=停开")
    private Integer status;
}
