package com.gwtplatform.dispatch.rpc.client;

import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.Result;

public class DefaultRpcDispatchHooks implements RpcDispatchHooks {
    @Override
    public void onExecute(Action action) {
    }

    @Override
    public void onSuccess(Action action, Result result) {
    }

    @Override
    public void onFailure(Action action, Result result) {
    }
}
