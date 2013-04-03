package com.gwtplatform.carstore.server;

import java.util.Date;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.authentication.PasswordSecurity;
import com.gwtplatform.carstore.server.dao.CarDao;
import com.gwtplatform.carstore.server.dao.ManufacturerDao;
import com.gwtplatform.carstore.server.dao.RatingDao;
import com.gwtplatform.carstore.server.dao.UserDao;
import com.gwtplatform.carstore.shared.domain.Car;
import com.gwtplatform.carstore.shared.domain.CarProperties;
import com.gwtplatform.carstore.shared.domain.Manufacturer;
import com.gwtplatform.carstore.shared.domain.Rating;
import com.gwtplatform.carstore.shared.domain.User;

public class DevBootStrapper {
    private final UserDao userDao;
    private final PasswordSecurity passwordSecurity;
    private final ManufacturerDao manufacturerDao;
    private final CarDao carDao;
    private final RatingDao ratingDao;

    @Inject
    public DevBootStrapper(final UserDao userDao, final PasswordSecurity passwordSecurity,
            final ManufacturerDao manufacturerDao, CarDao carDao, RatingDao ratingDao) {
        this.userDao = userDao;
        this.passwordSecurity = passwordSecurity;
        this.manufacturerDao = manufacturerDao;
        this.carDao = carDao;
        this.ratingDao = ratingDao;
    }

    public void init() {
        long userCount = userDao.countAll();

        if (userCount == 0) {
            createBasicUser();
        }

        createMockData();
    }

    private void createBasicUser() {
        User user = new User("admin", passwordSecurity.hashPassword("qwerty"), "FirstName", "LastName");
        userDao.put(user);
    }


    private void createMockData() {
        long manufacturerCount = manufacturerDao.countAll();

        if (manufacturerCount == 0) {
            Manufacturer honda = new Manufacturer("Honda");
            Manufacturer mitsubishi = new Manufacturer("Mitsubishi");

            honda = manufacturerDao.put(honda);
            mitsubishi = manufacturerDao.put(mitsubishi);

            Car civic = new Car("Civic", honda, new CarProperties("Cat", 0, new Date()));
            Car accord = new Car("Accord", honda, new CarProperties("Fish", 1, new Date()));
            Car lancer = new Car("Lancer", mitsubishi, new CarProperties("Dog", 2, new Date()));
            Car galant = new Car("Galant", mitsubishi, new CarProperties("Cow", 3, new Date()));

            civic = carDao.put(civic);
            accord = carDao.put(accord);
            lancer = carDao.put(lancer);
            galant = carDao.put(galant);

            Rating rating1 = new Rating(accord, 4);
            Rating rating2 = new Rating(civic, 2);
            Rating rating3 = new Rating(galant, 3);
            Rating rating4 = new Rating(lancer, 4);

            ratingDao.put(rating1);
            ratingDao.put(rating2);
            ratingDao.put(rating3);
            ratingDao.put(rating4);

            for (int i = 0; i < 100; ++i) {
                Car car = new Car("MyCar" + i, honda, new CarProperties("MyCarProperty", i, new Date()));
                car = carDao.put(car);
                Rating rating = new Rating(car, i % 10);
                ratingDao.put(rating);
            }
        }
    }
}
