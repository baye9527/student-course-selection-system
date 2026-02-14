package com.example.mapper;

import com.example.entity.Score;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface ScoreMapper {

    /**
     * 根据学生ID查询成绩（关联学生和课程信息）
     */
    @Select("select score.*, student.name as studentName, student.code as studentCode, " +
            "course.name as courseName, course.no as courseNo " +
            "from score " +
            "left join student on score.student_id = student.id " +
            "left join course on score.course_id = course.id " +
            "where score.student_id = #{studentId}")
    List<Score> selectByStudentId(Integer studentId);

    /**
     * 条件查询成绩（用于分页查询）
     */
    List<Score> selectByCondition(Score score);
}
