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
import java.util.Optional;

@Service
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final Calendar calendar = Calendar.getInstance();
    {
        calendar.set(Calendar.YEAR, 2022);
        calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 19);
    }

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
        ExchangeRate exchangeRate  = findById(1L);
        if((list.get(0) != null && !list.get(0).isBlank()) && (list.get(1) == null || list.get(1).isBlank()))
            exchangeRate = findByDateFrom(list.get(0));
        if(list.get(0) != null && !list.get(0).isBlank() && list.get(1) != null  && !list.get(1).isBlank()){
            exchangeRate = findByDateFromTo(list.get(0), list.get(1));
        }
        return exchangeRate;
    }

    @Transactional
    public ExchangeRate findById(long id){
        Optional<ExchangeRate> optional = exchangeRateRepository.findById(id);
        return optional.orElse(null);
    }

    public ExchangeRate findByDateFrom(String from){
        Date dateFrom;
        try {
            dateFrom = new Date(simpleDateFormat.parse(from).getTime());
        } catch (ParseException e) {
            dateFrom = new Date(calendar.getTimeInMillis());
        }
        return exchangeRateRepository.findByDate(dateFrom).orElse(findById(1L));
    }

    public ExchangeRate findByDateFromTo(String from, String to){
        Date dateFrom;
        Date dateTo;
        try {
            dateFrom = new Date(simpleDateFormat.parse(from).getTime());
            dateTo = new Date(simpleDateFormat.parse(to).getTime());
        } catch (ParseException e) {
            dateFrom = new Date(calendar.getTimeInMillis());
            dateTo = new Date(calendar.getTimeInMillis());
        }
        List<ExchangeRate> exchangeRates = exchangeRateRepository.findByDate(dateFrom, dateTo);
        if(exchangeRates.isEmpty()) return findById(1L);
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrency("UAH");
        exchangeRate.setCurrency("USD");
        try {
            exchangeRate.setSaleRate(getAverage(exchangeRates, "saleRate"));
            exchangeRate.setPurchaseRate(getAverage(exchangeRates, "purchaseRate"));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            exchangeRate.setSaleRate((float) 0);
            exchangeRate.setPurchaseRate((float) 0);
        }
        return exchangeRate;
    }

    public float getAverage(List<ExchangeRate> exchangeRates, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        float sum = 0;
        Field field = ExchangeRate.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        for(ExchangeRate exchangeRate : exchangeRates){
            if(field.get(exchangeRate) != null)sum += (float)field.get(exchangeRate);
        }
        return sum/exchangeRates.size();
    }
}
