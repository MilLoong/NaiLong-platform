package com.nailong.controller;

import com.nailong.common.result.Result;
import com.nailong.model.dto.notice.NoticeCreateDTO;
import com.nailong.model.dto.notice.NoticeUpdateDTO;
import com.nailong.model.vo.notice.NoticeVO;
import com.nailong.service.INoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @brief 公告控制器
 * @details 公告的增删改查和发布
 * @author Nailong
 */
@Slf4j
@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
@Tag(name = "公告管理", description = "公告的增删改查和发布")
public class NoticeController {

    private final INoticeService noticeService;

    /**
     * @brief 创建公告（仅管理员）
     * @param dto 公告创建请求体
     * @return 新公告ID
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建公告", description = "管理员创建新公告")
    public Result<Long> createNotice(@Validated @RequestBody NoticeCreateDTO dto) {
        Long noticeId = noticeService.createNotice(dto);
        return Result.success("公告创建成功", noticeId);
    }

    /**
     * @brief 更新公告（仅管理员）
     * @param dto 公告更新请求体
     * @return 操作结果
     */
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新公告", description = "管理员更新公告信息")
    public Result<Void> updateNotice(@Validated @RequestBody NoticeUpdateDTO dto) {
        noticeService.updateNotice(dto);
        return Result.success("公告更新成功", null);
    }

    /**
     * @brief 删除公告（仅管理员）
     * @param id 公告ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除公告", description = "管理员删除公告")
    public Result<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return Result.success("公告删除成功", null);
    }

    /**
     * @brief 发布公告（仅管理员）
     * @param id 公告ID
     * @return 操作结果
     */
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "发布公告", description = "管理员发布公告")
    public Result<Void> publishNotice(@PathVariable Long id) {
        noticeService.publishNotice(id);
        return Result.success("公告发布成功", null);
    }

    /**
     * @brief 获取公告详情
     * @param id 公告ID
     * @return 公告详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取公告详情", description = "查看公告的详细信息")
    public Result<NoticeVO> getNoticeDetail(@PathVariable Long id) {
        NoticeVO vo = noticeService.getNoticeDetail(id);
        return Result.success(vo);
    }

    /**
     * @brief 获取已发布的公告列表
     * @return 已发布公告列表
     */
    @GetMapping
    @Operation(summary = "获取公告列表", description = "查看所有已发布的公告")
    public Result<List<NoticeVO>> getPublishedNoticeList() {
        List<NoticeVO> voList = noticeService.getPublishedNoticeList();
        return Result.success(voList);
    }

    /**
     * @brief 获取所有公告列表（仅管理员）
     * @return 全部公告列表
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取所有公告", description = "管理员查看所有公告（包括隐藏的）")
    public Result<List<NoticeVO>> getAllNoticeList() {
        List<NoticeVO> voList = noticeService.getAllNoticeList();
        return Result.success(voList);
    }
}
