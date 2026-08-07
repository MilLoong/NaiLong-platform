package com.nailong.security;

import com.nailong.common.enums.ResultCode;
import com.nailong.common.exception.BusinessException;
import com.nailong.mapper.UserMapper;
import com.nailong.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @brief 用户详情服务实现类
 * @details 用于 Spring Security 认证时加载用户信息
 * @author Nailong
 */
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserMapper userMapper;
    
    /**
     * @brief 根据用户名加载用户详情
     * @param username 用户名或邮箱
     * @return Spring Security UserDetails 对象
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户信息
        User user = userMapper.selectByUsernameOrEmail(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }
        
        // 构建UserDetails对象
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            true, // 账号是否启用
            true, // 账号是否过期
            true, // 密码是否过期
            true, // 账号是否锁定
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
