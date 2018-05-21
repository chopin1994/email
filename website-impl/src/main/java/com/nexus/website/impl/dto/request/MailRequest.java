package com.nexus.website.impl.dto.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class MailRequest extends BaseRequest {
    String name;
    String email;
    @NotNull(message = "手机号码不能为空")
    @Length(min = 11, max = 11, message = "手机号码长度必须是11")
    @Pattern(regexp = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$", message = "手机号码格式不正确")
    String phone;
    String message;
}
