package com.agiantii.backend.mapper;


import com.agiantii.backend.pojo.Teacher;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TeacherMapper {

    @Insert("insert into t_teacher(id,name) values (#{id},#{name})")
    public void insert(Teacher teacher);


    @Delete("delete from t_teacher where id = #{id}")
    public void delete(Integer id);

    @Update("update t_teacher set name = #{name} where id = #{id}")
    public void update(Teacher teacher);

    @Select("select * from t_teacher where id = #{id}")
    public Teacher selectByTeacherId(Integer id);

    @Select("select * from t_teacher where name like concat ('%',#{name},'%')")
    public List<Teacher> selectByTeacherName(@Param("name") String name);
    // with fuzzy default search

    @Select("select * from t_teacher")
    public List<Teacher> selectAll();

    @Select("select teacher_name from t_teacher where user_id = #{userId}")
    String selectNameByUserId(@Param("userId") Integer userId);

    @Select("select teacher_id from t_teacher where user_id = #{userId}")
    Integer selectIdByUserId(@Param("userId") Integer userId);

    @Select("select teacher_name from t_teacher where teacher_id = #{teacherId}")
    String selectNameByTeacherId(@Param("teacherId") Integer teacherId);
}
