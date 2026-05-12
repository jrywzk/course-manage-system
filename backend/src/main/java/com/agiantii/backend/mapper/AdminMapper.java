package com.agiantii.backend.mapper;

import com.agiantii.backend.pojo.Admin;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminMapper {

    @Insert("insert into t_admin(id,name) values(#{id}, #{name})")
    void insertAdmin(Admin admin);

    @Delete("delete from t_admin where id = #{id}")
    void deleteByAdminId(@Param("id") Integer id);

    @Update("update t_admin set name = #{name} where id = #{id}")
    void updateAdmin(Admin admin);
    @Select("select * from t_admin where name like concat('%',#{name},'%')")
    public List<Admin> selectByName(@Param("name") String name);

    @Select("select * from t_admin")
    public List<Admin> selectAll();

    @Select("select admin_name from t_admin where user_id = #{userId}")
    String selectNameByUserId(@Param("userId") Integer userId);

    @Select("select admin_id from t_admin where user_id = #{userId}")
    Integer selectIdByUserId(@Param("userId") Integer userId);
}
