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

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 公告类型(NORMAL/IMPORTANT/URGENT)
     */
    private String type;

    /**
     * 状态(0-隐藏 1-发布)
     */
    private Integer status;

    /**
     * 是否置顶(0-否 1-是)
     */
    private Integer top;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 发布者ID
     */
    private Long publisherId;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishTime;
}
