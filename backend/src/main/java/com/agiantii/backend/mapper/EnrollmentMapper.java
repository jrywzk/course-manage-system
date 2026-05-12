package com.agiantii.backend.mapper;

import com.agiantii.backend.pojo.Enrollment;
import com.agiantii.backend.pojo.vo.EnrollmentVo;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface EnrollmentMapper {

    @Select("SELECT * FROM t_enrollment WHERE enrollment_id = #{enrollmentId}")
    Enrollment selectById(@Param("enrollmentId") Integer enrollmentId);

    @Select("SELECT * FROM t_enrollment WHERE student_id = #{studentId} AND section_id = #{sectionId}")
    Enrollment selectByStudentAndSection(@Param("studentId") Integer studentId, @Param("sectionId") Integer sectionId);

    @Insert("INSERT INTO t_enrollment (student_id, section_id, source, status, select_time) " +
            "VALUES (#{studentId}, #{sectionId}, #{source}, 1, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "enrollmentId", keyColumn = "enrollment_id")
    void insert(Enrollment enrollment);

    @Update("UPDATE t_enrollment SET status = 0 WHERE enrollment_id = #{enrollmentId} AND status = 1")
    int updateStatusToDropped(@Param("enrollmentId") Integer enrollmentId);

    @Update("UPDATE t_enrollment SET status = 1, select_time = NOW(), source = #{source} " +
            "WHERE student_id = #{studentId} AND section_id = #{sectionId} AND status = 0")
    int restoreEnrollment(@Param("studentId") Integer studentId, @Param("sectionId") Integer sectionId,
                          @Param("source") String source);

    @Select("<script>" +
            "SELECT e.enrollment_id AS enrollmentId, e.section_id AS sectionId, " +
            "cs.section_code AS sectionCode, cs.semester, " +
            "c.course_code AS courseCode, c.course_name AS courseName, c.credit, " +
            "t.teacher_name AS teacherName, cs.schedule_text AS scheduleText, " +
            "cr.building, cr.room_no AS roomNo, " +
            "DATE_FORMAT(e.select_time, '%Y-%m-%d %H:%i:%s') AS selectTime, e.status, " +
            "CASE WHEN sc.score_id IS NOT NULL THEN TRUE ELSE FALSE END AS hasScore, " +
            "sc.final_score AS finalScore, sc.gpa_point AS gpaPoint, sc.is_passed AS isPassed " +
            "FROM t_enrollment e " +
            "JOIN t_course_section cs ON e.section_id = cs.section_id " +
            "JOIN t_course c ON cs.course_id = c.course_id " +
            "JOIN t_teacher t ON cs.teacher_id = t.teacher_id " +
            "LEFT JOIN t_classroom cr ON cs.classroom_id = cr.classroom_id " +
            "LEFT JOIN t_score sc ON e.enrollment_id = sc.enrollment_id " +
            "WHERE e.student_id = #{studentId} " +
            "<if test='semester != null and semester != \"\"'>AND cs.semester = #{semester} </if>" +
            "<if test='status != null'>AND e.status = #{status} </if>" +
            "ORDER BY e.select_time DESC" +
            "</script>")
    List<EnrollmentVo> selectByStudentId(@Param("studentId") Integer studentId,
                                          @Param("semester") String semester,
                                          @Param("status") Integer status);

    @Select("SELECT e.enrollment_id AS enrollmentId, st.student_id AS studentId, " +
            "st.student_no AS studentNo, st.student_name AS studentName, st.gender, " +
            "m.major_name AS majorName, " +
            "DATE_FORMAT(e.select_time, '%Y-%m-%d %H:%i:%s') AS selectTime, " +
            "CASE WHEN sc.score_id IS NOT NULL THEN TRUE ELSE FALSE END AS hasScore, " +
            "sc.final_score AS finalScore, sc.is_passed AS isPassed " +
            "FROM t_enrollment e " +
            "JOIN t_student st ON e.student_id = st.student_id " +
            "LEFT JOIN t_major m ON st.major_id = m.major_id " +
            "LEFT JOIN t_score sc ON e.enrollment_id = sc.enrollment_id " +
            "WHERE e.section_id = #{sectionId} AND e.status = 1 " +
            "ORDER BY st.student_no")
    List<Map<String, Object>> selectSectionStudents(@Param("sectionId") Integer sectionId);
}
