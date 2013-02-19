package com.gwtplatform.dispatch.client;

import com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry;
import com.gwtplatform.dispatch.shared.SecurityCookieAccessor;

/**
 * Present for backward compatibility. This class will disappear in the next releases.
 * @deprecated please use RpcDispatchAsync instead
 */
@Deprecated
public class DefaultDispatchAsync extends RpcDispatchAsync {
    public DefaultDispatchAsync(ExceptionHandler exceptionHandler, SecurityCookieAccessor securityCookieAccessor,
            ClientActionHandlerRegistry registry) {
        super(exceptionHandler, securityCookieAccessor, registry);
    }
}
