package com.nailong.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @brief 公告实体类
 * @author Nailong
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notice")
public class Notice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String title;  ///< 公告标题
    private String content;  ///< 公告内容
    private String type;  ///< 公告类型(NORMAL/IMPORTANT/URGENT)
    private Integer status;  ///< 状态(0-隐藏 1-发布)
    private Integer top;  ///< 是否置顶(0-否 1-是)
    private Integer viewCount;  ///< 浏览次数
    private Long publisherId;  ///< 发布者ID
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishTime;  ///< 发布时间
}
