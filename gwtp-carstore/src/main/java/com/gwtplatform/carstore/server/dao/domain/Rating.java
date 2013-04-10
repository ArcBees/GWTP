package com.gwtplatform.carstore.server.dao.domain;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.gwtplatform.carstore.server.dao.objectify.Deref;
import com.gwtplatform.carstore.shared.domain.BaseEntity;
import com.gwtplatform.carstore.shared.dto.RatingDto;

@Index
@Entity
public class Rating extends BaseEntity {
    public static List<RatingDto> createDto(List<Rating> ratings) {
        if (ratings == null) {
            return null;
        }
        
        List<RatingDto> ratingsDto = new ArrayList<RatingDto>();
        for (Rating rating : ratings) {
            ratingsDto.add(createDto(rating));
        }
        
        return ratingsDto;
    }
    
    public static RatingDto createDto(Rating rating) {
        if (rating == null) {
            return null;
        }
        
        RatingDto ratingDto = new RatingDto();
        ratingDto.setCar(Car.createDto(rating.getCar()));
        ratingDto.setId(rating.getId());
        ratingDto.setRating(rating.getRating());
        
        return ratingDto;
    }
    
    public static Rating create(RatingDto ratingDto) {
        if (ratingDto == null) {
            return null;
        }
        
        Rating rating = new Rating();
        rating.setCar(Car.create(ratingDto.getCar()));
        rating.setId(ratingDto.getId());
        rating.setRating(ratingDto.getRating());
        
        return rating;
    }
    
    private Integer rating;
    @Load
    private Ref<Car> car;

    public Rating() {
    }

    public Car getCar() {
        return Deref.deref(car);
    }

    public void setCar(Car car) {
        this.car = Ref.create(car);
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
