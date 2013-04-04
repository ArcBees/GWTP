package com.gwtplatform.carstore.server;

import java.util.Date;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.authentication.PasswordSecurity;
import com.gwtplatform.carstore.server.dao.CarDao;
import com.gwtplatform.carstore.server.dao.ManufacturerDao;
import com.gwtplatform.carstore.server.dao.RatingDao;
import com.gwtplatform.carstore.server.dao.UserDao;
import com.gwtplatform.carstore.server.dao.domain.Car;
import com.gwtplatform.carstore.server.dao.domain.Manufacturer;
import com.gwtplatform.carstore.server.dao.domain.Rating;
import com.gwtplatform.carstore.server.dao.domain.User;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.carstore.shared.dto.CarPropertiesDto;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.carstore.shared.dto.RatingDto;
import com.gwtplatform.carstore.shared.dto.UserDto;

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
        
        init();
    }

    public void init() {
        long userCount = userDao.countAll();

        if (userCount == 0) {
            createBasicUser();
        }

        createMockData();
    }

    private void createBasicUser() {
        UserDto userDto = new UserDto("admin", passwordSecurity.hashPassword("qwerty"), "FirstName", "LastName");
        userDao.put(User.create(userDto));
    }


    private void createMockData() {
        long manufacturerCount = manufacturerDao.countAll();

        if (1==1) {
            ManufacturerDto honda = new ManufacturerDto("Honda");
            ManufacturerDto mitsubishi = new ManufacturerDto("Mitsubishi");

            honda = Manufacturer.createDto(manufacturerDao.put(Manufacturer.create(honda)));
            mitsubishi = Manufacturer.createDto(manufacturerDao.put(Manufacturer.create(mitsubishi)));

            CarDto civic = new CarDto("Civic", honda, new CarPropertiesDto("Cat", 0, new Date()));
            CarDto accord = new CarDto("Accord", honda, new CarPropertiesDto("Fish", 1, new Date()));
            CarDto lancer = new CarDto("Lancer", mitsubishi, new CarPropertiesDto("Dog", 2, new Date()));
            CarDto galant = new CarDto("Galant", mitsubishi, new CarPropertiesDto("Cow", 3, new Date()));

            civic = Car.createDto(carDao.put(Car.create(civic))); 
            accord = Car.createDto(carDao.put(Car.create(accord)));
            lancer = Car.createDto(carDao.put(Car.create(lancer)));
            galant = Car.createDto(carDao.put(Car.create(galant)));

            RatingDto rating1 = new RatingDto(accord, 4);
            RatingDto rating2 = new RatingDto(civic, 2);
            RatingDto rating3 = new RatingDto(galant, 3);
            RatingDto rating4 = new RatingDto(lancer, 4);

            ratingDao.put(Rating.create(rating1));
            ratingDao.put(Rating.create(rating2));
            ratingDao.put(Rating.create(rating3));
            ratingDao.put(Rating.create(rating4));

            for (int i = 0; i < 100; ++i) {
                CarDto carDto = new CarDto("MyCar" + i, honda, new CarPropertiesDto("MyCarProperty", i, new Date()));
                carDto = Car.createDto(carDao.put(Car.create(carDto)));
                RatingDto ratingDto = new RatingDto(carDto, i % 10);
                ratingDao.put(Rating.create(ratingDto));
            }
        }
    }
}
