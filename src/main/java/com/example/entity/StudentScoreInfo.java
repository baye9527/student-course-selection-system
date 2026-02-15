package com.example.entity;

/**
 * 学生成绩信息DTO（用于学生端成绩查询）
 */
public class StudentScoreInfo {

    private String courseName;    // 课程名称
    private Double usualScore;    // 平时成绩
    private Double examScore;     // 考试成绩
    private Double score;         // 总成绩
    private String semester;      // 学期

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Double getUsualScore() {
        return usualScore;
    }

    public void setUsualScore(Double usualScore) {
        this.usualScore = usualScore;
    }

    public Double getExamScore() {
        return examScore;
    }

    public void setExamScore(Double examScore) {
        this.examScore = examScore;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
