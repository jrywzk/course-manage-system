package com.agiantii.backend.mapper;

import com.agiantii.backend.pojo.ScoreV2;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface V2ScoreMapper {

    @Results({
            @Result(column = "score_id", property = "scoreId"),
            @Result(column = "enrollment_id", property = "enrollmentId"),
            @Result(column = "usual_score", property = "usualScore"),
            @Result(column = "exam_score", property = "examScore"),
            @Result(column = "final_score", property = "finalScore"),
            @Result(column = "gpa_point", property = "gpaPoint"),
            @Result(column = "is_passed", property = "isPassed"),
            @Result(column = "graded_at", property = "gradedAt"),
            @Result(column = "graded_by", property = "gradedBy")
    })
    @Select("SELECT * FROM t_score WHERE enrollment_id = #{enrollmentId}")
    ScoreV2 selectByEnrollmentId(@Param("enrollmentId") Integer enrollmentId);

    @Insert("INSERT INTO t_score (enrollment_id, usual_score, exam_score, final_score, gpa_point, is_passed, graded_at, graded_by) " +
            "VALUES (#{enrollmentId}, #{usualScore}, #{examScore}, #{finalScore}, #{gpaPoint}, #{isPassed}, NOW(), #{gradedBy})")
    @Options(useGeneratedKeys = true, keyProperty = "scoreId", keyColumn = "score_id")
    void insert(ScoreV2 score);

    @Update("UPDATE t_score SET usual_score = #{usualScore}, exam_score = #{examScore}, " +
            "final_score = #{finalScore}, gpa_point = #{gpaPoint}, is_passed = #{isPassed}, " +
            "graded_at = NOW(), graded_by = #{gradedBy} WHERE enrollment_id = #{enrollmentId}")
    void updateByEnrollmentId(ScoreV2 score);

    @Select("SELECT s.score_id AS scoreId, s.enrollment_id AS enrollmentId, st.student_no AS studentNo, st.student_name AS studentName, " +
            "s.usual_score AS usualScore, s.exam_score AS examScore, " +
            "s.final_score AS finalScore, s.is_passed AS isPassed, " +
            "DATE_FORMAT(s.graded_at, '%Y-%m-%d %H:%i:%s') AS gradedAt " +
            "FROM t_enrollment e " +
            "JOIN t_student st ON e.student_id = st.student_id " +
            "LEFT JOIN t_score s ON e.enrollment_id = s.enrollment_id " +
            "WHERE e.section_id = #{sectionId} AND e.status = 1 " +
            "ORDER BY st.student_no")
    List<Map<String, Object>> selectSectionScores(@Param("sectionId") Integer sectionId);
}
