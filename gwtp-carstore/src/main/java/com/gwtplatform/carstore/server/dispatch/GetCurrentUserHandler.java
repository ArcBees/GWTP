package com.gwtplatform.carstore.server.dispatch;

import com.google.inject.Inject;
import com.gwtplatform.carstore.server.authentication.CurrentUserDtoProvider;
import com.gwtplatform.carstore.shared.dispatch.GetCurrentUserAction;
import com.gwtplatform.carstore.shared.dispatch.GetCurrentUserResult;
import com.gwtplatform.carstore.shared.dto.CurrentUserDto;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class GetCurrentUserHandler extends AbstractActionHandler<GetCurrentUserAction, GetCurrentUserResult> {
    private final CurrentUserDtoProvider currentUserDtoProvider;

    @Inject
    public GetCurrentUserHandler(final CurrentUserDtoProvider currentUserDtoProvider) {
        super(GetCurrentUserAction.class);

        this.currentUserDtoProvider = currentUserDtoProvider;
    }

    @Override
    public GetCurrentUserResult execute(GetCurrentUserAction action, ExecutionContext context) throws ActionException {
        CurrentUserDto currentUserDto = currentUserDtoProvider.get();

        return new GetCurrentUserResult(currentUserDto);
    }
}
