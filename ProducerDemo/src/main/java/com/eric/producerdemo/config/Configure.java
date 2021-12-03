package com.eric.producerdemo.config;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.impl.AMQImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * description
 *
 * @author ericzhang 2021/11/30 10:00 下午
 */
@Configuration
public class Configure {

    private static final String EXCHANGE_NAME = "eric_exchange";
    private static final String EXCHANGE_DLX_NAME = "eric_dlx_exchange";
    private static final String QUEUE_NAME = "eric_queue";
    private static final String QUEUE_DLX_NAME = "eric_dlx_queue";
    private static final String BIND_NAME = "eric_bind";
    private static final String BIND_DLX_NAME = "eric_dlx_bind";
    private static final String BIND_DLX_ROUTING_KEY= "dlx";
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private Logger logger = LoggerFactory.getLogger("Configure");

    @Bean(name = Configure.EXCHANGE_DLX_NAME)
    public Exchange bootDlxExchange(){
        ExchangeBuilder eric_exchange_dlx = ExchangeBuilder.directExchange(Configure.EXCHANGE_DLX_NAME).durable(true);
        return eric_exchange_dlx.build();
    }

    @Bean(name = Configure.EXCHANGE_NAME)
    public Exchange bootExchange(){
        ExchangeBuilder eric_exchangeBuilder = ExchangeBuilder.fanoutExchange(Configure.EXCHANGE_NAME).durable(true);
        return eric_exchangeBuilder.build();
    }

    @Bean(name = Configure.QUEUE_DLX_NAME)
    public Queue bootDlxChannel(){
        Queue eric_dlx_queue = QueueBuilder.durable(Configure.QUEUE_DLX_NAME).build();
        return eric_dlx_queue;
    }

    @Bean(name = Configure.QUEUE_NAME)
    public Queue bootChannel(){
        Map<String,Object> param  = new HashMap<>();
//        param.put("x-message-ttl",30000);
//        Queue eric_queue = QueueBuilder.durable(Configure.QUEUE_NAME).ttl(100).withArguments(param).build();
        //使用queue 本身的ttl进行声明
        param.put("x-dead-letter-exchange",Configure.EXCHANGE_DLX_NAME);
        param.put("x-dead-letter-routing-key",Configure.BIND_DLX_ROUTING_KEY);
        Queue eric_queue = QueueBuilder.durable(Configure.QUEUE_NAME).ttl(10000).withArguments(param).build();
        return eric_queue;
    }

    @Bean(value = Configure.BIND_NAME)
    public Binding createBinding(@Qualifier(Configure.EXCHANGE_NAME) Exchange exchange, @Qualifier(Configure.QUEUE_NAME) Queue queue){
        Map<String,Object> param  = new HashMap<>();
        param.put("x-message-ttl",10000);
        Binding noargs = BindingBuilder.bind(queue).to(exchange).with("").and(param);
        return noargs;
    }

    @Bean(value = Configure.BIND_DLX_NAME)
    public Binding createDlxBinding(@Qualifier(Configure.EXCHANGE_DLX_NAME) Exchange exchange, @Qualifier(Configure.QUEUE_DLX_NAME) Queue queue){
        Binding noargs = BindingBuilder.bind(queue).to(exchange).with("dlx").noargs();
        return noargs;
    }

    @Bean
    public void configureTask(){
        //消息从producer到exchange都会返回这个confirmCallback
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if(!ack) {
                    logger.info("msg is not sent cause " + cause + "correlationData  Return msg is " + correlationData.getId());
                }else {
                    logger.info("msg is sent");
                }
            }
        });
        //消息从exchange到queue如果失败了并且开启了mandotary就会
//        rabbitTemplate.setMandatory(true);
//        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
//            @Override
//            public void returnedMessage(ReturnedMessage returned) {
//                logger.error("msg returned in returned callback" + returned.getMessage());
//            }
//        });
        rabbitTemplate.setMessageConverter(new MessageConverter() {
            @Override
            public Message toMessage(Object object, MessageProperties messageProperties) throws
                    MessageConversionException {
                String s = String.valueOf(object);
                byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
                messageProperties.setContentEncoding("UTF-8");
                messageProperties.setContentLength(s.length());
                messageProperties.setContentType(MessageProperties.DEFAULT_CONTENT_TYPE);
                messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
//                messageProperties.setExpiration("2000"); //设置单个消息的过期时间
                return new Message(bytes,messageProperties);
            }

            @Override
            public Object fromMessage(Message message) throws MessageConversionException {
                byte[] body = message.getBody();
                String s = (String) JSON.parse(new String(body, StandardCharsets.UTF_8));
                return s;
            }
        });
    }
}
