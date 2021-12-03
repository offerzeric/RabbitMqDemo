package com.eric.cosumerdemo.config;

import com.eric.cosumerdemo.listener.ChannelAware;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.ErrorHandler;

import java.io.IOException;
import java.util.Arrays;

/**
 * description
 *
 * @author ericzhang 2021/12/01 12:41 下午
 */
@Configuration
public class Configure {
    private Logger logger = LoggerFactory.getLogger("Configure");


    @Autowired
    @Qualifier("ChannelAware")
    private ChannelAware channelAware;
    @Autowired
    @Qualifier("createConnectionFactory")
    @Lazy
    private ConnectionFactory createConnectionFactory;

    @Value("${spring.rabbitmq.username}")
    String username;

    @Value("${spring.rabbitmq.password}")
    private String password;


    @Bean(name = "createConnectionFactory")
    public ConnectionFactory createConnectionFactory(){
        CachingConnectionFactory localhost = new CachingConnectionFactory("localhost");
        localhost.setUsername(username);
        localhost.setPassword(password);
        localhost.setVirtualHost("/eric");
        return localhost;

    }
    @Bean(name = "createListenerContainer")
    public MessageListenerContainer createListenerContainer(ConnectionFactory connectionFactory){
        DirectMessageListenerContainer directMessageListenerContainer = new DirectMessageListenerContainer(createConnectionFactory);
        directMessageListenerContainer.setupMessageListener(channelAware);
        directMessageListenerContainer.setQueueNames("eric_queue");
        //这里除了yml文件要加上manual声明 这里也要
        directMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        directMessageListenerContainer.setPrefetchCount(1);
        directMessageListenerContainer.setErrorHandler((Throwable t) -> {
                logger.error(t.getMessage() +"   stack trace:  " + Arrays.toString(t.getStackTrace()));
                throw new RuntimeException(t);
            });
        return directMessageListenerContainer;
    }

}
