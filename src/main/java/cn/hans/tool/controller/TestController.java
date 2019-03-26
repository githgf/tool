package cn.hans.tool.controller;

import cn.hans.tool.helper.SendMQHelper;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("/test")
@RestController
public class TestController {

    @Resource
    SendMQHelper sendMQHelper;

    @GetMapping("/sendRabbitmq")
    @ApiOperation("测试发送rabbitmq消息")
    public void sendTestRabbitMq(){
        sendMQHelper.sendTest();
    }

}
