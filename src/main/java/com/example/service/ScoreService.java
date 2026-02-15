package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.entity.Choice;
import com.example.entity.Score;
import com.example.exception.CustomException;
import com.example.mapper.ChoiceMapper;
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
    
    @Resource
    private ChoiceMapper choiceMapper;

    /**
     * 学生查询自己的成绩（分页）
     */
    public PageInfo<Score> selectMyScores(Integer studentId, String semester, Integer pageNum, Integer pageSize) {
        if (ObjectUtil.isEmpty(studentId)) {
            throw new CustomException("学生ID不能为空");
        }
        
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
     * 获取学生成绩统计信息
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

    /**
     * 管理员/教师录入学生成绩
     */
    public void addScore(Score score) {
        // 验证必填字段
        if (ObjectUtil.isEmpty(score.getStudentId())) {
            throw new CustomException("学生ID不能为空");
        }
        if (ObjectUtil.isEmpty(score.getCourseId())) {
            throw new CustomException("课程ID不能为空");
        }
        if (ObjectUtil.isEmpty(score.getChoiceId())) {
            throw new CustomException("选课ID不能为空");
        }
        if (ObjectUtil.isEmpty(score.getSemester())) {
            throw new CustomException("学期不能为空");
        }
        
        // 检查选课ID是否已存在成绩
        Score existingScore = scoreMapper.selectByChoiceId(score.getChoiceId());
        if (ObjectUtil.isNotEmpty(existingScore)) {
            throw new CustomException("该选课记录已存在成绩，请使用修改功能");
        }
        
        // 验证成绩范围
        if (score.getUsualScore() != null && (score.getUsualScore() < 0 || score.getUsualScore() > 100)) {
            throw new CustomException("平时成绩必须在0-100之间");
        }
        if (score.getExamScore() != null && (score.getExamScore() < 0 || score.getExamScore() > 100)) {
            throw new CustomException("考试成绩必须在0-100之间");
        }
        
        // 自动计算总成绩：平时成绩40% + 考试成绩60%
        if (score.getUsualScore() != null && score.getExamScore() != null) {
            double totalScore = score.getUsualScore() * 0.4 + score.getExamScore() * 0.6;
            // 四舍五入保留1位小数
            score.setScore(Math.round(totalScore * 10.0) / 10.0);
        } else if (score.getUsualScore() != null) {
            // 只有平时成绩，按100%计算
            score.setScore(score.getUsualScore());
        } else if (score.getExamScore() != null) {
            // 只有考试成绩，按100%计算
            score.setScore(score.getExamScore());
        } else {
            // 都没有，总成绩为null
            score.setScore(null);
        }
        
        scoreMapper.insert(score);
    }

    /**
     * 批量更新学生成绩（用于成绩管理页面）
     */
    public void batchUpdateScores(List<Score> scores) {
        if (ObjectUtil.isEmpty(scores)) {
            throw new CustomException("成绩数据不能为空");
        }
        
        for (Score score : scores) {
            // 验证必填字段
            if (ObjectUtil.isEmpty(score.getChoiceId())) {
                throw new CustomException("选课ID不能为空");
            }
            if (ObjectUtil.isEmpty(score.getSemester())) {
                throw new CustomException("学期不能为空");
            }
            
            // 验证成绩范围
            if (score.getUsualScore() != null && (score.getUsualScore() < 0 || score.getUsualScore() > 100)) {
                throw new CustomException("平时成绩必须在0-100之间");
            }
            if (score.getExamScore() != null && (score.getExamScore() < 0 || score.getExamScore() > 100)) {
                throw new CustomException("考试成绩必须在0-100之间");
            }
            
            // 检查是否已存在成绩记录
            Score existingScore = scoreMapper.selectByChoiceId(score.getChoiceId());
            
            if (ObjectUtil.isNotEmpty(existingScore)) {
                // 更新现有成绩
                score.setId(existingScore.getId());
                
                // 自动计算总成绩：平时成绩40% + 考试成绩60%
                if (score.getUsualScore() != null && score.getExamScore() != null) {
                    double totalScore = score.getUsualScore() * 0.4 + score.getExamScore() * 0.6;
                    // 四舍五入保留1位小数
                    score.setScore(Math.round(totalScore * 10.0) / 10.0);
                } else if (score.getUsualScore() != null) {
                    // 只有平时成绩，按100%计算
                    score.setScore(score.getUsualScore());
                } else if (score.getExamScore() != null) {
                    // 只有考试成绩，按100%计算
                    score.setScore(score.getExamScore());
                } else {
                    // 都没有，总成绩为null
                    score.setScore(null);
                }
                
                scoreMapper.updateById(score);
            } else {
                // 插入新成绩记录
                // 从选课记录中获取学生ID和课程ID
                System.out.println("正在查询选课记录，choiceId: " + score.getChoiceId());
                Choice choice = choiceMapper.selectById(score.getChoiceId());
                System.out.println("查询到的选课记录: " + choice);
                
                if (ObjectUtil.isEmpty(choice)) {
                    throw new CustomException("选课记录不存在，choiceId: " + score.getChoiceId());
                }
                score.setStudentId(choice.getStudentId());
                score.setCourseId(choice.getCourseId());
                
                // 自动计算总成绩：平时成绩40% + 考试成绩60%
                if (score.getUsualScore() != null && score.getExamScore() != null) {
                    double totalScore = score.getUsualScore() * 0.4 + score.getExamScore() * 0.6;
                    // 四舍五入保留1位小数
                    score.setScore(Math.round(totalScore * 10.0) / 10.0);
                } else if (score.getUsualScore() != null) {
                    // 只有平时成绩，按100%计算
                    score.setScore(score.getUsualScore());
                } else if (score.getExamScore() != null) {
                    // 只有考试成绩，按100%计算
                    score.setScore(score.getExamScore());
                } else {
                    // 都没有，总成绩为null
                    score.setScore(null);
                }
                
                scoreMapper.insert(score);
            }
        }
    }
}
