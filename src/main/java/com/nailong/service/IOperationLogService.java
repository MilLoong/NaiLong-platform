package com.nailong.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nailong.model.dto.log.LogQueryDTO;
import com.nailong.model.entity.OperationLog;
import com.nailong.model.vo.log.OperationLogVO;

import java.util.List;

/**
 * @brief 操作日志服务接口
 * @author Nailong
 */
public interface IOperationLogService {

    /**
     * @brief 添加操作日志
     * @param userId    用户 ID
     * @param username  用户名
     * @param operation 操作类型
     * @param method    请求方法
     * @param params    请求参数
     * @param result    返回结果
     * @param ip        IP 地址
     * @param location  IP 归属地
     * @param timeCost  耗时（ms）
     * @param status    状态（0-失败 1-成功）
     * @param errorMsg  错误信息
     */
    void addLog(Long userId, String username, String operation, String method,
                String params, String result, String ip, String location,
                Integer timeCost, Integer status, String errorMsg);

    /**
     * @brief 分页查询操作日志
     * @param dto 查询条件
     * @return 操作日志分页列表
     */
    Page<OperationLogVO> getLogList(LogQueryDTO dto);

    /**
     * @brief 根据用户 ID 查询操作日志
     * @param userId 用户 ID
     * @param limit  限制条数
     * @return 操作日志列表
     */
    List<OperationLogVO> getLogsByUserId(Long userId, Integer limit);

    /**
     * @brief 批量删除操作日志
     * @param ids 日志 ID 列表
     */
    void batchDeleteLogs(List<Long> ids);

    /**
     * @brief 清空操作日志
     * @details 删除全部操作日志记录
     */
    void clearAllLogs();
}
