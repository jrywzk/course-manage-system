package com.agiantii.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_enrollment")
@ApiModel("选课记录")
public class Enrollment {
    @TableId(value = "enrollment_id", type = IdType.AUTO)
    @ApiModelProperty("选课记录ID")
    private Integer enrollmentId;

    @TableField("student_id")
    @ApiModelProperty("学生ID")
    private Integer studentId;

    @TableField("section_id")
    @ApiModelProperty("教学班ID")
    private Integer sectionId;

    @TableField("select_time")
    @ApiModelProperty("选课时间")
    private Date selectTime;

    @TableField("status")
    @ApiModelProperty("状态 1=正常在读 0=已退课")
    private Integer status;

    @TableField("source")
    @ApiModelProperty("选课来源")
    private String source;

    @TableField("remark")
    @ApiModelProperty("备注")
    private String remark;
}
