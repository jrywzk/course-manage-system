package com.agiantii.backend.mapper;


import com.agiantii.backend.pojo.Teacher;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TeacherMapper {

    @Insert("insert into t_teacher(teacher_id, teacher_name) values (#{teacherId}, #{teacherName})")
    public void insert(Teacher teacher);


    @Delete("delete from t_teacher where teacher_id = #{teacherId}")
    public void delete(Integer teacherId);

    @Update("update t_teacher set teacher_name = #{teacherName} where teacher_id = #{teacherId}")
    public void update(Teacher teacher);

    @Select("select teacher_id, teacher_name from t_teacher where teacher_id = #{teacherId}")
    public Teacher selectByTeacherId(Integer teacherId);

    @Select("select teacher_id, teacher_name from t_teacher where teacher_name like concat ('%',#{name},'%')")
    public List<Teacher> selectByTeacherName(@Param("name") String name);

    @Select("select teacher_id, teacher_name from t_teacher")
    public List<Teacher> selectAll();

    @Select("select teacher_name from t_teacher where user_id = #{userId}")
    String selectNameByUserId(@Param("userId") Integer userId);

    @Select("select teacher_id from t_teacher where user_id = #{userId}")
    Integer selectIdByUserId(@Param("userId") Integer userId);

    @Select("select teacher_name from t_teacher where teacher_id = #{teacherId}")
    String selectNameByTeacherId(@Param("teacherId") Integer teacherId);
}
