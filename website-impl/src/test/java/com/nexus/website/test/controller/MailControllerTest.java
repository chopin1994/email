package com.nexus.website.test.controller;

import com.nexus.website.impl.dto.response.BaseResponse;
import com.nexus.website.impl.dto.request.MailRequest;
import com.nexus.website.impl.utils.FastJSONHelper;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MailControllerTest extends BaseControllerTest {

    @Test
    public void testSendMail() throws Exception {
        MailRequest request = new MailRequest();
        request.setName("chopin");
        request.setEmail("xxx@qq.com");
        request.setPhone("18888888888");
        request.setMessage("hello! ");
        String req = FastJSONHelper.serialize(request);
        ResultActions results = mockMvc.perform(post("/email/send").content(req)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status
                        ().isOk());
        results.andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                BaseResponse responseText = FastJSONHelper.deserialize(result.getResponse().getContentAsString()
                        , BaseResponse.class);
                System.out.println(FastJSONHelper.serialize(responseText));
            }
        });
    }
}
