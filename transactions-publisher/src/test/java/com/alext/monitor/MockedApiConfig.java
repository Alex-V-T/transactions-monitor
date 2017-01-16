package com.alext.monitor;

import com.alext.monitor.storage.StorageApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Mockito.mock;

@Configuration
public class MockedApiConfig {
    @Bean
    public JmsPublisher jmsPublisher() {
        return mock(JmsPublisher.class);
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return mock(JmsTemplate.class);
    }

    @Bean
    public StorageApi storageApi(){
        return mock(StorageApi.class);
    }

    @Bean
    public TransactionsListener transactionsListener(){
        return new TransactionsListener();
    }

    @Bean
    public FieldsFilter fieldsFilter(){
        return new FieldsFilter("field1", "field1", "field1");
    }
}
