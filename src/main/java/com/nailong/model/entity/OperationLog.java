package com.nailong.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @brief 操作日志实体类
 * @author Nailong
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("operation_log")
public class OperationLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long userId;  ///< 操作用户ID
    private String username;  ///< 用户名
    private String operation;  ///< 操作类型
    private String method;  ///< 请求方法
    private String params;  ///< 请求参数
    private String result;  ///< 返回结果
    private String ip;  ///< 操作IP
    private String location;  ///< IP归属地
    private Integer timeCost;  ///< 耗时(ms)
    private Integer status;  ///< 状态(0-失败 1-成功)
    private String errorMsg;  ///< 错误信息
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(updateStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NEVER)
    private LocalDateTime createTime;  ///< 操作时间
}
