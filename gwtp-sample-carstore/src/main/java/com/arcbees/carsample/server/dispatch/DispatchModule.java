package com.arcbees.carsample.server.dispatch;

import com.arcbees.carsample.server.authentication.LoggedInActionValidator;
import com.arcbees.carsample.shared.dispatch.DeleteCarAction;
import com.arcbees.carsample.shared.dispatch.DeleteManufacturerAction;
import com.arcbees.carsample.shared.dispatch.DeleteRatingAction;
import com.arcbees.carsample.shared.dispatch.GetAverageCarRatingByManufacturerAction;
import com.arcbees.carsample.shared.dispatch.GetCarsAction;
import com.arcbees.carsample.shared.dispatch.GetCarsCountAction;
import com.arcbees.carsample.shared.dispatch.GetCurrentUserAction;
import com.arcbees.carsample.shared.dispatch.GetManufacturerAction;
import com.arcbees.carsample.shared.dispatch.GetManufacturersAction;
import com.arcbees.carsample.shared.dispatch.GetRatingsAction;
import com.arcbees.carsample.shared.dispatch.LogInAction;
import com.arcbees.carsample.shared.dispatch.LogoutAction;
import com.arcbees.carsample.shared.dispatch.SaveCarAction;
import com.arcbees.carsample.shared.dispatch.SaveManufacturerAction;
import com.arcbees.carsample.shared.dispatch.SaveRatingAction;
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
