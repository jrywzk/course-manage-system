package com.agiantii.backend.mapper;

import com.agiantii.backend.pojo.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentMapper {
    @Insert("insert into t_student(id,name) values (#{id},#{name})")
    void insertStudent(Student student);

    @Delete(("delete from t_student where id = ${id}"))
    void deleteStudentById(Integer id);

    @Update("update t_student set name = #{name} where id = #{id}")
    void updateStudent(Student student);

    @Select("select * from t_student where name like CONCAT('%',#{name},'%')")
    List<Student> selectByStudentName(@Param("name") String Studentname);
    // with fuzzy default search

    @Select("select * from t_student")
    List<Student> selectAllStudent();
    @Select("select * from t_student where id = #{id}")
    Student selectStudentById(Integer id);

    @Select("select student_name from t_student where user_id = #{userId}")
    String selectNameByUserId(@Param("userId") Integer userId);

    @Select("select student_id from t_student where user_id = #{userId}")
    Integer selectIdByUserId(@Param("userId") Integer userId);
}
