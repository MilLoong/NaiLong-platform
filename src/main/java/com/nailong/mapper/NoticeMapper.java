package com.nailong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nailong.model.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @brief 公告Mapper接口
 * @author Nailong
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * @brief 查询已发布的公告列表
     * @return 已发布公告列表
     */
    List<Notice> selectPublishedList();

    /**
     * @brief 查询所有公告列表（含未发布）
     * @return 公告列表
     */
    List<Notice> selectAllList();

    /**
     * @brief 增加公告浏览次数
     * @param id 公告ID
     * @return 影响的行数
     */
    int incrementViewCount(@Param("id") Long id);

    /**
     * @brief 发布公告
     * @param id 公告ID
     * @return 影响的行数
     */
    int publishNotice(@Param("id") Long id);
}
