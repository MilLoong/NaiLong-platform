package com.nailong.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nailong.common.annotation.Log; 
import com.nailong.common.result.Result;
import com.nailong.model.dto.log.LogQueryDTO;
import com.nailong.model.vo.log.OperationLogVO;
import com.nailong.service.IOperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * @brief 操作日志控制器
 * @details 操作日志管理接口
 * @author Nailong
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/log")
@RequiredArgsConstructor
@Tag(name = "操作日志", description = "操作日志管理接口")
public class OperationLogController {

    private final IOperationLogService operationLogService;

    /**
     * @brief 分页查询操作日志
     * @param dto 查询条件
     * @return 分页操作日志
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    @Operation(summary = "分页查询操作日志", description = "分页查询系统操作日志")
    public Result<Page<OperationLogVO>> getLogList(@Valid LogQueryDTO dto) {
        Page<OperationLogVO> page = operationLogService.getLogList(dto);
        return Result.success("查询成功", page);
    }

    /**
     * @brief 根据用户ID查询操作日志
     * @param userId 用户ID
     * @param limit  返回条数上限
     * @return 操作日志列表
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户ID查询操作日志", description = "查询指定用户的操作日志（管理员）")
    public Result<List<OperationLogVO>> getLogsByUserId(@PathVariable Long userId,
                                                     @RequestParam(defaultValue = "20") Integer limit) {
        List<OperationLogVO> logList = operationLogService.getLogsByUserId(userId, limit);
        return Result.success("查询成功", logList);
    }

    /**
     * @brief 批量删除操作日志
     * @param ids 日志ID列表
     * @return 操作结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除操作日志", description = "批量删除系统操作日志")
    public Result<Void> batchDeleteLogs(@RequestBody List<Long> ids) {
        operationLogService.batchDeleteLogs(ids);
        return Result.success("删除成功", null);
    }

    /**
     * @brief 清空操作日志
     * @return 操作结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/clear")
    @Operation(summary = "清空操作日志", description = "清空所有系统操作日志")
    @Log(operation = "清空操作日志", remark = "管理员执行")
    public Result<Void> clearAllLogs() {
        operationLogService.clearAllLogs();
        return Result.success("清空成功", null);
    }
}
