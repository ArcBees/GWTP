package com.gwtplatform.carstore.server.dao.objectify;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.gwtplatform.carstore.server.dao.domain.Car;
import com.gwtplatform.carstore.server.dao.domain.CarProperties;
import com.gwtplatform.carstore.server.dao.domain.Manufacturer;
import com.gwtplatform.carstore.server.dao.domain.Rating;
import com.gwtplatform.carstore.server.dao.domain.User;
import com.gwtplatform.carstore.server.dao.domain.UserSession;

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
