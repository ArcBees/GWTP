package com.arcbees.carsample.server.dao.objectify;

import com.arcbees.carsample.shared.domain.Car;
import com.arcbees.carsample.shared.domain.CarProperties;
import com.arcbees.carsample.shared.domain.Manufacturer;
import com.arcbees.carsample.shared.domain.Rating;
import com.arcbees.carsample.shared.domain.User;
import com.arcbees.carsample.shared.domain.UserSession;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

/**
 * For use of:
 * import static com.sceneverse.shozon.server.dao.objectify.OfyService.ofy;
 */
public class OfyService {
    static {
        factory().register(Car.class);
        factory().register(CarProperties.class);
        factory().register(Manufacturer.class);
        factory().register(Rating.class);
        factory().register(User.class);
        factory().register(UserSession.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
