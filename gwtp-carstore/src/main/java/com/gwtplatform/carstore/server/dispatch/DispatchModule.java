package com.gwtplatform.carstore.server.dispatch;

import com.gwtplatform.carstore.server.authentication.LoggedInActionValidator;
import com.gwtplatform.carstore.shared.dispatch.DeleteCarAction;
import com.gwtplatform.carstore.shared.dispatch.DeleteManufacturerAction;
import com.gwtplatform.carstore.shared.dispatch.DeleteRatingAction;
import com.gwtplatform.carstore.shared.dispatch.GetAverageCarRatingByManufacturerAction;
import com.gwtplatform.carstore.shared.dispatch.GetCarsAction;
import com.gwtplatform.carstore.shared.dispatch.GetCarsCountAction;
import com.gwtplatform.carstore.shared.dispatch.GetCurrentUserAction;
import com.gwtplatform.carstore.shared.dispatch.GetManufacturerAction;
import com.gwtplatform.carstore.shared.dispatch.GetManufacturersAction;
import com.gwtplatform.carstore.shared.dispatch.GetRatingsAction;
import com.gwtplatform.carstore.shared.dispatch.LogInAction;
import com.gwtplatform.carstore.shared.dispatch.LogoutAction;
import com.gwtplatform.carstore.shared.dispatch.SaveCarAction;
import com.gwtplatform.carstore.shared.dispatch.SaveManufacturerAction;
import com.gwtplatform.carstore.shared.dispatch.SaveRatingAction;
import com.gwtplatform.dispatch.server.guice.HandlerModule;

public class DispatchModule extends HandlerModule {
    @Override
    protected void configureHandlers() {
        bindHandler(LogInAction.class, LogInHandler.class);
        bindHandler(LogoutAction.class, LogoutHandler.class);
        bindHandler(GetCurrentUserAction.class, GetCurrentUserHandler.class);

        bindHandler(GetManufacturersAction.class, GetManufacturersHandler.class, LoggedInActionValidator.class);
        bindHandler(GetManufacturerAction.class, GetManufacturerHandler.class, LoggedInActionValidator.class);
        bindHandler(SaveManufacturerAction.class, SaveManufacturerHandler.class, LoggedInActionValidator.class);
        bindHandler(DeleteManufacturerAction.class, DeleteManufacturerHandler.class, LoggedInActionValidator.class);

        bindHandler(GetCarsAction.class, GetCarsHandler.class, LoggedInActionValidator.class);
        bindHandler(GetCarsCountAction.class, GetCarsCountHandler.class, LoggedInActionValidator.class);
        bindHandler(SaveCarAction.class, SaveCarHandler.class, LoggedInActionValidator.class);
        bindHandler(DeleteCarAction.class, DeleteCarHandler.class, LoggedInActionValidator.class);

        bindHandler(GetRatingsAction.class, GetRatingsHandler.class, LoggedInActionValidator.class);
        bindHandler(SaveRatingAction.class, SaveRatingHandler.class, LoggedInActionValidator.class);
        bindHandler(DeleteRatingAction.class, DeleteRatingHandler.class, LoggedInActionValidator.class);

        bindHandler(GetAverageCarRatingByManufacturerAction.class, GetAverageCarRatingByManufacturerHandler.class,
                LoggedInActionValidator.class);
    }
}
