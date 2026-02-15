package com.example.controller;

import com.example.common.Result;
import com.example.entity.Score;
import com.example.entity.StudentScoreInfo;
import com.example.service.ScoreService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生成绩查询接口
 */
@RestController
@RequestMapping("/score")
public class ScoreController {

    @Resource
    private ScoreService scoreService;

    /**
     * 管理员/教师录入学生成绩
     */
    @PostMapping("/add")
    public Result add(@RequestBody Score score) {
        scoreService.addScore(score);
        return Result.success();
    }

    /**
     * 批量更新学生成绩（用于成绩管理页面）
     */
    @PostMapping("/batchUpdate")
    public Result batchUpdate(@RequestBody List<Score> scores) {
        scoreService.batchUpdateScores(scores);
        return Result.success();
    }

    /**
     * 学生查询自己的成绩（分页）- 返回完整信息
     */
    @GetMapping("/myScores")
    public Result myScores(@RequestParam Integer studentId,
                           @RequestParam(required = false) String semester,
                           @RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Score> pageInfo = scoreService.selectMyScores(studentId, semester, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 学生查询自己的成绩（分页）- 返回简化信息
     */
    @GetMapping("/myScoresSimple")
    public Result myScoresSimple(@RequestParam Integer studentId,
                              @RequestParam(required = false) String semester,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<StudentScoreInfo> pageInfo = scoreService.selectMyScoresSimple(studentId, semester, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 学生查询自己的成绩统计
     */
    @GetMapping("/myStatistics")
    public Result myStatistics(@RequestParam Integer studentId) {
        return Result.success(scoreService.getMyScoreStatistics(studentId));
    }
}
