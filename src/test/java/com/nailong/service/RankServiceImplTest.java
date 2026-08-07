package com.nailong.service;

import com.nailong.common.constant.RedisKey;
import com.nailong.mapper.UserMapper;
import com.nailong.model.entity.User;
import com.nailong.model.vo.user.UserRankVO;
import com.nailong.service.impl.RankServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @brief 排行榜服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class RankServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private RankServiceImpl rankService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(rankService, "cacheExpire", 3600L);
        ReflectionTestUtils.setField(rankService, "pageSize", 20);
        lenient().when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    void getUserRank_success() {
        when(zSetOperations.reverseRank(RedisKey.RANK + "all", "1")).thenReturn(2L);

        Long rank = rankService.getUserRank(1L);

        assertEquals(3L, rank); // reverseRank 从 0 开始，服务层 +1
    }

    @Test
    void getUserRank_notOnBoard() {
        when(zSetOperations.reverseRank(RedisKey.RANK + "all", "1")).thenReturn(null);
        assertNull(rankService.getUserRank(1L));
    }

    @Test
    void getRankList_fromRedis() {
        @SuppressWarnings("unchecked")
        ZSetOperations.TypedTuple<String> tuple = mock(ZSetOperations.TypedTuple.class);
        when(tuple.getValue()).thenReturn("1");
        when(tuple.getScore()).thenReturn(200.0);
        Set<ZSetOperations.TypedTuple<String>> rankSet = new LinkedHashSet<>();
        rankSet.add(tuple);

        when(zSetOperations.reverseRangeWithScores(RedisKey.RANK + "all", 0, 9))
                .thenReturn(rankSet);

        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setNickname("Alice");
        user.setTotalScore(200);
        user.setSolvedCount(5);
        when(userMapper.selectById(1L)).thenReturn(user);

        List<UserRankVO> list = rankService.getRankList(10);

        assertEquals(1, list.size());
        assertEquals(1L, list.get(0).getRank());
        assertEquals("alice", list.get(0).getUsername());
        assertEquals(200, list.get(0).getTotalScore());
    }

    @Test
    void getRankList_emptyThenSync() {
        when(zSetOperations.reverseRangeWithScores(eq(RedisKey.RANK + "all"), anyLong(), anyLong()))
                .thenReturn(Collections.emptySet())
                .thenReturn(Collections.emptySet());
        when(userMapper.selectRankList(1000)).thenReturn(Collections.emptyList());
        when(userMapper.selectRankListByDirection(anyString(), eq(1000)))
                .thenReturn(Collections.emptyList());

        List<UserRankVO> list = rankService.getRankList(5);

        assertNotNull(list);
        assertTrue(list.isEmpty());
        verify(userMapper).selectRankList(1000);
    }

    @Test
    void updateUserRank_userMissing_shouldSkip() {
        when(userMapper.selectById(99L)).thenReturn(null);
        rankService.updateUserRank(99L);
        verify(zSetOperations, never()).add(anyString(), anyString(), anyDouble());
    }
}
