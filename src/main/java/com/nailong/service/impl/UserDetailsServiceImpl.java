package com.nailong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nailong.common.constant.SecurityConstant;
import com.nailong.exception.AuthException;
import com.nailong.mapper.UserMapper;
import com.nailong.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @brief Spring Security 用户详情服务实现
 * @details 实现 UserDetailsService，加载用户认证信息与权限
 * @author Nailong
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    /**
     * @brief 根据用户名加载用户详情
     * @param username 用户名
     * @return Spring Security UserDetails
     * @throws UsernameNotFoundException 用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUsername(username);
        checkUserStatus(user);
        return buildUserDetails(user);
    }

    /**
     * @brief 根据用户名查询用户
     * @param username 用户名
     * @return 用户实体
     * @throws UsernameNotFoundException 用户不存在
     */
    private User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }

        return user;
    }

    /**
     * @brief 检查用户状态是否可用
     * @param user 用户实体
     * @throws AuthException 账户已禁用
     */
    private void checkUserStatus(User user) {
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw AuthException.userDisabled();
        }
    }

    /**
     * @brief 构建 Spring Security UserDetails
     * @param user 用户实体
     * @return UserDetails 对象
     */
    private UserDetails buildUserDetails(User user) {
        List<String> authorityStrings = getUserAuthorities(user);

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String authority : authorityStrings) {
            authorities.add(new SimpleGrantedAuthority(authority));
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    /**
     * @brief 获取用户权限列表
     * @param user 用户实体
     * @return 权限字符串列表
     * @details 所有用户拥有 ROLE_USER；管理员额外拥有 ROLE_ADMIN
     */
    private List<String> getUserAuthorities(User user) {
        List<String> authorities = new ArrayList<>();

        String role = user.getRole();
        if (role == null) {
            role = SecurityConstant.ROLE_USER;
        }

        authorities.add(SecurityConstant.ROLE_PREFIX + SecurityConstant.ROLE_USER);

        if (SecurityConstant.ROLE_ADMIN.equals(role)) {
            authorities.add(SecurityConstant.ROLE_PREFIX + SecurityConstant.ROLE_ADMIN);
        }

        return authorities;
    }

    /**
     * @brief 根据用户 ID 加载用户详情
     * @param userId 用户 ID
     * @return Spring Security UserDetails
     * @throws UsernameNotFoundException 用户不存在
     */
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        User user = userMapper.selectById(userId);

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在，ID：" + userId);
        }

        checkUserStatus(user);
        return buildUserDetails(user);
    }

    /**
     * @brief 获取当前登录用户
     * @return 当前用户实体
     * @throws AuthException 未登录
     */
    public User getCurrentUser() {
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal() instanceof String) {
            throw AuthException.authenticationFailed("用户未登录");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        return getUserByUsername(username);
    }

    /**
     * @brief 检查当前用户是否为管理员
     * @return 是否为管理员
     */
    public boolean isAdmin() {
        try {
            User user = getCurrentUser();
            return user.getRole() != null && SecurityConstant.ROLE_ADMIN.equals(user.getRole());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @brief 刷新用户权限
     * @param userId 用户 ID
     */
    public void refreshUserAuthorities(Long userId) {
    }
}
