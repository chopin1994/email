package com.nexus.website.test.controller;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;

@WebAppConfiguration
@ContextConfiguration(locations = { "classpath*:/testContext.xml", "classpath*:config/spring-mvc.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class BaseControllerTest {
//    private static ServiceFactory factory = ServiceFactory.getInstanceWithPath("D:\\taobao-tomcat-7.0.59\\deploy");

    @Autowired
    private WebApplicationContext wac;
    protected MockMvc mockMvc;
    protected Cookie cookie;

    @Before
    public void setup() throws InterruptedException {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
}
