package com.agiantii.backend.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("t_admin")
@Data
public class Admin {
    @TableId(value = "admin_id", type = IdType.AUTO)
    private Integer adminId;

    @TableField("admin_name")
    private String adminName;
}
