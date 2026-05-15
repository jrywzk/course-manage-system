package com.agiantii.backend.mapper;

import com.agiantii.backend.pojo.Course;
import com.agiantii.backend.pojo.vo.CourseVo;
import org.apache.ibatis.annotations.*;

import java.util.List;
@Mapper
// 注意：本 Mapper 基于 V1 表结构编写，V2 迁移后 t_course 列名已变化，
// 已修复 JOIN 中 t_teacher 的列名引用，其余列名问题待相关模块整体迁移时处理。
public interface CourseMapper {
    @Insert("insert into t_course(teacher_id,id,name,term,credit,student_limit) values (#{teacherId},#{id},#{name},#{term},#{credit},#{studentLimit})")
    void insert(Course course);

    @Delete("delete from t_course where id = #{id}")
    void deleteByCourseId(Integer id);

    @Delete("delete from t_course where id = #{id} and teacher_id = #{teacherId}")
    void deleteByCourseIdAndTeacherId(@Param("id") Integer id, @Param("teacherId") Integer teacherId);

    @Update("update t_course set name = #{name}, term = #{term}, credit = #{credit},student_limit = #{studentLimit} where id = #{id} and teacher_id = #{teacherId}")
    void update(Course course);

    @Select("select course.*, teacher.teacher_name as teacherName from t_course as course left join t_teacher as teacher on course.teacher_id = teacher.teacher_id where course.name like concat('%',#{courseName},'%')")
    List<CourseVo> selectByCourseName(String courseName);

    @Select("select course.*, teacher.teacher_name as teacherName from t_course as course left join t_teacher as teacher on course.teacher_id = teacher.teacher_id where teacher.teacher_name like concat('%',#{teacherName},'%')")
    List<CourseVo> selectByTeacherName(String teacherName);

    @Select("select student_limit from t_course where id = #{courseId} and teacher_id = #{teacherId}")
    Integer countByCourseIdAndTeacherId(@Param("courseId") Integer courseId, @Param("teacherId") Integer teacherId);

    @Select("select course.*, teacher.teacher_name as teacherName from t_course as course left join t_teacher as teacher on course.teacher_id = teacher.teacher_id where teacher.teacher_id = #{teacherId}")
    List<CourseVo> selectByTeacherId(Integer teacherId);

    @Select("select course.*, teacher.teacher_name as teacherName from t_course as course left join t_teacher as teacher on course.teacher_id = teacher.teacher_id where course.id = #{courseId}")
    CourseVo selectByCourseId(Integer courseId);

    @Select("select course.*, teacher.teacher_name as teacherName from t_course as course left join t_teacher as teacher on course.teacher_id = teacher.teacher_id")
    List<CourseVo> selectAll();
}
