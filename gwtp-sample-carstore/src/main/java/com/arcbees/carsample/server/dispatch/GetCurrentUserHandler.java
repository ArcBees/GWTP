package com.arcbees.carsample.server.dispatch;

import com.arcbees.carsample.server.authentication.CurrentUserDtoProvider;
import com.arcbees.carsample.shared.dispatch.GetCurrentUserAction;
import com.arcbees.carsample.shared.dispatch.GetCurrentUserResult;
import com.arcbees.carsample.shared.dto.CurrentUserDto;
import com.google.inject.Inject;
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
