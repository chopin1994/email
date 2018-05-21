package com.nexus.website.impl.service;

import com.nexus.website.impl.dto.request.MailRequest;
import com.nexus.website.impl.dto.response.BaseResponse;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface IMailService {
    //发送邮件
    BaseResponse sendHtmlMail(String to, String subject, MailRequest request) throws MessagingException, UnsupportedEncodingException;
}
