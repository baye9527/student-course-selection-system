package com.example.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel导入结果
 */
public class ImportResult {

    /**
     * 成功条数
     */
    private Integer successCount;

    /**
     * 失败条数
     */
    private Integer failCount;

    /**
     * 失败原因列表
     */
    private List<String> failReasons;

    public ImportResult() {
        this.successCount = 0;
        this.failCount = 0;
        this.failReasons = new ArrayList<>();
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    public List<String> getFailReasons() {
        return failReasons;
    }

    public void setFailReasons(List<String> failReasons) {
        this.failReasons = failReasons;
    }

    public void addSuccess() {
        this.successCount++;
    }

    public void addFail(String reason) {
        this.failCount++;
        this.failReasons.add(reason);
    }
}
