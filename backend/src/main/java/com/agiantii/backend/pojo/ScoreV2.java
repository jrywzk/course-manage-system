package com.agiantii.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_score")
@ApiModel("成绩V2（弱实体，依附于选课记录）")
public class ScoreV2 {
    @TableId(value = "score_id", type = IdType.AUTO)
    @ApiModelProperty("成绩ID")
    private Integer scoreId;

    @TableField("enrollment_id")
    @ApiModelProperty("选课记录ID")
    private Integer enrollmentId;

    @TableField("usual_score")
    @ApiModelProperty("平时成绩（0-100）")
    private BigDecimal usualScore;

    @TableField("exam_score")
    @ApiModelProperty("考试成绩（0-100）")
    private BigDecimal examScore;

    @TableField("final_score")
    @ApiModelProperty("综合成绩 = usual*0.3 + exam*0.7")
    private BigDecimal finalScore;

    @TableField("gpa_point")
    @ApiModelProperty("GPA绩点（8档换算）")
    private BigDecimal gpaPoint;

    @TableField("is_passed")
    @ApiModelProperty("是否通过 1=通过 0=未通过")
    private Integer isPassed;

    @TableField("graded_at")
    @ApiModelProperty("评分时间")
    private Date gradedAt;

    @TableField("graded_by")
    @ApiModelProperty("评分人")
    private String gradedBy;
}
