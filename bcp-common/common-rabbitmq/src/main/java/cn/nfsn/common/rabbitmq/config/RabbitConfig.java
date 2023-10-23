package cn.nfsn.common.rabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: RabbitConfig
 * @Description: RabbitMQ的配置类，用于初始化RabbitMQ的消息监听容器工厂。
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-20 15:34:04
 **/
@Configuration
@Slf4j
public class RabbitConfig {

    /**
     * 创建并配置一个RabbitListenerContainerFactory实例。
     * 设置连接工厂以及消息转换器。消息转换器使用Jackson2JsonMessageConverter，
     * 可以将消费的消息自动转换为JSON格式。
     *
     * @param connectionFactory Spring Boot自动配置的ConnectionFactory。
     * @return 配置好的RabbitListenerContainerFactory。
     */
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }
}