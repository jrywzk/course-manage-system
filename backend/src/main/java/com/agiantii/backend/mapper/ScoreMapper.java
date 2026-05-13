package com.agiantii.backend.mapper;

import com.agiantii.backend.pojo.Score;
import com.agiantii.backend.pojo.vo.ScoreVo;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
// 注意：本 Mapper 基于 V1 t_score 表结构，V2 已将 t_score 完全重构（enrollment_id 替代 course_id/student_id/teacher_id）。
// 已修复 JOIN 中的 t_teacher/t_course/t_student 列名引用，以及一处 SQL 拼接 bug。
public interface ScoreMapper {
    @Insert("insert into t_score (course_id, student_id,teacher_id,score) values (#{courseId},#{studentId},#{teacherId},#{score})")
    void insert(Score score);

    @Delete("delete from t_score where id = #{id}")
    void delete(Score score);
    @Delete("delete from t_score where student_id = #{studentId}")
    void deleteByStudentId(@Param("studentId") Integer studentId);
    @Delete("delete from t_score where teacher_id = #{teacherId}")
    void deleteByTeacherId(@Param("teacherId") Integer teacherId);
    @Delete("delete from t_score where course_id = #{courseId}")
    void deleteByCourseId(@Param("courseId") Integer courseId);
    @Delete("delete from t_score where course_id = #{courseId} and student_id = #{studentId} and teacher_id = #{teacherId}")
    void deleteByCourseIdAndStudentIdAndTeacherId(@Param("courseId") Integer courseId, @Param("studentId") Integer studentId, @Param("teacherId") Integer teacherId);


    @Update("update t_score set score = #{score} where course_id = #{courseId} and student_id = #{studentId}")
    void update(Score score);

    @Select("select score.*, course.course_name as courseName, student.student_name as studentName, teacher.teacher_name as teacherName " +
            "from t_score score " +
            "left join t_course course on score.course_id = course.course_id " +
            "left join t_student student on score.student_id = student.student_id " +
            "left join t_teacher teacher on score.teacher_id = teacher.teacher_id " +
            "where student.student_id = #{studentId}")
    List<ScoreVo> selectByStudentId(@Param("studentId") Integer studentId);

    @Select("select score.*, course.course_name as courseName, student.student_name as studentName, teacher.teacher_name as teacherName " +
            "from t_score score " +
            "left join t_course course on score.course_id = course.course_id " +
            "left join t_student student on score.student_id = student.student_id " +
            "left join t_teacher teacher on score.teacher_id = teacher.teacher_id " +
            "where student.student_name like concat('%',#{studentName},'%')")
    List<ScoreVo> selectByStudentName(String studentName);

    @Select("select score.*, course.course_name as courseName, student.student_name as studentName, teacher.teacher_name as teacherName " +
            "from t_score score " +
            "left join t_course course on score.course_id = course.course_id " +
            "left join t_student student on score.student_id = student.student_id " +
            "left join t_teacher teacher on score.teacher_id = teacher.teacher_id " +
            "where teacher.teacher_id = #{teacherId}")
    List<ScoreVo> selectByTeacherId(@Param("teacherId") Integer teacherId);

    @Select("select score.*, course.course_name as courseName, student.student_name as studentName, teacher.teacher_name as teacherName " +
            "from t_score score " +
            "left join t_course course on score.course_id = course.course_id " +
            "left join t_student student on score.student_id = student.student_id " +
            "left join t_teacher teacher on score.teacher_id = teacher.teacher_id " +
            "where teacher.teacher_id = #{teacherId} and course.course_id = #{courseId}")
    List<ScoreVo> selectByTeacherIdAndCourseId(@Param("teacherId")Integer teacherId, @Param("courseId") Integer courseId);

    @Select("select score.*, course.course_name as courseName, student.student_name as studentName, teacher.teacher_name as teacherName " +
            "from t_score score " +
            "left join t_course course on score.course_id = course.course_id " +
            "left join t_student student on score.student_id = student.student_id " +
            "left join t_teacher teacher on score.teacher_id = teacher.teacher_id " +
            "where course.course_name like concat('%',#{courseName},'%')")
    List<ScoreVo> selectByCourseName(String courseName);

    @Select("SELECT score.*, course.course_name AS courseName, student.student_name AS studentName, teacher.teacher_name AS teacherName\n" +
            "FROM t_score AS score\n" +
            "LEFT JOIN t_course AS course ON score.course_id = course.course_id\n" +
            "LEFT JOIN t_student AS student ON score.student_id = student.student_id\n" +
            "LEFT JOIN t_teacher AS teacher ON score.teacher_id = teacher.teacher_id\n" +
            "WHERE course.course_id = #{courseId}")
    List<ScoreVo> selectByCourseId(@Param("courseId") Integer courseId);


    @Select("select score.*, course.course_name as courseName, student.student_name as studentName, teacher.teacher_name as teacherName " +
            "from t_score score " +
            "left join t_course course on score.course_id = course.course_id " +
            "left join t_student student on score.student_id = student.student_id " +
            "left join t_teacher teacher on score.teacher_id = teacher.teacher_id " +
            "where course.course_id = #{courseId} and student.student_id = #{studentId}")
    ScoreVo selectByCourseIdAndStudentId(@Param("courseId") Integer courseId, @Param("studentId") Integer studentId);

    @Select("select score.*, course.course_name as courseName, student.student_name as studentName, teacher.teacher_name as teacherName " +
            "from t_score score " +
            "left join t_course course on score.course_id = course.course_id " +
            "left join t_student student on score.student_id = student.student_id " +
            "left join t_teacher teacher on score.teacher_id = teacher.teacher_id " +
            "where student.student_id = #{studentId} and course.term <= #{term}")
    List<ScoreVo> selectByStudentIdAndTerm(@Param("studentId") Integer studentId,@Param("term") String term);

    @Select("select count(*) from t_score where course_id = #{courseId} and teacher_id = #{teacherId}")
    Integer countByCourseIdAndTeacherId(@Param("courseId") Integer courseId, @Param("teacherId") Integer teacherId);

    @Select("select score.*, course.course_name as courseName, student.student_name as studentName, teacher.teacher_name as teacherName " +
            "from t_score score " +
            "left join t_course course on score.course_id = course.course_id " +
            "left join t_student student on score.student_id = student.student_id " +
            "left join t_teacher teacher on score.teacher_id = teacher.teacher_id")
    List<ScoreVo> selectAll();
}
