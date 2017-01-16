package com.alext.monitor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
        context.getBean(TransactionsListener.class).run();
    }
}
