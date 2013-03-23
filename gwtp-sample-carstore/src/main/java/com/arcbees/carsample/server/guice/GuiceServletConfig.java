package com.arcbees.carsample.server.guice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.arcbees.carsample.shared.BootStrapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class GuiceServletConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        Injector injector = Guice.createInjector(new ServerModule(), new JpaPersistModule(getPersistenceUnit()),
                new DispatchServletModule());

        PersistService persistService = injector.getInstance(PersistService.class);
        persistService.start();

        BootStrapper bootStrapper = injector.getInstance(BootStrapper.class);
        bootStrapper.init();

        return injector;
    }

    private String getPersistenceUnit() {
        InputStream inputStream = getClass().getResourceAsStream("/database.properties");

        if (inputStream == null) {
            throw new RuntimeException();
        }

        try {
            Properties properties = new Properties();
            properties.load(inputStream);

            return properties.getProperty("persistenceUnit");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
