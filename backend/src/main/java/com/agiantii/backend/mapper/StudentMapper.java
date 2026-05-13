package com.agiantii.backend.mapper;

import com.agiantii.backend.pojo.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentMapper {
    @Insert("insert into t_student(student_id, student_name) values (#{studentId}, #{studentName})")
    void insertStudent(Student student);

    @Delete("delete from t_student where student_id = #{studentId}")
    void deleteStudentById(Integer studentId);

    @Update("update t_student set student_name = #{studentName} where student_id = #{studentId}")
    void updateStudent(Student student);

    @Select("select student_id, student_name from t_student where student_name like CONCAT('%',#{name},'%')")
    List<Student> selectByStudentName(@Param("name") String studentName);

    @Select("select student_id, student_name from t_student")
    List<Student> selectAllStudent();

    @Select("select student_id, student_name from t_student where student_id = #{studentId}")
    Student selectStudentById(Integer studentId);

    @Select("select student_name from t_student where user_id = #{userId}")
    String selectNameByUserId(@Param("userId") Integer userId);

    @Select("select student_id from t_student where user_id = #{userId}")
    Integer selectIdByUserId(@Param("userId") Integer userId);
}
