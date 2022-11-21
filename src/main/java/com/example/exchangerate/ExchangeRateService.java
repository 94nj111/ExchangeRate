package com.example.exchangerate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Service
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Transactional
    public void add(ExchangeRate exchangeRate){
        exchangeRateRepository.save(exchangeRate);
    }

    @Transactional
    public ExchangeRate findByDate(String ... params){
        List<String> list = Arrays.stream(params).toList();
        list.forEach(a -> System.out.println(a));
        ExchangeRate exchangeRate  = findById(1L);
        if(list.get(1) == null || list.get(1).equals("") || list.get(1).isBlank())
            exchangeRate = findByDateFrom(list.get(0));
        if(list.get(0) != null && !list.get(0).equals("") && list.get(1) != null  && !list.get(1).equals("")){
            exchangeRate = findByDateFromTo(list.get(0), list.get(1));
        }
        return exchangeRate;
    }

    @Transactional
    public ExchangeRate findById(long id){
        return exchangeRateRepository.findById(id).get();
    }

    public ExchangeRate findByDateFrom(String from){
        java.util.Date date;
        try {
            date = sim.parse(from);
        } catch (ParseException e) {
            date = new java.util.Date(122, Calendar.NOVEMBER, 19);
        }
        Date dateFrom = new Date(date.getTime());
        return exchangeRateRepository.findByDate(dateFrom);
    }

    public ExchangeRate findByDateFromTo(String from, String to){
        java.util.Date dfrom;
        java.util.Date dto;
        try {
            dfrom = sim.parse(from);
            dto = sim.parse(to);
        } catch (ParseException e) {
            dfrom = new java.util.Date(122, Calendar.NOVEMBER, 19);
            dto = new java.util.Date(122, Calendar.NOVEMBER, 19);
        }
        Date dateFrom = new Date(dfrom.getTime());
        Date dateTo = new Date(dto.getTime());
        ExchangeRate exchangeRate = new ExchangeRate();
        List<ExchangeRate> exchangeRates = exchangeRateRepository.findByDate(dateFrom, dateTo);
        exchangeRate.setBaseCurrency("UAH");
        exchangeRate.setCurrency("USD");
        try {
            exchangeRate.setSaleRate(getAvarage(exchangeRates, "saleRate"));
            exchangeRate.setPurchaseRate(getAvarage(exchangeRates, "purchaseRate"));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            exchangeRate.setSaleRate((float) 0);
            exchangeRate.setPurchaseRate((float) 0);
        }
        return exchangeRate;
    }

    public float getAvarage(List<ExchangeRate> exchangeRates, String field) throws NoSuchFieldException, IllegalAccessException {
        Float avarage = (float)0;
        Field f = ExchangeRate.class.getDeclaredField(field);
        f.setAccessible(true);
        for(ExchangeRate ex : exchangeRates){
            avarage = avarage + (Float)f.get(ex);
        }
        return avarage/exchangeRates.size();
    }
}
