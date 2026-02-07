package com.cs.web.spring.helper.mail;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author sb
 * @date 2025/2/22 16:03
 */
@Slf4j
public class SpringMailHelper {

    private JavaMailSenderImpl javaMailSender;

    public SpringMailHelper(JavaMailSenderImpl javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void send(String to, String subject, String html) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(javaMailSender.getUsername());
        helper.setTo(to);
        helper.setSubject(subject);
        // 第二个参数 `true` 表示发送 HTML 内容
        helper.setText(html, true);
        javaMailSender.send(mimeMessage);
        log.info("Mail-ok {}, {}, {}", to, subject, StringUtils.truncate(html, 50));
    }
}
