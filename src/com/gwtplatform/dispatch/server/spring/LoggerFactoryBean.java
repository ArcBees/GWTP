package com.gwtplatform.dispatch.server.spring;

import org.springframework.beans.factory.FactoryBean;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class LoggerFactoryBean implements FactoryBean {

    private final Logger logger;

    public LoggerFactoryBean(java.util.logging.Logger logger,
            List<Handler> handlers) {
        this.logger = logger;
        for (Handler handler : handlers) {
            logger.addHandler(handler);
        }
    }

    @Override
    public Object getObject() throws Exception {
        return logger;
    }

    @Override
    public Class getObjectType() {
        return Logger.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
