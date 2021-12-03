package com.eric.cosumerdemo.listener;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * description
 *
 * @author ericzhang 2021/12/01 6:21 下午
 */
@Component(value = "ChannelAware")
public class ChannelAware implements ChannelAwareMessageListener {

    private Logger logger = LoggerFactory.getLogger("ChannelAware");
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
            logger.info(message.getBody().toString()+"listener on!");
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), Boolean.TRUE);
            }catch (IOException e){
                logger.info("msg has error");
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),Boolean.TRUE,Boolean.TRUE);
            }
    }
}
