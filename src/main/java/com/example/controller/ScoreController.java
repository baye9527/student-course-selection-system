package com.example.controller;

import com.example.common.Result;
import com.example.entity.Score;
import com.example.service.ScoreService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 学生成绩查询接口
 */
@RestController
@RequestMapping("/score")
public class ScoreController {

    @Resource
    private ScoreService scoreService;

    /**
     * 学生查询自己的成绩（分页）
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
     * 学生查询自己的成绩统计
     */
    @GetMapping("/myStatistics")
    public Result myStatistics(@RequestParam Integer studentId) {
        java.util.Map<String, Object> statistics = scoreService.getMyScoreStatistics(studentId);
        return Result.success(statistics);
    }
}
