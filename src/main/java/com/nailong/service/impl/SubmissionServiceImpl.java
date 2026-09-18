package com.nailong.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nailong.common.constant.RedisKey;
import com.nailong.common.enums.ResultCode;
import com.nailong.common.exception.BusinessException;
import com.nailong.mapper.ProblemMapper;
import com.nailong.mapper.SubmissionMapper;
import com.nailong.mapper.UserMapper;
import com.nailong.model.dto.problem.SubmitAnswerDTO;
import com.nailong.model.dto.submission.SubmissionCountDTO;
import com.nailong.model.entity.Problem;
import com.nailong.model.entity.Submission;
import com.nailong.model.entity.User;
import com.nailong.model.vo.SubmitResultVO;
import com.nailong.model.vo.submission.SubmissionDetailVO;
import com.nailong.model.vo.submission.SubmissionListVO;
import com.nailong.service.IProblemService;
import com.nailong.service.ISubmissionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @brief 提交服务实现
 * @details 实现答题提交、自动判题、提交记录查询与重新判题
 * @author Nailong
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements ISubmissionService {

    private final SubmissionMapper submissionMapper;
    private final ProblemMapper problemMapper;
    private final UserMapper userMapper;
    private final IProblemService problemService;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate redisTemplate;

    /**
     * @brief 提交答案（核心方法）
     * @param dto    提交请求
     * @param userId 用户 ID
     * @param ip     客户端 IP
     * @details 使用分布式锁防并发重复提交；答对时更新积分与排行榜缓存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitAnswer(SubmitAnswerDTO dto, Long userId, String ip) {
        // 查题 -> 是否已 AC -> 分布式锁 -> 判题 -> 落库 / 更新积分与衰减分 -> 清缓存

        Long problemId = dto.getProblemId();

        // 查题
        Problem problem = problemMapper.selectById(problemId);
        if (problem == null || problem.getStatus() == 0) {
            throw new BusinessException(ResultCode.PROBLEM_NOT_FOUND);
        }

        // 是否已 AC
        LambdaQueryWrapper<Submission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Submission::getUserId, userId)
               .eq(Submission::getProblemId, problemId)
               .eq(Submission::getStatus, "ACCEPTED");

        if (submissionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.PROBLEM_ALREADY_SOLVED);
        }

        // 分布式锁
        String lockKey = RedisKey.LOCK + "submit:" + userId + ":" + problemId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                throw new BusinessException("提交过于频繁，请稍后再试");
            }

            // 判题
            boolean isCorrect = judgeAnswer(problem, dto.getAnswer());

            Submission submission = new Submission();
            submission.setUserId(userId);
            submission.setProblemId(problemId);
            submission.setAnswer(dto.getAnswer());
            submission.setLanguage(dto.getLanguage());
            submission.setIp(ip);

            if (isCorrect) {
                // 落库 AC，更新题目衰减分与用户积分，清缓存
                submission.setStatus("ACCEPTED");

                Integer score = problemService.calculateDecayedScore(
                    problem.getBaseScore(),
                    problem.getSolvedCount()
                );
                submission.setScore(score);

                Integer newScore = problemService.calculateDecayedScore(
                    problem.getBaseScore(),
                    problem.getSolvedCount() + 1
                );
                problemMapper.updateScoreAndCount(problemId, newScore);

                userMapper.updateScore(userId, score);

                redisTemplate.delete(RedisKey.USER_INFO + userId);
                clearRankCache();

                log.info("答案正确: userId={}, problemId={}, score={}", userId, problemId, score);
            } else {
                // 落库 WA
                submission.setStatus("WRONG_ANSWER");
                submission.setScore(0);
                submission.setRemark("答案错误");

                problemMapper.increaseSubmitCount(problemId);

                log.info("答案错误: userId={}, problemId={}", userId, problemId);
            }

            submissionMapper.insert(submission);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("系统繁忙，请稍后再试");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * @brief 按题目类型判题
     * @param problem    题目实体
     * @param userAnswer 用户答案
     * @return 是否正确
     */
    private boolean judgeAnswer(Problem problem, String userAnswer) {
        String correctAnswer = problem.getAnswer();
        String type = problem.getType();

        if (correctAnswer == null || correctAnswer.isEmpty()) {
            throw new BusinessException("该题目暂不支持自动判题");
        }

        userAnswer = userAnswer.trim();
        correctAnswer = correctAnswer.trim();

        return switch (type) {
            case "CHOICE" -> userAnswer.equalsIgnoreCase(correctAnswer);
            case "FLAG" -> judgeFlagAnswer(userAnswer, correctAnswer);
            case "FILE" -> judgeFileAnswer(userAnswer, correctAnswer);
            case "CODE" -> judgeCodeAnswer(userAnswer, correctAnswer);
            default -> false;
        };
    }

    /**
     * @brief Flag 题判题
     * @param userAnswer     用户答案
     * @param correctAnswer  正确答案
     * @return 是否正确
     * @details 支持明文与 MD5 两种比较方式
     */
    private boolean judgeFlagAnswer(String userAnswer, String correctAnswer) {
        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            return true;
        }

        String userAnswerMd5 = DigestUtil.md5Hex(userAnswer);
        if (userAnswerMd5.equalsIgnoreCase(correctAnswer)) {
            return true;
        }

        String correctAnswerMd5 = DigestUtil.md5Hex(correctAnswer);
        return userAnswerMd5.equalsIgnoreCase(correctAnswerMd5);
    }

    /**
     * @brief 附件题判题
     * @param userAnswer     用户答案
     * @param correctAnswer  正确答案
     * @return 是否正确
     * @details 当前为模拟逻辑，实际应集成文件存储与人工审核
     */
    private boolean judgeFileAnswer(String userAnswer, String correctAnswer) {
        if (userAnswer.contains("已上传") || userAnswer.contains("uploaded") ||
            userAnswer.startsWith("http://") || userAnswer.startsWith("https://")) {
            log.info("文件题需要人工审核，答案: {}", userAnswer);
            return false;
        }
        return false;
    }

    /**
     * @brief 编程题判题
     * @param userAnswer     用户代码
     * @param correctAnswer  期望关键字（逗号分隔）
     * @return 是否正确
     * @details 当前为关键字匹配模拟，实际应集成在线判题系统
     */
    private boolean judgeCodeAnswer(String userAnswer, String correctAnswer) {
        userAnswer = userAnswer.toLowerCase();
        correctAnswer = correctAnswer.toLowerCase();

        String[] keywords = correctAnswer.split(",");
        for (String keyword : keywords) {
            if (!userAnswer.contains(keyword.trim())) {
                return false;
            }
        }

        log.info("编程题简单验证通过，需要实际执行验证，答案: {}", userAnswer);
        return false;
    }

    /**
     * @brief 清除排行榜缓存
     */
    private void clearRankCache() {
        redisTemplate.delete(RedisKey.RANK + "all");
        String[] directions = {"frontend", "backend", "android", "design", "operations"};
        for (String direction : directions) {
            redisTemplate.delete(RedisKey.RANK + direction);
        }
    }

    /**
     * @brief 提交答案（Controller 入口）
     * @param dto 提交请求
     * @return 提交结果 VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubmitResultVO submitAnswer(SubmitAnswerDTO dto) {
        User currentUser = getCurrentUser();
        Long userId = currentUser.getId();

        String ip = "127.0.0.1";
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                jakarta.servlet.http.HttpServletRequest request = requestAttributes.getRequest();
                String forwardedFor = request.getHeader("X-Forwarded-For");
                if (forwardedFor != null && !forwardedFor.isEmpty()) {
                    ip = forwardedFor.split(",")[0].trim();
                } else {
                    ip = request.getRemoteAddr();
                }
            }
        } catch (Exception e) {
            log.error("获取IP地址失败", e);
        }

        submitAnswer(dto, userId, ip);

        LambdaQueryWrapper<Submission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Submission::getUserId, userId)
               .eq(Submission::getProblemId, dto.getProblemId())
               .orderByDesc(Submission::getCreateTime)
               .last("LIMIT 1");
        Submission submission = submissionMapper.selectOne(wrapper);

        SubmitResultVO result = new SubmitResultVO();
        result.setCorrect("ACCEPTED".equals(submission.getStatus()));
        result.setStatus(submission.getStatus());
        result.setScore(submission.getScore());
        result.setMessage(result.getCorrect() ? "答案正确，恭喜您！" : "答案错误，请继续努力！");
        result.setSubmissionId(submission.getId());

        return result;
    }

    /**
     * @brief 获取当前用户的提交记录
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 提交记录列表
     */
    @Override
    public List<SubmissionListVO> getUserSubmissions(Integer pageNum, Integer pageSize) {
        User currentUser = getCurrentUser();
        Long userId = currentUser.getId();

        int offset = (pageNum - 1) * pageSize;

        List<Submission> submissions = submissionMapper.selectList(
            new LambdaQueryWrapper<Submission>()
                .eq(Submission::getUserId, userId)
                .orderByDesc(Submission::getCreateTime)
                .last("LIMIT " + offset + "," + pageSize)
        );

        List<SubmissionListVO> voList = new ArrayList<>();
        for (Submission submission : submissions) {
            SubmissionListVO vo = BeanUtil.copyProperties(submission, SubmissionListVO.class);

            Problem problem = problemMapper.selectById(submission.getProblemId());
            if (problem != null) {
                vo.setProblemTitle(problem.getTitle());
            }

            voList.add(vo);
        }

        return voList;
    }

    /**
     * @brief 获取题目的提交统计
     * @param problemId 题目 ID
     * @return 提交统计 DTO（含通过率）
     */
    @Override
    public SubmissionCountDTO getProblemSubmissionStats(Long problemId) {
        Problem problem = problemMapper.selectById(problemId);
        if (problem == null || problem.getStatus() == 0) {
            throw new BusinessException(ResultCode.PROBLEM_NOT_FOUND);
        }

        SubmissionCountDTO stats = submissionMapper.getProblemSubmissionStats(problemId);
        if (stats == null) {
            stats = new SubmissionCountDTO();
            stats.setTotalSubmissions(0);
            stats.setSuccessSubmissions(0);
            stats.setProblemId(problemId);
        }

        stats.setProblemId(problemId);
        stats.setProblemTitle(problem.getTitle());

        if (stats.getTotalSubmissions() > 0) {
            double passRate = (double) stats.getSuccessSubmissions() / stats.getTotalSubmissions();
            stats.setPassRate(passRate);
        } else {
            stats.setPassRate(0.0);
        }

        return stats;
    }

    /**
     * @brief 获取提交详情
     * @param submissionId 提交记录 ID
     * @return 提交详情 VO
     */
    @Override
    public SubmissionDetailVO getSubmissionDetail(Long submissionId) {
        Submission submission = submissionMapper.selectById(submissionId);
        if (submission == null) {
            throw new BusinessException("提交记录不存在");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        Object principal = authentication.getPrincipal();
        Long currentUserId = principal instanceof Long ? (Long) principal : null;
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isAdmin && (currentUserId == null || !currentUserId.equals(submission.getUserId()))) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        User user = userMapper.selectById(submission.getUserId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        Problem problem = problemMapper.selectById(submission.getProblemId());
        if (problem == null) {
            throw new BusinessException(ResultCode.PROBLEM_NOT_FOUND);
        }

        SubmissionDetailVO detail = BeanUtil.copyProperties(submission, SubmissionDetailVO.class);
        detail.setUsername(user.getUsername());
        detail.setProblemTitle(problem.getTitle());

        return detail;
    }

    /**
     * @brief 重新判题
     * @param submissionId 提交记录 ID
     * @return 判题结果 VO
     * @details 状态变更时同步回滚或补记题目统计与用户积分
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubmitResultVO rejudgeSubmission(Long submissionId) {
        Submission submission = submissionMapper.selectById(submissionId);
        if (submission == null) {
            throw new BusinessException("提交记录不存在");
        }

        Problem problem = problemMapper.selectById(submission.getProblemId());
        if (problem == null) {
            throw new BusinessException(ResultCode.PROBLEM_NOT_FOUND);
        }

        boolean isCorrect = judgeAnswer(problem, submission.getAnswer());

        String oldStatus = submission.getStatus();
        Integer oldScore = submission.getScore();

        try {
            submission.setStatus(isCorrect ? "ACCEPTED" : "WRONG_ANSWER");
            submission.setScore(isCorrect ? problemService.calculateDecayedScore(problem.getBaseScore(), problem.getSolvedCount()) : 0);

            if (isCorrect && !"ACCEPTED".equals(oldStatus)) {
                Integer newScore = problemService.calculateDecayedScore(
                    problem.getBaseScore(),
                    problem.getSolvedCount() + 1
                );
                problemMapper.updateScoreAndCount(submission.getProblemId(), newScore);
                userMapper.updateScore(submission.getUserId(), submission.getScore());
                clearRankCache();
            } else if (!isCorrect && "ACCEPTED".equals(oldStatus)) {
                problemMapper.updateScoreAndCount(submission.getProblemId(), problem.getCurrentScore());
                userMapper.updateScore(submission.getUserId(), -oldScore);
                clearRankCache();
            }

            submissionMapper.updateById(submission);

            SubmitResultVO result = new SubmitResultVO();
            result.setCorrect(isCorrect);
            result.setStatus(submission.getStatus());
            result.setScore(submission.getScore());
            result.setMessage("重新判题完成");
            result.setSubmissionId(submission.getId());

            return result;
        } catch (Exception e) {
            submission.setStatus(oldStatus);
            submission.setScore(oldScore);
            submissionMapper.updateById(submission);
            throw new BusinessException("重新判题失败: " + e.getMessage());
        }
    }

    /**
     * @brief 获取当前登录用户
     * @return 用户实体
     * @details 兼容 principal 为 Long 或 UserDetails 两种认证方式
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        if (authentication.getPrincipal() instanceof Long) {
            Long userId = (Long) authentication.getPrincipal();
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(ResultCode.USER_NOT_FOUND);
            }
            return user;
        }

        if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            String username = ((org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal()).getUsername();
            try {
                LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(User::getUsername, username);
                User user = userMapper.selectOne(wrapper);
                if (user == null) {
                    throw new BusinessException(ResultCode.USER_NOT_FOUND);
                }
                return user;
            } catch (Exception e) {
                log.error("获取用户信息失败", e);
                throw new BusinessException(ResultCode.UNAUTHORIZED);
            }
        }
        throw new BusinessException(ResultCode.UNAUTHORIZED);
    }
}
