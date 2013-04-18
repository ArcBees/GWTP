package com.gwtplatform.carstore.server.guice;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.guice.GuiceResourceFactory;
import org.jboss.resteasy.plugins.server.servlet.FilterDispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;

import com.google.inject.Binding;
import com.google.inject.Injector;

@Singleton
public class GuiceRestEasyFilterDispatcher extends FilterDispatcher {
    private static final Logger logger = Logger.getLogger(GuiceRestEasyFilterDispatcher.class.getName());

    @Inject
    Injector injector;

    @Override
    public void init(FilterConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        ServletContext context = servletConfig.getServletContext();
        Registry registry = (Registry) context.getAttribute(Registry.class.getName());
        ResteasyProviderFactory providerFactory = (ResteasyProviderFactory) context.getAttribute
                (ResteasyProviderFactory.class.getName());

        for (final Binding<?> binding : injector.getBindings().values()) {
            final Type type = binding.getKey().getTypeLiteral().getType();
            if (type instanceof Class) {
                final Class<?> beanClass = (Class) type;
                if (GetRestful.isRootResource(beanClass)) {
                    final ResourceFactory resourceFactory = new GuiceResourceFactory(binding.getProvider(), beanClass);
                    logger.info("Registering factory for" + beanClass.getName());
                    registry.addResourceFactory(resourceFactory);
                }

                if (beanClass.isAnnotationPresent(Provider.class)) {
                    logger.info("Registering provider instance for" + beanClass.getName());
                    providerFactory.registerProviderInstance(binding.getProvider().get());
                }
            }
        }
    }
}
