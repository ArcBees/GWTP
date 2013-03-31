package com.gwtplatform.carstore.server.dispatch;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.dao.CarDao;
import com.gwtplatform.carstore.shared.dispatch.DeleteCarAction;
import com.gwtplatform.carstore.shared.dispatch.NoResults;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class DeleteCarHandler extends AbstractActionHandler<DeleteCarAction, NoResults> {
    private final CarDao carDao;

    @Inject
    public DeleteCarHandler(final CarDao carDao) {
        super(DeleteCarAction.class);

        this.carDao = carDao;
    }

    @Override
    public NoResults execute(DeleteCarAction action, ExecutionContext context) throws ActionException {
        carDao.delete(action.getCar());

        return new NoResults();
    }
}
