package com.eric.producerdemo.service;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * description
 *
 * @author ericzhang 2021/11/30 10:58 下午
 */
@Service
public class SendService {

    private static final String EXCHANGE_NAME = "eric_exchange";
    private static final String QUEUE_NAME = "eric_queue";
    private Logger logger = LoggerFactory.getLogger("SendService");
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void basicSend(){
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration("100");
                return message;
            }
        };
        rabbitTemplate.convertAndSend(EXCHANGE_NAME,"","hahah",new CorrelationData("1"));
    }

}
