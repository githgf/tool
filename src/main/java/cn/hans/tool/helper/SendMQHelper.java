package cn.hans.tool.helper;

import cn.hans.common.mq.ExpireMsgProcessor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendMQHelper {

    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendTest(){
        amqpTemplate.convertAndSend("amq.topic","*.topic","test hello rabbitmq");
    }

    public void sendExpireMessage(){
        amqpTemplate.convertSendAndReceive("amq.topic","*.topic","test hello rabbitmq",new ExpireMsgProcessor(1231L));
    }

    @RabbitListener(queues = {"hgf"})
    public void listen(Message message){
        System.out.println("hgf队列接受到消息");
        System.out.println("message body"+ message.getBody());

        System.out.println("message head"+ message.getMessageProperties());
    }


}
