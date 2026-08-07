package com.nailong.service;

import com.nailong.common.exception.BusinessException;
import com.nailong.mapper.ProblemMapper;
import com.nailong.mapper.SubmissionMapper;
import com.nailong.model.entity.Problem;
import com.nailong.model.vo.problem.ProblemDetailVO;
import com.nailong.service.impl.ProblemServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @brief 题目服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class ProblemServiceImplTest {

    @Mock
    private ProblemMapper problemMapper;

    @Mock
    private SubmissionMapper submissionMapper;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private ProblemServiceImpl problemService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(problemService, "decayRate", new BigDecimal("0.01"));
        ReflectionTestUtils.setField(problemService, "minScore", 10);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void calculateDecayedScore_withSolvers() {
        // base=100, solved=10, decay=0.01 => 100 * (1 - 0.1) = 90
        assertEquals(90, problemService.calculateDecayedScore(100, 10));
    }

    @Test
    void calculateDecayedScore_shouldNotBelowMin() {
        // base=100, solved=100 => 100 * (1 - 1) = 0 => clamp to minScore 10
        assertEquals(10, problemService.calculateDecayedScore(100, 100));
    }

    @Test
    void calculateDecayedScore_noSolvers() {
        assertEquals(100, problemService.calculateDecayedScore(100, 0));
    }

    @Test
    void getProblemDetail_notFound_shouldThrow() {
        when(valueOperations.get(anyString())).thenReturn(null);
        when(problemMapper.selectById(1L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> problemService.getProblemDetail(1L, 1L));
    }

    @Test
    void getProblemDetail_success_fromDb() throws Exception {
        Problem problem = new Problem();
        problem.setId(1L);
        problem.setTitle("两数之和");
        problem.setCurrentScore(100);
        problem.setStatus(1);

        when(valueOperations.get(anyString())).thenReturn(null);
        when(problemMapper.selectById(1L)).thenReturn(problem);
        when(submissionMapper.selectCount(any())).thenReturn(0L);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        ProblemDetailVO vo = problemService.getProblemDetail(1L, 2L);

        assertEquals(1L, vo.getId());
        assertEquals("两数之和", vo.getTitle());
        assertFalse(Boolean.TRUE.equals(vo.getSolved()));
    }

    @Test
    void deleteProblem_notFound_shouldThrow() {
        when(problemMapper.selectById(99L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> problemService.deleteProblem(99L));
    }
}
