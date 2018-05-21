package com.nexus.website.impl.controller;


import com.nexus.website.impl.dto.ResponseText;
import com.nexus.website.impl.dto.request.MailRequest;
import com.nexus.website.impl.dto.response.BaseResponse;
import com.nexus.website.impl.service.IMailService;
import com.nexus.website.impl.utils.RespPackUtil;
import com.nexus.website.impl.utils.ServiceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.io.UnsupportedEncodingException;

/**
 * 邮件控制器
 * Created by caishufan on 2018.03.08
 **/
@Controller
@RequestMapping("/email")
public class MailController {
    private Logger log = LoggerFactory.getLogger(MailController.class);
    @Autowired
    IMailService mailService;
    /**
     * 发送邮件
     */
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse sendMail(@RequestBody final MailRequest request){
        return RespPackUtil.execInvokeService(request, new BaseResponse(), new ServiceWrapper() {
            @Override
            public String invokeService() throws UnsupportedEncodingException, MessagingException {
                String emailMsg = "测试发送邮件";
                BaseResponse baseResponse = new BaseResponse();
                baseResponse = mailService.sendHtmlMail("nexusinfo@nexusest.com", "官网用户反馈", request);
                return baseResponse.getCode();
            }
        });
    }
}
