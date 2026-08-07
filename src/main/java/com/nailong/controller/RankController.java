package com.nailong.controller;

import com.nailong.common.result.Result;
import com.nailong.common.utils.JwtUtil;
import com.nailong.common.utils.SpringUtil;
import com.nailong.model.vo.user.UserRankVO;
import com.nailong.service.IRankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @brief 排行榜控制器
 * @details 查看总榜、方向榜和个人排名
 * @author Nailong
 */
@Slf4j
@RestController
@RequestMapping("/rank")
@RequiredArgsConstructor
@Tag(name = "排行榜管理", description = "查看总榜、方向榜和个人排名")
public class RankController {
    
    private final IRankService rankService;
    
    /**
     * @brief 获取总排行榜
     * @param limit 返回条数上限
     * @return 总排行榜列表
     */
    @GetMapping("/all")
    @Operation(summary = "总排行榜", description = "查看全部用户的排名")
    public Result<List<UserRankVO>> getAllRank(@RequestParam(defaultValue = "50") Integer limit) {
        List<UserRankVO> rankList = rankService.getRankList(limit);
        return Result.success(rankList);
    }
    
    /**
     * @brief 获取指定方向的排行榜
     * @param direction 方向标识
     * @param limit     返回条数上限
     * @return 方向排行榜列表
     */
    @GetMapping("/direction/{direction}")
    @Operation(summary = "方向排行榜", description = "查看指定方向的排名")
    public Result<List<UserRankVO>> getDirectionRank(
            @PathVariable String direction,
            @RequestParam(defaultValue = "50") Integer limit) {
        List<UserRankVO> rankList = rankService.getRankListByDirection(direction, limit);
        return Result.success(rankList);
    }
    
    /**
     * @brief 获取我的排名
     * @param authentication 当前认证信息
     * @param request        HTTP 请求
     * @return 当前用户的总排名信息
     */
    @GetMapping("/my")
    @Operation(summary = "我的排名", description = "查看当前用户的排名信息")
    public Result<Map<String, Object>> getMyRank(Authentication authentication, 
                                               HttpServletRequest request) {
        Long userId = getCurrentUserId(authentication, request);
        
        // 获取总排名
        Long allRank = rankService.getUserRank(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("allRank", allRank);
        result.put("userId", userId);
        
        return Result.success(result);
    }
    
    /**
     * @brief 获取我在指定方向的排名
     * @param direction      方向标识
     * @param authentication 当前认证信息
     * @param request        HTTP 请求
     * @return 当前用户在该方向的排名信息
     */
    @GetMapping("/my/direction/{direction}")
    @Operation(summary = "我的方向排名", description = "查看当前用户在指定方向的排名")
    public Result<Map<String, Object>> getMyDirectionRank(
            @PathVariable String direction,
            Authentication authentication,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(authentication, request);
        
        Long rank = rankService.getUserRankByDirection(userId, direction);
        
        Map<String, Object> result = new HashMap<>();
        result.put("direction", direction);
        result.put("rank", rank);
        result.put("userId", userId);
        
        return Result.success(result);
    }
    
    /**
     * @brief 手动同步排行榜（管理员）
     * @return 操作结果
     */
    @PostMapping("/sync")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "同步排行榜", description = "手动触发排行榜数据同步（管理员）")
    public Result<Void> syncRank() {
        rankService.syncRankData();
        return Result.success("排行榜同步成功", null);
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
