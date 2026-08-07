package com.nailong.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.nailong.common.constant.RedisKey;
import com.nailong.mapper.UserMapper;
import com.nailong.model.entity.User;
import com.nailong.model.vo.user.UserRankVO;
import com.nailong.service.IRankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @brief 排行榜服务实现
 * @details 基于 Redis ZSet 实现总榜与方向榜，支持实时更新与定时全量同步
 * @author Nailong
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RankServiceImpl implements IRankService {

    private final UserMapper userMapper;
    private final StringRedisTemplate redisTemplate;

    @Value("${nailong.rank.cache-expire}")
    private Long cacheExpire;

    @Value("${nailong.rank.page-size}")
    private Integer pageSize;

    /**
     * @brief 应用启动时初始化排行榜数据
     */
    @PostConstruct
    public void init() {
        log.info("开始初始化排行榜数据...");
        syncRankData();
        log.info("排行榜数据初始化完成");
    }

    /**
     * @brief 定时同步排行榜数据
     * @details 每 5 分钟全量同步，防止 Redis 数据丢失或不一致
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void scheduledSyncRankData() {
        log.info("开始定时同步排行榜数据");
        syncRankData();
    }

    /**
     * @brief 获取总排行榜
     * @param limit 返回条数上限
     * @return 排行榜 VO 列表
     */
    @Override
    public List<UserRankVO> getRankList(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = pageSize;
        }

        String key = RedisKey.RANK + "all";

        Set<ZSetOperations.TypedTuple<String>> rankSet =
            redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, limit - 1);

        if (rankSet == null || rankSet.isEmpty()) {
            syncRankData();
            rankSet = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, limit - 1);
        }

        return buildRankVOList(rankSet);
    }

    /**
     * @brief 获取指定方向的排行榜
     * @param direction 方向标识
     * @param limit     返回条数上限
     * @return 排行榜 VO 列表
     */
    @Override
    public List<UserRankVO> getRankListByDirection(String direction, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = pageSize;
        }

        String key = RedisKey.RANK + direction;

        Set<ZSetOperations.TypedTuple<String>> rankSet =
            redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, limit - 1);

        if (rankSet == null || rankSet.isEmpty()) {
            syncRankData();
            rankSet = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, limit - 1);
        }

        return buildRankVOList(rankSet);
    }

    /**
     * @brief 获取用户在总榜的排名
     * @param userId 用户 ID
     * @return 排名（从 1 开始），未上榜时返回 null
     */
    @Override
    public Long getUserRank(Long userId) {
        String key = RedisKey.RANK + "all";
        Long rank = redisTemplate.opsForZSet().reverseRank(key, userId.toString());

        return rank != null ? rank + 1 : null;
    }

    /**
     * @brief 获取用户在指定方向的排名
     * @param userId    用户 ID
     * @param direction 方向标识
     * @return 排名（从 1 开始），未上榜时返回 null
     */
    @Override
    public Long getUserRankByDirection(Long userId, String direction) {
        String key = RedisKey.RANK + direction;
        Long rank = redisTemplate.opsForZSet().reverseRank(key, userId.toString());

        return rank != null ? rank + 1 : null;
    }

    /**
     * @brief 全量同步排行榜数据
     * @details 从 MySQL 同步总榜与各方向榜到 Redis ZSet
     */
    @Override
    public void syncRankData() {
        try {
            syncAllRank();

            String[] directions = {"frontend", "backend", "android", "design", "operations"};
            for (String direction : directions) {
                syncDirectionRank(direction);
            }

            log.info("排行榜数据同步完成");
        } catch (Exception e) {
            log.error("排行榜数据同步失败", e);
        }
    }

    /**
     * @brief 同步总排行榜到 Redis
     */
    private void syncAllRank() {
        String key = RedisKey.RANK + "all";

        List<User> userList = userMapper.selectRankList(1000);

        redisTemplate.delete(key);

        for (User user : userList) {
            double score = user.getTotalScore() == null ? 0D : user.getTotalScore().doubleValue();
            redisTemplate.opsForZSet().add(
                key,
                user.getId().toString(),
                score
            );
        }

        redisTemplate.expire(key, cacheExpire, TimeUnit.SECONDS);

        log.debug("总排行榜同步完成，共{}条数据", userList.size());
    }

    /**
     * @brief 同步指定方向排行榜到 Redis
     * @param direction 方向标识
     */
    private void syncDirectionRank(String direction) {
        String key = RedisKey.RANK + direction;

        List<User> userList = userMapper.selectRankListByDirection(direction, 1000);

        redisTemplate.delete(key);

        for (User user : userList) {
            double score = user.getTotalScore() == null ? 0D : user.getTotalScore().doubleValue();
            redisTemplate.opsForZSet().add(
                key,
                user.getId().toString(),
                score
            );
        }

        redisTemplate.expire(key, cacheExpire, TimeUnit.SECONDS);

        log.debug("方向[{}]排行榜同步完成，共{}条数据", direction, userList.size());
    }

    /**
     * @brief 实时更新单个用户的排行榜数据
     * @param userId 用户 ID
     * @details 用户答题成功后调用，同步更新总榜与方向榜
     */
    @Override
    public void updateUserRank(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return;
        }

        double score = user.getTotalScore() == null ? 0D : user.getTotalScore().doubleValue();
        String allKey = RedisKey.RANK + "all";
        redisTemplate.opsForZSet().add(
            allKey,
            userId.toString(),
            score
        );

        if (user.getDirection() != null) {
            String directionKey = RedisKey.RANK + user.getDirection();
            redisTemplate.opsForZSet().add(
                directionKey,
                userId.toString(),
                score
            );
        }

        log.debug("用户排名更新完成: userId={}, score={}", userId, user.getTotalScore());
    }

    /**
     * @brief 构建排行榜 VO 列表
     * @param rankSet Redis ZSet 查询结果
     * @return 排行榜 VO 列表
     */
    private List<UserRankVO> buildRankVOList(Set<ZSetOperations.TypedTuple<String>> rankSet) {
        if (rankSet == null || rankSet.isEmpty()) {
            return new ArrayList<>();
        }

        List<UserRankVO> voList = new ArrayList<>();
        long rank = 1;

        for (ZSetOperations.TypedTuple<String> tuple : rankSet) {
            String userIdStr = tuple.getValue();
            Double score = tuple.getScore();

            if (userIdStr == null) {
                continue;
            }

            Long userId = Long.parseLong(userIdStr);
            User user = userMapper.selectById(userId);

            if (user != null) {
                UserRankVO vo = BeanUtil.copyProperties(user, UserRankVO.class);
                vo.setUserId(userId);
                vo.setRank(rank++);
                vo.setTotalScore(score.intValue());
                voList.add(vo);
            }
        }

        return voList;
    }
}
