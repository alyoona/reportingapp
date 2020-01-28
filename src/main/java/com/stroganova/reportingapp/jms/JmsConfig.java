package com.stroganova.reportingapp.jms;


import org.apache.activemq.RedeliveryPolicy;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQConnectionFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@Configuration
public class JmsConfig {
    @Bean
    public ActiveMQConnectionFactoryCustomizer configureRedeliveryPolicy() {
        return connectionFactory ->
        {
            RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
            // configure redelivery policy
            redeliveryPolicy.setMaximumRedeliveries(1);
            connectionFactory.setRedeliveryPolicy(redeliveryPolicy);
        };
    }
}
