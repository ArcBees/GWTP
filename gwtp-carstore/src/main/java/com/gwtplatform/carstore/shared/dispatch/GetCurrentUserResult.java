package com.gwtplatform.carstore.shared.dispatch;

import com.gwtplatform.carstore.shared.dto.CurrentUserDto;
import com.gwtplatform.dispatch.shared.Result;

public class GetCurrentUserResult implements Result {
    private CurrentUserDto currentUserDto;

    @SuppressWarnings("unused")
    protected GetCurrentUserResult() {
        // Needed for serialization
    }

    public GetCurrentUserResult(CurrentUserDto currentUserDto) {
        this.currentUserDto = currentUserDto;
    }

    public CurrentUserDto getCurrentUserDto() {
        return currentUserDto;
    }
}
