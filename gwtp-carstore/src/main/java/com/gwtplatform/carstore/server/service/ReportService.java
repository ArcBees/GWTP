package com.gwtplatform.carstore.server.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import com.gwtplatform.carstore.shared.dto.ManufacturerRatingDto;
import com.gwtplatform.carstore.shared.dto.RatingDto;

public class ReportService {
    @Inject
    public ReportService() {
    }

    public List<ManufacturerRatingDto> getAverageCarRatings(List<RatingDto> ratingDtos) {
        HashMap<String, AveragingCounter> averages = new HashMap<String, AveragingCounter>();

        for (RatingDto ratingDto : ratingDtos) {
            String manufacturer = ratingDto.getCar().getManufacturer().getName();
            Double rating = Double.valueOf(ratingDto.getRating());

            if (averages.containsKey(manufacturer)) {
                averages.get(manufacturer).add(rating);
            } else {
                averages.put(manufacturer, new AveragingCounter(rating));
            }
        }

        List<ManufacturerRatingDto> results = new ArrayList<ManufacturerRatingDto>(averages.size());
        for (String manufacturer : averages.keySet()) {
            results.add(new ManufacturerRatingDto(manufacturer, averages.get(manufacturer).average()));
        }

        return results;
    }

    private class AveragingCounter {

        private double sum;

        private int count;

        AveragingCounter(double number) {
            this.sum = number;
            this.count = 1;
        }

        void add(double number) {
            this.sum += number;
            this.count++;
        }

        double average() {
            return count == 0 ? Double.NaN : sum / count;
        }
    }
}
