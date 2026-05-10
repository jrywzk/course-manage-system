package com.agiantii.backend.mapper;
import com.agiantii.backend.pojo.Score;
import com.agiantii.backend.pojo.vo.ScoreVo;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.*;
import java.util.List;
@Mapper
public interface ScoreMapper {
    // ========== 插入 ==========
    @Insert("insert into t_score (course_id, student_id,teacher_id,score) values (#{courseId},#{studentId},#{teacherId},#{score})")
    void insert(Score score);
    //  新增：成绩录入（关联 enrollment_id + 分项成绩）
    // @Insert("insert into t_score (enrollment_id, course_id, student_id, teacher_id, " +
    //         "daily_score, exam_score, total_score, score, gpa, is_pass) " +
    //         "values (#{enrollmentId}, #{courseId}, #{studentId}, #{teacherId}, " +
    //         "#{dailyScore}, #{examScore}, #{totalScore}, #{score}, #{gpa}, #{isPass})")
    // @Options(useGeneratedKeys = true, keyProperty = "id")
    // void insert(Score score);
    // ========== 删除 ==========
    @Delete("delete from t_score where id = #{id}")
    void delete(Score score);
    @Delete("delete from t_score where student_id = #{studentId}")
    void deleteByStudentId(@Param("studentId") Integer studentId);
    @Delete("delete from t_score where teacher_id = #{teacherId}")
    void deleteByTeacherId(@Param("teacherId") Integer teacherId);
    @Delete("delete from t_score where course_id = #{courseId}")
    void deleteByCourseId(@Param("courseId") Integer courseId);
    // 旧方法保留，过渡后废弃
    // @Deprecated
    @Delete("delete from t_score where course_id = #{courseId} and student_id = #{studentId} and teacher_id = #{teacherId}")
    void deleteByCourseIdAndStudentIdAndTeacherId(@Param("courseId") Integer courseId,
                                                   @Param("studentId") Integer studentId,
                                                   @Param("teacherId") Integer teacherId);
    // ========== 更新 ==========
    @Update("update t_score set score = #{score} where course_id = #{courseId} and student_id = #{studentId}")
    void update(Score score);
    // 新增：按 ID 更新（含分项成绩）
    // @Update("update t_score set daily_score = #{dailyScore}, exam_score = #{examScore}, " +
    //         "total_score = #{totalScore}, score = #{score}, gpa = #{gpa}, is_pass = #{isPass} " +
    //         "where id = #{id}")
    // void update(Score score);
    //  新增：按 enrollment_id 更新
    // @Update("update t_score set daily_score = #{dailyScore}, exam_score = #{examScore}, " +
    //         "total_score = #{totalScore}, score = #{score}, gpa = #{gpa}, is_pass = #{isPass} " +
    //         "where enrollment_id = #{enrollmentId}")
    // void updateByEnrollmentId(Score score);
    // ========== 查询（原：直接 JOIN course/student/teacher） ==========
    @Select("select score.*,course.name as courseName,student.name as studentName,teacher.name as teacherName from t_score score left join t_course course on score.course_id = course.id left join t_student student on score.student_id = student.id left join t_teacher teacher on score.teacher_id = teacher.id where student.id = #{studentId}")
    List<ScoreVo> selectByStudentId(@Param("studentId") Integer studentId);
    //  新增：学生查成绩（走 enrollment 链，含 section_code + semester）
    // @Select("select s.*, " +
    //         "c.name as courseName, st.name as studentName, t.name as teacherName, " +
    //         "cs.section_code as sectionCode, cs.semester as semester " +
    //         "from t_score s " +
    //         "left join t_enrollment e on s.enrollment_id = e.id " +
    //         "left join t_course_section cs on e.section_id = cs.id " +
    //         "left join t_course c on cs.course_id = c.id " +
    //         "left join t_student st on e.student_id = st.id " +
    //         "left join t_teacher t on cs.teacher_id = t.id " +
    //         "where e.student_id = #{studentId}")
    // List<ScoreVo> selectByStudentId(@Param("studentId") Integer studentId);
    @Select("select score.*,course.name as courseName,student.name as studentName,teacher.name as teacherName from t_score score left join t_course course on score.course_id = course.id left join t_student student on score.student_id = student.id left join t_teacher teacher on score.teacher_id = teacher.id where student.name like concat('%',#{studentName},'%')")
    List<ScoreVo> selectByStudentName(String studentName);
    @Select("select score.*,course.name as courseName,student.name as studentName,teacher.name as teacherName from t_score score left join t_course course on score.course_id = course.id left join t_student student on score.student_id = student.id left join t_teacher teacher on score.teacher_id = teacher.id where teacher.id = #{teacherId}")
    List<ScoreVo> selectByTeacherId(@Param("teacherId") Integer teacherId);
    //  新增：教师查成绩（走 enrollment 链）
    // @Select("select s.*, " +
    //         "c.name as courseName, st.name as studentName, t.name as teacherName, " +
    //         "cs.section_code as sectionCode, cs.semester as semester " +
    //         "from t_score s " +
    //         "left join t_enrollment e on s.enrollment_id = e.id " +
    //         "left join t_course_section cs on e.section_id = cs.id " +
    //         "left join t_course c on cs.course_id = c.id " +
    //         "left join t_student st on e.student_id = st.id " +
    //         "left join t_teacher t on cs.teacher_id = t.id " +
    //         "where cs.teacher_id = #{teacherId}")
    // List<ScoreVo> selectByTeacherId(@Param("teacherId") Integer teacherId);
    @Select("select score.*,course.name as courseName,student.name as studentName,teacher.name as teacherName from t_score score left join t_course course on score.course_id = course.id left join t_student student on score.student_id = student.id left join t_teacher teacher on score.teacher_id = teacher.id where teacher.id = #{teacherId} and course.id = #{courseId}")
    List<ScoreVo> selectByTeacherIdAndCourseId(@Param("teacherId")Integer teacherId, @Param("courseId") Integer courseId);
    //  新增：教师按课程查成绩（走 enrollment 链）
    // @Select("select s.*, " +
    //         "c.name as courseName, st.name as studentName, t.name as teacherName, " +
    //         "cs.section_code as sectionCode, cs.semester as semester " +
    //         "from t_score s " +
    //         "left join t_enrollment e on s.enrollment_id = e.id " +
    //         "left join t_course_section cs on e.section_id = cs.id " +
    //         "left join t_course c on cs.course_id = c.id " +
    //         "left join t_student st on e.student_id = st.id " +
    //         "left join t_teacher t on cs.teacher_id = t.id " +
    //         "where cs.teacher_id = #{teacherId} and c.id = #{courseId}")
    // List<ScoreVo> selectByTeacherIdAndCourseId(@Param("teacherId") Integer teacherId,
    //                                              @Param("courseId") Integer courseId);
    @Select("select score.*,course.name as courseName,student.name as studentName,teacher.name as teacherName from t_score score left join t_course course on score.course_id = course.id left join t_student student on score.student_id = student.id left join t_teacher teacher on score.teacher_id = teacher.id where course.name like concat('%',#{courseName},'%')")
    List<ScoreVo> selectByCourseName(String courseName);
    @Select("SELECT score.*, course.name AS courseName, student.name AS studentName, teacher.name AS teacherName\n" +
            "FROM t_score AS score\n" +
            "LEFT JOIN t_course AS course ON score.course_id = course.id\n" +
            "LEFT JOIN t_student AS student ON score.student_id = student.id\n" +
            "LEFT JOIN t_teacher AS teacher ON score.teacher_id = teacher.id\n" +
            "WHERE course.id = #{courseId}")
    List<ScoreVo> selectByCourseId(@Param("courseId") Integer courseId);
    //  新增：按课程查成绩（走 enrollment 链）
    // @Select("select s.*, " +
    //         "c.name as courseName, st.name as studentName, t.name as teacherName, " +
    //         "cs.section_code as sectionCode, cs.semester as semester " +
    //         "from t_score s " +
    //         "left join t_enrollment e on s.enrollment_id = e.id " +
    //         "left join t_course_section cs on e.section_id = cs.id " +
    //         "left join t_course c on cs.course_id = c.id " +
    //         "left join t_student st on e.student_id = st.id " +
    //         "left join t_teacher t on cs.teacher_id = t.id " +
    //         "where c.id = #{courseId}")
    // List<ScoreVo> selectByCourseId(@Param("courseId") Integer courseId);
    @Select("select score.*,course.name as courseName,student.name as studentName,teacher.name as teacherName from t_score score left join t_course course on score.course_id = course.id left join t_student student on score.student_id = student.id left join t_teacher teacher on score.teacher_id = teacher.id where course.id = #{courseId} and student.id = #{studentId}")
    ScoreVo selectByCourseIdAndStudentId(@Param("courseId") Integer courseId, @Param("studentId") Integer studentId);
    //  旧方法保留，过渡后废弃
    // @Deprecated
    // ScoreVo selectByCourseIdAndStudentId(...);
    //  新增：按教学班查成绩
    // @Select("select s.*, " +
    //         "c.name as courseName, st.name as studentName, t.name as teacherName, " +
    //         "cs.section_code as sectionCode, cs.semester as semester " +
    //         "from t_score s " +
    //         "left join t_enrollment e on s.enrollment_id = e.id " +
    //         "left join t_course_section cs on e.section_id = cs.id " +
    //         "left join t_course c on cs.course_id = c.id " +
    //         "left join t_student st on e.student_id = st.id " +
    //         "left join t_teacher t on cs.teacher_id = t.id " +
    //         "where e.section_id = #{sectionId}")
    // List<ScoreVo> selectBySectionId(@Param("sectionId") Integer sectionId);
    //  新增：按 enrollment_id 查单条成绩
    // @Select("select * from t_score where enrollment_id = #{enrollmentId}")
    // Score selectByEnrollmentId(@Param("enrollmentId") Integer enrollmentId);
    @Select("select score.*,course.name as courseName,student.name as studentName,teacher.name as teacherName from t_score score left join t_course course on score.course_id = course.id left join t_student student on score.student_id = student.id left join t_teacher teacher on score.teacher_id = teacher.id where  student.id = #{studentId} and course.term <= #{term}")
    List<ScoreVo> selectByStudentIdAndTerm(@Param("studentId") Integer studentId, @Param("term") String term);
    //  新增：学生按学期查成绩（走 enrollment 链）
    // @Select("select s.*, " +
    //         "c.name as courseName, st.name as studentName, t.name as teacherName, " +
    //         "cs.section_code as sectionCode, cs.semester as semester " +
    //         "from t_score s " +
    //         "left join t_enrollment e on s.enrollment_id = e.id " +
    //         "left join t_course_section cs on e.section_id = cs.id " +
    //         "left join t_course c on cs.course_id = c.id " +
    //         "left join t_student st on e.student_id = st.id " +
    //         "left join t_teacher t on cs.teacher_id = t.id " +
    //         "where e.student_id = #{studentId} and cs.semester <= #{term}")
    // List<ScoreVo> selectByStudentIdAndTerm(@Param("studentId") Integer studentId,
    //                                          @Param("term") String term);
    @Select("select count(*) from t_score where course_id = #{courseId}  and teacher_id = #{teacherId}")
    Integer countByCourseIdAndTeacherId(@Param("courseId") Integer courseId, @Param("teacherId") Integer teacherId);
    //  旧方法保留，过渡后废弃
    // @Deprecated
    // Integer countByCourseIdAndTeacherId(...);
    @Select("select score.*,course.name as courseName,student.name as studentName,teacher.name as teacherName from t_score score left join t_course course on score.course_id = course.id left join t_student student on score.student_id = student.id left join t_teacher teacher on score.teacher_id = teacher.id")
    List<ScoreVo> selectAll();
    //  新增：全查（走 enrollment 链，含 section_code + semester）
    // @Select("select s.*, " +
    //         "c.name as courseName, st.name as studentName, t.name as teacherName, " +
    //         "cs.section_code as sectionCode, cs.semester as semester " +
    //         "from t_score s " +
    //         "left join t_enrollment e on s.enrollment_id = e.id " +
    //         "left join t_course_section cs on e.section_id = cs.id " +
    //         "left join t_course c on cs.course_id = c.id " +
    //         "left join t_student st on e.student_id = st.id " +
    //         "left join t_teacher t on cs.teacher_id = t.id")
    // List<ScoreVo> selectAll();
}