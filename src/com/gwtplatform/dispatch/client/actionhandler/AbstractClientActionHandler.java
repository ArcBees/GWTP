package com.gwtplatform.dispatch.client.actionhandler;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.Result;

public abstract class AbstractClientActionHandler<A extends Action<R>, R extends Result>
    implements ClientActionHandler<A, R> {

  private final Class<A> actionType;

  protected AbstractClientActionHandler(Class<A> actionType) {
    this.actionType = actionType;
  }

  public Class<A> getActionType() {
    return actionType;
  }
}
