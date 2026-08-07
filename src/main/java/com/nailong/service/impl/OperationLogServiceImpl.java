package com.nailong.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nailong.mapper.OperationLogMapper;
import com.nailong.model.dto.log.LogQueryDTO;
import com.nailong.model.entity.OperationLog;
import com.nailong.model.vo.log.OperationLogVO;
import com.nailong.service.IOperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @brief 操作日志服务实现
 * @details 记录、查询与清理系统操作日志
 * @author Nailong
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements IOperationLogService {

    private final OperationLogMapper operationLogMapper;

    /**
     * @brief 添加操作日志
     * @param userId    操作用户 ID
     * @param username  操作用户名
     * @param operation 操作描述
     * @param method    请求方法
     * @param params    请求参数
     * @param result    操作结果
     * @param ip        客户端 IP
     * @param location  IP 归属地
     * @param timeCost  耗时（毫秒）
     * @param status    状态（1 成功，0 失败）
     * @param errorMsg  错误信息
     * @details 写入失败不影响主业务流程
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addLog(Long userId, String username, String operation, String method,
                      String params, String result, String ip, String location,
                      Integer timeCost, Integer status, String errorMsg) {
        try {
            OperationLog log = new OperationLog();
            log.setUserId(userId);
            log.setUsername(username);
            log.setOperation(operation);
            log.setMethod(method);
            log.setParams(params);
            log.setResult(result);
            log.setIp(ip);
            log.setLocation(location);
            log.setTimeCost(timeCost);
            log.setStatus(status);
            log.setErrorMsg(errorMsg);
            log.setCreateTime(LocalDateTime.now());

            operationLogMapper.insert(log);
        } catch (Exception e) {
            log.error("添加操作日志失败: {}", e.getMessage());
        }
    }

    /**
     * @brief 分页查询操作日志
     * @param dto 查询条件（时间范围、操作类型、用户名等）
     * @return 分页日志列表
     */
    @Override
    public Page<OperationLogVO> getLogList(LogQueryDTO dto) {
        Page<OperationLog> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        List<OperationLog> logList = operationLogMapper.selectLogList(
                dto.getStartTime(),
                dto.getEndTime(),
                dto.getOperation(),
                dto.getUsername(),
                (page.getCurrent() - 1) * page.getSize(),
                page.getSize()
        );

        Long total = operationLogMapper.countLogList(
                dto.getStartTime(),
                dto.getEndTime(),
                dto.getOperation(),
                dto.getUsername()
        );

        page.setRecords(logList);
        page.setTotal(total);

        List<OperationLogVO> voList = logList.stream()
                .map(log -> BeanUtil.copyProperties(log, OperationLogVO.class))
                .collect(Collectors.toList());

        Page<OperationLogVO> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(total);
        voPage.setCurrent(page.getCurrent());
        voPage.setSize(page.getSize());
        voPage.setPages(page.getPages());

        return voPage;
    }

    /**
     * @brief 获取指定用户的最近操作日志
     * @param userId 用户 ID
     * @param limit  返回条数上限
     * @return 操作日志列表
     */
    @Override
    public List<OperationLogVO> getLogsByUserId(Long userId, Integer limit) {
        if (userId == null || limit == null || limit <= 0) {
            return new ArrayList<>();
        }

        List<OperationLog> logList = operationLogMapper.selectByUserId(userId, limit);
        if (CollectionUtils.isEmpty(logList)) {
            return new ArrayList<>();
        }

        return logList.stream()
                .map(log -> BeanUtil.copyProperties(log, OperationLogVO.class))
                .collect(Collectors.toList());
    }

    /**
     * @brief 批量删除操作日志
     * @param ids 日志 ID 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteLogs(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        operationLogMapper.deleteBatchIds(ids);
        log.info("批量删除操作日志成功，共删除{}条记录", ids.size());
    }

    /**
     * @brief 清空所有操作日志
     * @details 生产环境需谨慎使用，通常应保留一定时间的日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearAllLogs() {
        operationLogMapper.delete(null);
        log.info("清空所有操作日志成功");
    }
}
