package cn.hans.common.mq;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.support.Correlation;

public class ExpireMsgProcessor implements MessagePostProcessor {
    /**过期时间*/
    private Long expireTime;

    public ExpireMsgProcessor(Long expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        message.getMessageProperties().setExpiration(String.valueOf(expireTime));
        return message;
    }

    @Override
    public Message postProcessMessage(Message message, Correlation correlation) {
        return null;
    }
}
