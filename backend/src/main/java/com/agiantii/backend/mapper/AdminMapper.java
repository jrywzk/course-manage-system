package com.agiantii.backend.mapper;

import com.agiantii.backend.pojo.Admin;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminMapper {

    @Insert("insert into t_admin(admin_id, admin_name) values(#{adminId}, #{adminName})")
    void insertAdmin(Admin admin);

    @Delete("delete from t_admin where admin_id = #{adminId}")
    void deleteByAdminId(@Param("adminId") Integer adminId);

    @Update("update t_admin set admin_name = #{adminName} where admin_id = #{adminId}")
    void updateAdmin(Admin admin);

    @Select("select admin_id, admin_name from t_admin where admin_name like concat('%',#{name},'%')")
    public List<Admin> selectByName(@Param("name") String name);

    @Select("select admin_id, admin_name from t_admin")
    public List<Admin> selectAll();

    @Select("select admin_name from t_admin where user_id = #{userId}")
    String selectNameByUserId(@Param("userId") Integer userId);

    @Select("select admin_id from t_admin where user_id = #{userId}")
    Integer selectIdByUserId(@Param("userId") Integer userId);
}
