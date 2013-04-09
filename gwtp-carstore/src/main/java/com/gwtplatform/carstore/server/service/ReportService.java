package com.gwtplatform.carstore.server.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.gwtplatform.carstore.shared.dto.ManufacturerRatingDto;

public class ReportService {
    @Inject
    public ReportService() {
    }

    public List<ManufacturerRatingDto> getAverageCarRatingByManufacturer() {
        
//        TODO 
//        Query query = entityManager.createQuery(
//                "select new map(m.name as manufacturer, c.model as carModel, avg(r.rating) as rating)" +
//                        " from Manufacturer m" +
//                        " join m.cars as c" +
//                        " join c.ratings as r" +
//                        " group by m.name, c.model");
//
//        List resultList = query.getResultList();
//        HashMap<String, AveragingCounter> averages = new HashMap<String, AveragingCounter>();
//        for (Object resultObject : resultList) {
//            HashMap result = (HashMap) resultObject;
//
//            String manufacturer = (String) result.get("manufacturer");
//            Double rating = (Double) result.get("rating");
//
//            if (averages.containsKey(manufacturer)) {
//                averages.get(manufacturer).add(rating);
//            } else {
//                averages.put(manufacturer, new AveragingCounter(rating));
//            }
//        }
//
//        List<ManufacturerRatingDto> results = new ArrayList<ManufacturerRatingDto>(averages.size());
//        for (String manufacturer : averages.keySet()) {
//            results.add(new ManufacturerRatingDto(manufacturer, averages.get(manufacturer).average()));
//        }

        return new ArrayList<ManufacturerRatingDto>();
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
