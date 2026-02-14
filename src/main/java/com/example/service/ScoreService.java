package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.entity.Score;
import com.example.exception.CustomException;
import com.example.mapper.ScoreMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学生成绩业务层处理
 */
@Service
public class ScoreService {

    @Resource
    private ScoreMapper scoreMapper;

    /**
     * 学生查询自己的成绩（分页）
     */
    public PageInfo<Score> selectMyScores(Integer studentId, String semester, Integer pageNum, Integer pageSize) {
        if (ObjectUtil.isEmpty(studentId)) {
            throw new CustomException("学生ID不能为空");
        }
        
        PageHelper.startPage(pageNum, pageSize);
        
        // 构建查询条件
        Score score = new Score();
        score.setStudentId(studentId);
        if (ObjectUtil.isNotEmpty(semester)) {
            score.setSemester(semester);
        }
        
        List<Score> list = scoreMapper.selectByCondition(score);
        return PageInfo.of(list);
    }

    /**
     * 学生查询自己的成绩统计
     */
    public java.util.Map<String, Object> getMyScoreStatistics(Integer studentId) {
        if (ObjectUtil.isEmpty(studentId)) {
            throw new CustomException("学生ID不能为空");
        }
        
        List<Score> scores = scoreMapper.selectByStudentId(studentId);
        
        // 计算平均成绩
        double averageScore = scores.stream()
                .filter(s -> s.getScore() != null)
                .mapToDouble(Score::getScore)
                .sum();
        
        // 计算已修课程数
        long courseCount = scores.stream()
                .filter(s -> s.getScore() != null)
                .count();
        
        // 计算及格课程数
        long passCount = scores.stream()
                .filter(s -> s.getScore() != null && s.getScore() >= 60)
                .count();
        
        return java.util.Map.of(
            "averageScore", courseCount > 0 ? averageScore / courseCount : 0.0,
            "courseCount", courseCount,
            "passCount", passCount,
            "passRate", courseCount > 0 ? (double) passCount / courseCount * 100 : 0.0
        );
    }
}
