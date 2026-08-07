package com.nailong.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailong.common.constant.RedisKey;
import com.nailong.common.enums.ResultCode;
import com.nailong.common.exception.BusinessException;
import com.nailong.mapper.ProblemMapper;
import com.nailong.mapper.SubmissionMapper;
import com.nailong.model.dto.problem.*;
import com.nailong.model.entity.Problem;
import com.nailong.model.entity.Submission;
import com.nailong.model.vo.problem.*;
import com.nailong.service.IProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @brief 题目服务实现
 * @details 提供题目 CRUD、分页查询、分数衰减计算与 Redis 缓存
 * @author Nailong
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements IProblemService {

    private final ProblemMapper problemMapper;
    private final SubmissionMapper submissionMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${nailong.score.decay-rate}")
    private BigDecimal decayRate;

    @Value("${nailong.score.min-score:10}")
    private Integer minScore;

    private static final long CACHE_EXPIRE_TIME = 3600;

    /**
     * @brief 创建题目
     * @param dto       创建请求
     * @param creatorId 创建者用户 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createProblem(ProblemCreateDTO dto, Long creatorId) {
        Problem problem = new Problem();
        BeanUtil.copyProperties(dto, problem);

        problem.setCreatorId(creatorId);
        problem.setCurrentScore(dto.getBaseScore());
        problem.setDecayRate(decayRate);
        problem.setMinScore(minScore);
        problem.setSolvedCount(0);
        problem.setSubmitCount(0);
        problem.setStatus(1);

        problemMapper.insert(problem);

        clearProblemCache(problem.getId());
        clearProblemListCache();

        log.info("题目创建成功: id={}, title={}", problem.getId(), problem.getTitle());
    }

    /**
     * @brief 更新题目
     * @param dto 更新请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProblem(ProblemUpdateDTO dto) {
        Problem problem = problemMapper.selectById(dto.getId());
        if (problem == null) {
            throw new BusinessException(ResultCode.PROBLEM_NOT_FOUND);
        }

        BeanUtil.copyProperties(dto, problem, "id", "creatorId");
        problemMapper.updateById(problem);

        clearProblemCache(dto.getId());
        clearProblemListCache();

        log.info("题目更新成功: id={}", dto.getId());
    }

    /**
     * @brief 删除题目
     * @param id 题目 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProblem(Long id) {
        Problem problem = problemMapper.selectById(id);
        if (problem == null) {
            throw new BusinessException(ResultCode.PROBLEM_NOT_FOUND);
        }

        problemMapper.deleteById(id);
        clearProblemCache(id);
        clearProblemListCache();

        log.info("题目删除成功: id={}", id);
    }

    /**
     * @brief 获取题目详情
     * @param id     题目 ID
     * @param userId 当前用户 ID（可为 null）
     * @return 题目详情 VO
     * @details 缓存不含用户解题状态，命中缓存后需单独查询 solved 字段
     */
    @Override
    public ProblemDetailVO getProblemDetail(Long id, Long userId) {
        try {
            String cacheKey = RedisKey.PROBLEM_INFO + id;
            ProblemDetailVO cachedVO = getFromCache(cacheKey, ProblemDetailVO.class);
            if (cachedVO != null) {
                if (userId != null) {
                    LambdaQueryWrapper<Submission> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Submission::getUserId, userId)
                           .eq(Submission::getProblemId, id)
                           .eq(Submission::getStatus, "ACCEPTED");
                    cachedVO.setSolved(submissionMapper.selectCount(wrapper) > 0);
                } else {
                    cachedVO.setSolved(false);
                }
                return cachedVO;
            }
        } catch (Exception e) {
            log.error("从缓存获取题目详情失败: id={}", id, e);
        }

        Problem problem = problemMapper.selectById(id);
        if (problem == null || problem.getStatus() == 0) {
            throw new BusinessException(ResultCode.PROBLEM_NOT_FOUND);
        }

        ProblemDetailVO vo = BeanUtil.copyProperties(problem, ProblemDetailVO.class);

        if (userId != null) {
            LambdaQueryWrapper<Submission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Submission::getUserId, userId)
                   .eq(Submission::getProblemId, id)
                   .eq(Submission::getStatus, "ACCEPTED");
            vo.setSolved(submissionMapper.selectCount(wrapper) > 0);
        } else {
            vo.setSolved(false);
        }

        try {
            String cacheKey = RedisKey.PROBLEM_INFO + id;
            ProblemDetailVO cacheVO = BeanUtil.copyProperties(vo, ProblemDetailVO.class);
            cacheVO.setSolved(null);
            putToCache(cacheKey, cacheVO, CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("缓存题目详情失败: id={}", id, e);
        }

        return vo;
    }

    /**
     * @brief 分页查询题目列表
     * @param dto    查询条件
     * @param userId 当前用户 ID（可为 null）
     * @return 分页题目列表
     */
    @Override
    public Page<ProblemListVO> getProblemList(ProblemQueryDTO dto, Long userId) {
        LambdaQueryWrapper<Problem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Problem::getStatus, 1)
               .eq(StringUtils.hasText(dto.getType()), Problem::getType, dto.getType())
               .eq(StringUtils.hasText(dto.getDirection()), Problem::getDirection, dto.getDirection())
               .eq(StringUtils.hasText(dto.getDifficulty()), Problem::getDifficulty, dto.getDifficulty())
               .and(StringUtils.hasText(dto.getKeyword()), w -> w
                   .like(Problem::getTitle, dto.getKeyword())
                   .or()
                   .like(Problem::getDescription, dto.getKeyword())
               )
               .orderByDesc(Problem::getSortOrder)
               .orderByDesc(Problem::getCreateTime);

        Page<Problem> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<Problem> problemPage = problemMapper.selectPage(page, wrapper);

        Page<ProblemListVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ProblemListVO> voList = problemPage.getRecords().stream().map(problem -> {
            ProblemListVO vo = BeanUtil.copyProperties(problem, ProblemListVO.class);

            if (userId != null) {
                LambdaQueryWrapper<Submission> subWrapper = new LambdaQueryWrapper<>();
                subWrapper.eq(Submission::getUserId, userId)
                         .eq(Submission::getProblemId, problem.getId())
                         .eq(Submission::getStatus, "ACCEPTED");
                vo.setSolved(submissionMapper.selectCount(subWrapper) > 0);
            } else {
                vo.setSolved(false);
            }

            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * @brief 计算题目衰减后的分数
     * @param baseScore   基础分数
     * @param solvedCount 已解题人数
     * @return 衰减后分数（不低于最低分）
     * @details 公式：currentScore = baseScore × (1 - decayRate × solvedCount)
     */
    @Override
    public Integer calculateDecayedScore(Integer baseScore, Integer solvedCount) {
        BigDecimal base = new BigDecimal(baseScore);
        BigDecimal decay = decayRate.multiply(new BigDecimal(solvedCount));
        BigDecimal factor = BigDecimal.ONE.subtract(decay);

        Integer newScore = base.multiply(factor).setScale(0, RoundingMode.HALF_UP).intValue();

        return Math.max(newScore, minScore);
    }

    /**
     * @brief 从 Redis 读取对象缓存
     * @param key   缓存键
     * @param clazz 目标类型
     * @return 缓存对象，不存在时返回 null
     */
    private <T> T getFromCache(String key, Class<T> clazz) {
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("从缓存获取对象失败: key={}", key, e);
            return null;
        }
    }

    /**
     * @brief 将对象写入 Redis 缓存
     * @param key      缓存键
     * @param object   缓存对象
     * @param timeout  过期时长
     * @param timeUnit 时间单位
     */
    private <T> void putToCache(String key, T object, long timeout, TimeUnit timeUnit) {
        try {
            String json = objectMapper.writeValueAsString(object);
            redisTemplate.opsForValue().set(key, json, timeout, timeUnit);
        } catch (Exception e) {
            log.error("缓存对象失败: key={}", key, e);
        }
    }

    /**
     * @brief 清除指定题目缓存
     * @param problemId 题目 ID
     */
    private void clearProblemCache(Long problemId) {
        try {
            String key = RedisKey.PROBLEM_INFO + problemId;
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("清除题目缓存失败: id={}", problemId, e);
        }
    }

    /**
     * @brief 清除题目列表缓存
     */
    private void clearProblemListCache() {
        Set<String> keys = redisTemplate.keys(RedisKey.PROBLEM_LIST + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
