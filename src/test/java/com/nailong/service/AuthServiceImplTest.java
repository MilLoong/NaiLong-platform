package com.nailong.service;

import com.nailong.common.constant.RedisKey;
import com.nailong.common.constant.SecurityConstant;
import com.nailong.common.exception.BusinessException;
import com.nailong.common.utils.JwtUtil;
import com.nailong.common.utils.PasswordUtil;
import com.nailong.mapper.UserMapper;
import com.nailong.model.dto.user.EmailCodeDTO;
import com.nailong.model.dto.user.UserLoginDTO;
import com.nailong.model.dto.user.UserRegisterDTO;
import com.nailong.model.entity.User;
import com.nailong.model.vo.user.LoginVO;
import com.nailong.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @brief 认证服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private IEmailService emailService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "accessTokenExpire", 86400L);
        ReflectionTestUtils.setField(authService, "refreshTokenExpire", 604800L);
        ReflectionTestUtils.setField(authService, "codeExpire", 300L);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void register_success() {
        UserRegisterDTO dto = buildRegisterDto();
        when(userMapper.selectCount(any())).thenReturn(0L);
        when(valueOperations.get(RedisKey.EMAIL_CODE + "register:" + dto.getEmail()))
                .thenReturn("123456");

        authService.register(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertEquals(dto.getUsername(), saved.getUsername());
        assertTrue(PasswordUtil.matches(dto.getPassword(), saved.getPassword()));
        verify(redisTemplate).delete(RedisKey.EMAIL_CODE + "register:" + dto.getEmail());
    }

    @Test
    void register_usernameExists_shouldThrow() {
        UserRegisterDTO dto = buildRegisterDto();
        when(userMapper.selectCount(any())).thenReturn(1L);

        assertThrows(BusinessException.class, () -> authService.register(dto));
        verify(userMapper, never()).insert(any());
    }

    @Test
    void register_invalidCode_shouldThrow() {
        UserRegisterDTO dto = buildRegisterDto();
        when(userMapper.selectCount(any())).thenReturn(0L);
        when(valueOperations.get(anyString())).thenReturn("000000");

        assertThrows(BusinessException.class, () -> authService.register(dto));
        verify(userMapper, never()).insert(any());
    }

    @Test
    void login_success() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("Password123");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword(PasswordUtil.encode("Password123"));
        user.setRole("USER");
        user.setStatus(1);
        user.setEmail("test@example.com");

        when(redisTemplate.hasKey(SecurityConstant.REDIS_PREFIX_ACCOUNT_LOCK + "testuser"))
                .thenReturn(false);
        when(userMapper.selectByUsernameOrEmail("testuser")).thenReturn(user);
        when(jwtUtil.generateAccessToken(1L, "testuser", "USER")).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(1L)).thenReturn("refresh-token");

        LoginVO vo = authService.login(dto, "127.0.0.1");

        assertEquals("access-token", vo.getAccessToken());
        assertEquals("refresh-token", vo.getRefreshToken());
        assertEquals(86400L, vo.getExpiresIn());
        assertNotNull(vo.getUserInfo());
        verify(userMapper).updateById(any(User.class));
        verify(valueOperations).set(eq(RedisKey.USER_TOKEN + 1L), eq("access-token"), eq(86400L), eq(TimeUnit.SECONDS));
        verify(valueOperations).set(eq(RedisKey.USER_REFRESH_TOKEN + 1L), eq("refresh-token"), eq(604800L), eq(TimeUnit.SECONDS));
    }

    @Test
    void login_userNotFound_shouldThrow() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("missing");
        dto.setPassword("Password123");
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(userMapper.selectByUsernameOrEmail("missing")).thenReturn(null);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        assertThrows(BusinessException.class, () -> authService.login(dto, "127.0.0.1"));
    }

    @Test
    void login_wrongPassword_shouldThrow() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("WrongPass1");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword(PasswordUtil.encode("Password123"));
        user.setStatus(1);
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(userMapper.selectByUsernameOrEmail("testuser")).thenReturn(user);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        assertThrows(BusinessException.class, () -> authService.login(dto, "127.0.0.1"));
    }

    @Test
    void sendEmailCode_alreadySent_shouldThrow() {
        EmailCodeDTO dto = new EmailCodeDTO();
        dto.setEmail("test@example.com");
        dto.setType("register");
        when(valueOperations.get(RedisKey.EMAIL_CODE + "register:test@example.com"))
                .thenReturn("123456");

        assertThrows(BusinessException.class, () -> authService.sendEmailCode(dto));
        verify(emailService, never()).sendVerificationCode(anyString(), anyString(), anyString());
    }

    @Test
    void logout_shouldDeleteToken() {
        when(jwtUtil.validateToken("token")).thenReturn(true);
        when(jwtUtil.getUserIdFromToken("token")).thenReturn(1L);

        authService.logout("token");

        verify(redisTemplate).delete(RedisKey.USER_TOKEN + 1L);
        verify(redisTemplate).delete(RedisKey.USER_REFRESH_TOKEN + 1L);
    }

    private UserRegisterDTO buildRegisterDto() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setPassword("Password123");
        dto.setEmail("test@example.com");
        dto.setCode("123456");
        dto.setNickname("测试用户");
        dto.setDirection("backend");
        return dto;
    }
}
