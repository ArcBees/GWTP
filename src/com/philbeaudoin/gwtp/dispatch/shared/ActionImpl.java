package com.philbeaudoin.gwtp.dispatch.shared;

/**
 * An {@link Action} that uses the standard service name {@code "dispatch"}.
 * Inherit from this class if you're fine with the deault service name.
 * 
 * @author Philippe Beaudoin
 *
 * @param <R> The {@link Result} type.
 */
public class ActionImpl<R extends Result> implements Action<R> {

  private static final long serialVersionUID = -6235632606539498934L;
  public static final String DEFAULT_SERVICE_NAME = "dispatch/";

  @Override
  public String getServiceName() {
    return DEFAULT_SERVICE_NAME;
  }
}
