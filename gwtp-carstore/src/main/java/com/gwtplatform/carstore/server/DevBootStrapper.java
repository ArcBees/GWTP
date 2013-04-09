package com.gwtplatform.carstore.server;

import java.util.Date;

import javax.inject.Inject;

import com.gwtplatform.carstore.server.authentication.PasswordSecurity;
import com.gwtplatform.carstore.server.dao.CarDao;
import com.gwtplatform.carstore.server.dao.CarPropertiesDao;
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
    private final CarPropertiesDao carPropertiesDao;

    @Inject
    public DevBootStrapper(UserDao userDao, PasswordSecurity passwordSecurity,
            final ManufacturerDao manufacturerDao, CarDao carDao, RatingDao ratingDao, CarPropertiesDao carPropertiesDao) {
        this.userDao = userDao;
        this.passwordSecurity = passwordSecurity;
        this.manufacturerDao = manufacturerDao;
        this.carDao = carDao;
        this.ratingDao = ratingDao;
        this.carPropertiesDao = carPropertiesDao;
        
        init();
    }

    public void init() {
        //deleteAllEntities();
        
        long userCount = userDao.countAll();

        if (userCount == 0) {
            createBasicUser();
        }

        createMockData();
    }

    private void deleteAllEntities() {
        userDao.deleteAll();
        manufacturerDao.deleteAll();
        carDao.deleteAll();
        ratingDao.deleteAll();
        carPropertiesDao.deleteAll();
    }

    private void createBasicUser() {
        UserDto userDto = new UserDto("admin", passwordSecurity.hashPassword("qwerty"), "FirstName", "LastName");
        userDao.put(User.create(userDto));
    }


    private void createMockData() {
        long manufacturerCount = manufacturerDao.countAll();

        if (manufacturerCount == 0) {
            ManufacturerDto honda = new ManufacturerDto("Honda");
            ManufacturerDto mitsubishi = new ManufacturerDto("Mitsubishi");

            honda = Manufacturer.createDto(manufacturerDao.put(Manufacturer.create(honda)));
            mitsubishi = Manufacturer.createDto(manufacturerDao.put(Manufacturer.create(mitsubishi)));

            CarPropertiesDto carPropertiesCivic = new CarPropertiesDto("Cat", 0, new Date());
            carPropertiesCivic = carPropertiesDao.put(carPropertiesCivic);
            
            CarPropertiesDto carPropertiesAccord = new CarPropertiesDto("Fish", 1, new Date());
            carPropertiesAccord = carPropertiesDao.put(carPropertiesAccord);
            
            CarPropertiesDto carPropertiesLancer = new CarPropertiesDto("Dog", 2, new Date());
            carPropertiesLancer = carPropertiesDao.put(carPropertiesLancer);
            
            CarPropertiesDto carPropertiesMitsubishi = new CarPropertiesDto("Cow", 3, new Date());
            carPropertiesMitsubishi = carPropertiesDao.put(carPropertiesMitsubishi);
            
            CarDto civic = new CarDto("Civic", honda, carPropertiesCivic);
            CarDto accord = new CarDto("Accord", honda, carPropertiesAccord);
            CarDto lancer = new CarDto("Lancer", mitsubishi, carPropertiesLancer);
            CarDto galant = new CarDto("Galant", mitsubishi, carPropertiesMitsubishi);

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
                CarPropertiesDto carProperties = new CarPropertiesDto("MyCarProperty", i, new Date());
                carProperties = carPropertiesDao.put(carProperties);
                CarDto carDto = new CarDto("MyCar" + i, honda, carProperties);
                carDto = Car.createDto(carDao.put(Car.create(carDto)));
                RatingDto ratingDto = new RatingDto(carDto, i % 10);
                ratingDao.put(Rating.create(ratingDto));
            }
        }
    }
}
