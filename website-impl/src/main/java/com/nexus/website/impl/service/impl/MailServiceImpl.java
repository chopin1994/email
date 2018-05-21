package com.nexus.website.impl.service.impl;

import com.nexus.website.impl.constants.ResultCodeConstant;
import com.nexus.website.impl.dto.ResponseText;
import com.nexus.website.impl.dto.request.MailRequest;
import com.nexus.website.impl.dto.response.BaseResponse;
import com.nexus.website.impl.service.IMailService;
import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Properties;

/**
 * Created by caishufan on 2018.03.08
 **/
@Service
public class MailServiceImpl implements IMailService {
    private static final String HOST = "smtp.exmail.qq.com";
    private static final Integer PORT = 465;
    private static final String USERNAME = "nexusinfo@nexusest.com";
    private static final String PASSWORD = "password";//企业邮箱用密码，个人邮箱用授权码
    private static final String EMAILFORM = "nexusinfo@nexusest.com";
    private JavaMailSenderImpl mailSender = createMailSender();

    /**
     * 邮件发送器
     * @return 配置好的工具
     */
    private JavaMailSenderImpl createMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        Properties props = new Properties();
        sender.setHost(HOST);
        sender.setPort(PORT);
        sender.setUsername(USERNAME);
        sender.setPassword(PASSWORD);
        sender.setDefaultEncoding("Utf-8");
        sender.setProtocol("smtp");

        Authenticator auth = new Email_Autherticator(); // 进行邮件服务器用户认证
        MailSSLSocketFactory sf = null;//ssl设置
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        sf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);
        props.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props, auth);
        sender.setSession(session);
        sender.setJavaMailProperties(props);
        return sender;
    }

    /**
     * 用来进行服务器对用户的认证
     */
    public class Email_Autherticator extends Authenticator {
        public Email_Autherticator() {
            super();
        }

        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(USERNAME, PASSWORD);
        }
    }

    /**
     * 发送邮件
     *
     * @param to 接受人
     * @param subject 主题
     * @param request 发送内容
     * @throws MessagingException 异常
     * @throws UnsupportedEncodingException 异常
     */
    @Override
    public BaseResponse sendHtmlMail(String to, String subject, MailRequest request) throws MessagingException, UnsupportedEncodingException {
        BaseResponse baseResponse = new BaseResponse();

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // 设置utf-8或GBK编码，否则邮件会有乱码
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        //发送者昵称
        messageHelper.setFrom(EMAILFORM, "官网");
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);

        StringBuffer html = new StringBuffer();
        html.append("<p>");
        html.append("name:").append(request.getName()).append("<br/>");
        html.append("email:").append(request.getEmail()).append("<br/>");
        html.append("phone:").append(request.getPhone()).append("<br/>");
        html.append("message:").append(request.getMessage()).append("<br/>");
        html.append("</p>");
        messageHelper.setText(html.toString(), true);

        try {
            mailSender.send(mimeMessage);
            baseResponse.setCode(ResultCodeConstant.SUCCESS_CODE);
            baseResponse.setMessage("邮件发送成功");
        } catch (Exception e) {
            baseResponse.setCode(ResultCodeConstant.FAIL_CODE);
            baseResponse.setMessage("邮件发送失败");
            e.printStackTrace();
        }
        return baseResponse;
    }
}