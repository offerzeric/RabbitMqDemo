package com.eric.cosumerdemo;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 *
 * @author ericzhang 2021/11/30 9:49 下午
 */
@Component
public class ConsumerListener {
    private static final String QUEUE_NAME = "eric_queue";
    private static final String QUEUE_DLX_NAME = "eric_dlx_queue";

    private Logger logger = LoggerFactory.getLogger("consumerListener");

    @RabbitListener(queues = ConsumerListener.QUEUE_NAME)
    public void ListenerQueue(Message message){
//        byte[] body = message.getBody();
        logger.info(message.toString());
//        String s = new String(body, StandardCharsets.UTF_8);
    }

    @RabbitListener(queues = ConsumerListener.QUEUE_DLX_NAME)
    public void ListenerDlxQueue(Message message){
//        byte[] body = message.getBody();
        logger.info(message.toString());
//        String s = new String(body, StandardCharsets.UTF_8);
    }
}
