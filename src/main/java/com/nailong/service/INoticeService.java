package com.nailong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nailong.model.dto.notice.NoticeCreateDTO;
import com.nailong.model.dto.notice.NoticeUpdateDTO;
import com.nailong.model.entity.Notice;
import com.nailong.model.vo.notice.NoticeVO;

import java.util.List;

/**
 * @brief 公告服务接口
 * @author Nailong
 */
public interface INoticeService extends IService<Notice> {

    /**
     * @brief 创建公告
     * @param dto 公告创建请求参数
     * @return 公告 ID
     */
    Long createNotice(NoticeCreateDTO dto);

    /**
     * @brief 更新公告
     * @param dto 公告更新请求参数
     */
    void updateNotice(NoticeUpdateDTO dto);

    /**
     * @brief 删除公告
     * @param id 公告 ID
     */
    void deleteNotice(Long id);

    /**
     * @brief 发布公告
     * @param id 公告 ID
     */
    void publishNotice(Long id);

    /**
     * @brief 获取公告详情
     * @param id 公告 ID
     * @return 公告详情
     */
    NoticeVO getNoticeDetail(Long id);

    /**
     * @brief 获取公告列表（已发布）
     * @return 已发布公告列表
     */
    List<NoticeVO> getPublishedNoticeList();

    /**
     * @brief 获取所有公告列表（管理后台）
     * @return 全部公告列表（含草稿）
     */
    List<NoticeVO> getAllNoticeList();

    /**
     * @brief 增加浏览次数
     * @param id 公告 ID
     */
    void incrementViewCount(Long id);
}
