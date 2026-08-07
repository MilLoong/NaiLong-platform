package com.nailong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nailong.model.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @brief 操作日志Mapper接口
 * @author Nailong
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    /**
     * @brief 分页查询操作日志
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param operation 操作类型
     * @param username  用户名
     * @param current   当前页
     * @param size      每页大小
     * @return 操作日志列表
     */
    List<OperationLog> selectLogList(@Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime,
                                     @Param("operation") String operation,
                                     @Param("username") String username,
                                     @Param("current") Long current,
                                     @Param("size") Long size);

    /**
     * @brief 统计操作日志总数
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param operation 操作类型
     * @param username  用户名
     * @return 符合条件的日志总数
     */
    Long countLogList(@Param("startTime") LocalDateTime startTime,
                     @Param("endTime") LocalDateTime endTime,
                     @Param("operation") String operation,
                     @Param("username") String username);

    /**
     * @brief 根据用户ID查询操作日志
     * @param userId 用户ID
     * @param limit  限制条数
     * @return 操作日志列表
     */
    List<OperationLog> selectByUserId(@Param("userId") Long userId,
                                     @Param("limit") Integer limit);
}
