package com.nailong.service;

/**
 * @brief 邮件服务接口
 * @author Nailong
 */
public interface IEmailService {
    
    /**
     * @brief 发送验证码邮件
     * @param to   收件人邮箱
     * @param code 验证码
     * @param type 验证码类型（register/reset 等）
     */
    void sendVerificationCode(String to, String code, String type);
    
    /**
     * @brief 发送普通文本邮件
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件正文
     */
    void sendSimpleMail(String to, String subject, String content);
}
