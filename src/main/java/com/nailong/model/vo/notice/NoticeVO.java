package com.nailong.model.vo.notice;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @brief 公告视图对象
 * @author Nailong
 */
@Data
@Schema(description = "公告视图对象")
public class NoticeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "公告ID")
    private Long id;  ///< 公告ID


    @Schema(description = "公告标题")
    private String title;  ///< 公告标题


    @Schema(description = "公告内容")
    private String content;  ///< 公告内容


    @Schema(description = "公告类型")
    private String type;  ///< 公告类型


    @Schema(description = "状态(0-隐藏 1-发布)")
    private Integer status;

    @Schema(description = "是否置顶(0-否 1-是)")
    private Integer top;

    @Schema(description = "浏览次数")
    private Integer viewCount;  ///< 浏览次数


    @Schema(description = "发布者ID")
    private Long publisherId;  ///< 发布者ID


    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishTime;  ///< 发布时间


    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;  ///< 创建时间

}
