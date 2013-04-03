package com.gwtplatform.carstore.server.dao.objectify;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.gwtplatform.carstore.shared.domain.UserSession;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.carstore.shared.dto.CarPropertiesDto;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.carstore.shared.dto.UserDto;

/**
 * For use of:
 * import static com.sceneverse.shozon.server.dao.objectify.OfyService.ofy;
 */
public class OfyService {
    static {
        factory().register(CarDto.class);
        factory().register(CarPropertiesDto.class);
        factory().register(ManufacturerDto.class);
        factory().register(RatingDto.class);
        factory().register(UserDto.class);
        factory().register(UserSession.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
