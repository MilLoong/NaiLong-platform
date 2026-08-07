package com.nailong.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nailong.common.annotation.RateLimit;
import com.nailong.common.annotation.RepeatSubmit;
import com.nailong.common.result.Result;
import com.nailong.common.utils.JwtUtil;
import com.nailong.common.utils.SpringUtil;
import com.nailong.common.utils.IpUtil;
import com.nailong.model.dto.problem.*;
import com.nailong.model.vo.problem.*;
import com.nailong.service.IProblemService;
import com.nailong.service.ISubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @brief 题目控制器
 * @details 题目的增删改查和答题提交
 * @author Nailong
 */
@Slf4j
@RestController
@RequestMapping("/problems")
@RequiredArgsConstructor
@Tag(name = "题目管理", description = "题目的增删改查和答题提交")
public class ProblemController {
    
    private final IProblemService problemService;
    private final ISubmissionService submissionService;
    
    /**
     * @brief 创建题目（仅管理员）
     * @param dto            题目创建请求体
     * @param authentication 当前认证信息
     * @param request        HTTP 请求
     * @return 操作结果
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建题目", description = "管理员创建新题目")
    public Result<Void> createProblem(@Validated @RequestBody ProblemCreateDTO dto, 
                                      Authentication authentication, 
                                      HttpServletRequest request) {
        Long creatorId = getCurrentUserId(authentication, request);
        problemService.createProblem(dto, creatorId);
        return Result.success("题目创建成功", null);
    }
    
    /**
     * @brief 更新题目（仅管理员）
     * @param dto 题目更新请求体
     * @return 操作结果
     */
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新题目", description = "管理员更新题目信息")
    public Result<Void> updateProblem(@Validated @RequestBody ProblemUpdateDTO dto) {
        problemService.updateProblem(dto);
        return Result.success("题目更新成功", null);
    }
    
    /**
     * @brief 删除题目（仅管理员）
     * @param id 题目ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除题目", description = "管理员删除题目")
    public Result<Void> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return Result.success("题目删除成功", null);
    }
    
    /**
     * @brief 获取题目详情
     * @param id             题目ID
     * @param authentication 当前认证信息（可选）
     * @param request        HTTP 请求
     * @return 题目详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取题目详情", description = "查看题目的详细信息")
    public Result<ProblemDetailVO> getProblemDetail(@PathVariable Long id, 
                                                   Authentication authentication, 
                                                   HttpServletRequest request) {
        Long userId = authentication != null ? getCurrentUserId(authentication, request) : null;
        ProblemDetailVO vo = problemService.getProblemDetail(id, userId);
        return Result.success(vo);
    }
    
    /**
     * @brief 分页查询题目列表
     * @param dto            查询条件
     * @param authentication 当前认证信息（可选）
     * @param request        HTTP 请求
     * @return 分页题目列表
     */
    @GetMapping
    @Operation(summary = "查询题目列表", description = "分页查询题目，支持多条件筛选")
    public Result<Page<ProblemListVO>> getProblemList(@Validated ProblemQueryDTO dto, 
                                                     Authentication authentication, 
                                                     HttpServletRequest request) {
        Long userId = authentication != null ? getCurrentUserId(authentication, request) : null;
        Page<ProblemListVO> page = problemService.getProblemList(dto, userId);
        return Result.success(page);
    }
    
    /**
     * @brief 提交答案
     * @param dto            提交答案请求体
     * @param authentication 当前认证信息
     * @param request        HTTP 请求
     * @return 操作结果
     */
    @PostMapping("/submit")
    @RateLimit(qps = 5)
    @RepeatSubmit
    @Operation(summary = "提交答案", description = "用户提交题目答案")
    public Result<Void> submitAnswer(@Validated @RequestBody SubmitAnswerDTO dto, 
                                    Authentication authentication, 
                                    HttpServletRequest request) {
        Long userId = getCurrentUserId(authentication, request);
        String ip = IpUtil.getIpAddress(request);
        
        submissionService.submitAnswer(dto, userId, ip);
        
        return Result.success("提交成功", null);
    }

    /**
     * @brief 从认证对象中获取用户ID
     * @param authentication 当前认证信息
     * @param request        HTTP 请求
     * @return 用户ID
     */
    private Long getCurrentUserId(Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            throw new RuntimeException("用户未登录或认证信息无效");
        }
        
        // 如果principal是Long类型，直接获取用户ID
        if (authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        
        // 兼容旧版本：如果principal是UserDetails对象
        if (authentication.getPrincipal() instanceof UserDetails) {
            // 尝试通过JwtUtil从token中获取用户ID
            String token = getTokenFromRequest(request);
            if (StringUtils.hasText(token)) {
                try {
                    JwtUtil jwtUtil = SpringUtil.getBean(JwtUtil.class);
                    return jwtUtil.getUserIdFromToken(token);
                } catch (Exception e) {
                    log.error("从token获取用户ID失败", e);
                    throw new RuntimeException("获取用户信息失败");
                }
            }
        }
        
        throw new RuntimeException("无法获取用户信息");
    }
    
    /**
     * @brief 从请求头中提取Token
     * @param request HTTP 请求
     * @return Bearer Token，未找到时返回 null
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}
