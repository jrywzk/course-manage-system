package com.agiantii.backend.mapper;

import com.agiantii.backend.pojo.vo.CourseSectionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseSectionMapper {

    @Select("SELECT cs.section_id AS sectionId, cs.section_code AS sectionCode, cs.semester, " +
            "cs.course_id AS courseId, c.course_code AS courseCode, c.course_name AS courseName, " +
            "c.credit, c.total_hours AS totalHours, c.course_type AS courseType, " +
            "t.teacher_name AS teacherName, t.title, " +
            "cr.building, cr.room_no AS roomNo, " +
            "cs.schedule_text AS scheduleText, cs.capacity_limit AS capacityLimit, " +
            "cs.selected_count AS selectedCount, " +
            "(cs.capacity_limit - cs.selected_count) AS remaining, cs.status " +
            "FROM t_course_section cs " +
            "JOIN t_course c ON cs.course_id = c.course_id " +
            "JOIN t_teacher t ON cs.teacher_id = t.teacher_id " +
            "LEFT JOIN t_classroom cr ON cs.classroom_id = cr.classroom_id " +
            "WHERE cs.status = #{status} " +
            "ORDER BY cs.section_id")
    List<CourseSectionVo> selectAllWithDetails(@Param("status") Integer status);

    @Select("SELECT cs.section_id AS sectionId, cs.section_code AS sectionCode, cs.semester, " +
            "cs.course_id AS courseId, c.course_code AS courseCode, c.course_name AS courseName, " +
            "c.credit, c.total_hours AS totalHours, c.course_type AS courseType, c.description, " +
            "t.teacher_name AS teacherName, t.title, " +
            "cr.building, cr.room_no AS roomNo, " +
            "cs.schedule_text AS scheduleText, cs.capacity_limit AS capacityLimit, " +
            "cs.selected_count AS selectedCount, " +
            "(cs.capacity_limit - cs.selected_count) AS remaining, cs.status " +
            "FROM t_course_section cs " +
            "JOIN t_course c ON cs.course_id = c.course_id " +
            "JOIN t_teacher t ON cs.teacher_id = t.teacher_id " +
            "LEFT JOIN t_classroom cr ON cs.classroom_id = cr.classroom_id " +
            "WHERE cs.section_id = #{sectionId}")
    CourseSectionVo selectSectionDetail(@Param("sectionId") Integer sectionId);

    @Select("SELECT cs.section_id AS sectionId, cs.section_code AS sectionCode, cs.semester, " +
            "c.course_name AS courseName, c.credit, " +
            "cs.schedule_text AS scheduleText, " +
            "cr.building, cr.room_no AS roomNo, " +
            "cs.capacity_limit AS capacityLimit, cs.selected_count AS selectedCount, " +
            "(SELECT COUNT(*) FROM t_enrollment e WHERE e.section_id = cs.section_id AND e.status = 1) AS enrolledCount " +
            "FROM t_course_section cs " +
            "JOIN t_course c ON cs.course_id = c.course_id " +
            "LEFT JOIN t_classroom cr ON cs.classroom_id = cr.classroom_id " +
            "WHERE cs.teacher_id = #{teacherId} AND cs.status = 1 " +
            "ORDER BY cs.semester DESC, cs.section_id")
    List<CourseSectionVo> selectByTeacherId(@Param("teacherId") Integer teacherId);
}
