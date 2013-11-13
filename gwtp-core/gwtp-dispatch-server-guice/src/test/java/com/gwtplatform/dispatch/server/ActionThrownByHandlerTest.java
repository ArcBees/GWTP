package com.gwtplatform.dispatch.server;

import javax.inject.Inject;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gwtplatform.dispatch.server.guice.DispatchServiceImpl;
import com.gwtplatform.dispatch.server.guice.actionvalidator.DefaultActionValidator;
import com.gwtplatform.dispatch.shared.ActionException;
import com.gwtplatform.dispatch.shared.ServiceException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


@RunWith(JukitoRunner.class)
public class ActionThrownByHandlerTest {
    public static class MyModule extends JukitoModule {
        @Override
        protected void configureTest() {
            install(new ServiceModule(DefaultActionValidator.class));
        }
    }

    @Inject
    DispatchServiceImpl service;

    @Test
    public void exceptionThrownByHandlerIsNotWrappedInActionException() throws ServiceException {
        try {
            service.execute("", new SomeAction());
            fail();
        } catch (ActionException e) {
            assertThat(e, instanceOf(ActionExceptionThrownByHandler.class));
            assertEquals(0, e.getStackTrace().length);
        }
    }
}
