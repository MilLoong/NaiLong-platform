package com.nailong.service.impl;

import com.nailong.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @brief 邮件服务实现
 * @details 基于 Spring Mail 发送验证码及普通邮件
 * @author Nailong
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * @brief 发送邮箱验证码
     * @param to   收件人邮箱
     * @param code 验证码
     * @param type 验证码类型（register/reset）
     */
    @Override
    public void sendVerificationCode(String to, String code, String type) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);

            if ("register".equals(type)) {
                message.setSubject("凌睿工作室招新平台 - 注册验证码");
                message.setText(buildRegisterVerificationContent(code));
            } else if ("reset".equals(type)) {
                message.setSubject("凌睿工作室招新平台 - 密码重置验证码");
                message.setText(buildResetPasswordVerificationContent(code));
            } else {
                throw new IllegalArgumentException("无效的验证码类型: " + type);
            }

            mailSender.send(message);
            log.info("验证码邮件发送成功: to={}, type={}", to, type);
        } catch (MailException e) {
            log.error("验证码邮件发送失败: to={}, type={}, error={}", to, type, e.getMessage(), e);
            throw new RuntimeException("邮件发送失败，请稍后重试", e);
        }
    }

    /**
     * @brief 发送普通文本邮件
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件正文
     */
    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            log.info("普通邮件发送成功: to={}, subject={}", to, subject);
        } catch (MailException e) {
            log.error("普通邮件发送失败: to={}, subject={}, error={}", to, subject, e.getMessage(), e);
            throw new RuntimeException("邮件发送失败，请稍后重试", e);
        }
    }

    /**
     * @brief 构建注册验证码邮件正文
     * @param code 验证码
     * @return 邮件正文
     */
    private String buildRegisterVerificationContent(String code) {
        StringBuilder sb = new StringBuilder();
        sb.append("您好！\n\n");
        sb.append("感谢您注册凌睿工作室招新平台。您的验证码是：\n\n");
        sb.append("【").append(code).append("】\n\n");
        sb.append("请在5分钟内完成注册，验证码过期后需要重新获取。\n\n");
        sb.append("如果您没有进行注册操作，请忽略此邮件。\n\n");
        sb.append("凌睿工作室招新平台\n");
        return sb.toString();
    }

    /**
     * @brief 构建密码重置验证码邮件正文
     * @param code 验证码
     * @return 邮件正文
     */
    private String buildResetPasswordVerificationContent(String code) {
        StringBuilder sb = new StringBuilder();
        sb.append("您好！\n\n");
        sb.append("您正在请求重置凌睿工作室招新平台的账户密码。您的验证码是：\n\n");
        sb.append("【").append(code).append("】\n\n");
        sb.append("请在5分钟内完成密码重置，验证码过期后需要重新获取。\n\n");
        sb.append("如果您没有请求重置密码，请立即联系管理员。\n\n");
        sb.append("凌睿工作室招新平台\n");
        return sb.toString();
    }
}
