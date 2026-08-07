package com.nailong.service;

import com.nailong.common.exception.BusinessException;
import com.nailong.common.utils.RedisUtils;
import com.nailong.mapper.UserMapper;
import com.nailong.model.entity.User;
import com.nailong.model.vo.user.UserInfoVO;
import com.nailong.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @brief 用户服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private RedisUtils redisUtils;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = spy(new UserServiceImpl(userMapper, redisUtils));
    }

    @Test
    void getCurrentUserInfo_success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setNickname("昵称");
        user.setStatus(1);
        doReturn(user).when(userService).getById(1L);

        UserInfoVO vo = userService.getCurrentUserInfo(1L);

        assertEquals(1L, vo.getId());
        assertEquals("testuser", vo.getUsername());
        assertEquals("昵称", vo.getNickname());
    }

    @Test
    void getCurrentUserInfo_userNotFound_shouldThrow() {
        doReturn(null).when(userService).getById(99L);
        assertThrows(BusinessException.class, () -> userService.getCurrentUserInfo(99L));
    }

    @Test
    void getCurrentUserInfo_disabled_shouldThrow() {
        User user = new User();
        user.setId(1L);
        user.setStatus(0);
        doReturn(user).when(userService).getById(1L);

        assertThrows(BusinessException.class, () -> userService.getCurrentUserInfo(1L));
    }

    @Test
    void updateUserStatus_disable_shouldClearToken() {
        User user = new User();
        user.setId(1L);
        user.setStatus(1);
        doReturn(user).when(userService).getById(1L);
        doReturn(true).when(userService).updateById(any(User.class));

        userService.updateUserStatus(1L, 0);

        assertEquals(0, user.getStatus());
        verify(redisUtils).delete("user:token:1");
        verify(userService).updateById(user);
    }

    @Test
    void updateUserRole_success() {
        User user = new User();
        user.setId(1L);
        user.setRole("USER");
        doReturn(user).when(userService).getById(1L);
        doReturn(true).when(userService).updateById(any(User.class));

        userService.updateUserRole(1L, "ADMIN");

        assertEquals("ADMIN", user.getRole());
        verify(userService).updateById(user);
    }
}
