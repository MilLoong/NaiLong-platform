package com.nailong.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @brief 用户实体类
 * @author Nailong
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    private String username;  ///< 用户名
    private String password;  ///< 密码(BCrypt加密)
    private String email;  ///< 邮箱
    private String nickname;  ///< 昵称
    private String avatar;  ///< 头像URL
    private String phone;  ///< 手机号
    private String studentId;  ///< 学号
    private String realName;  ///< 真实姓名
    private String grade;  ///< 年级
    private String major;  ///< 专业
    private String direction;  ///< 招新方向
    private String role;  ///< 角色
    private Integer totalScore;  ///< 总积分
    private Integer solvedCount;  ///< 解题数量
    private Integer status;  ///< 账号状态 (0-禁用 1-正常)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastLoginTime;  ///< 最后登录时间
    private String lastLoginIp;  ///< 最后登录IP
}