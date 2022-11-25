package com.example.exchangerate;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Entity
@Table(name = "ExchangeRate")
@NoArgsConstructor @Data
public class ExchangeRate {

    @Id
    @GeneratedValue
    private long id;
    private Date date;
    private String baseCurrency;
    private String currency;
    private Float saleRateNB;
    private Float purchaseRateNB;
    private Float saleRate;
    private Float purchaseRate;


    public void setDate(String date) {
        try {
            this.date = new Date(new SimpleDateFormat("dd.MM.yyyy").parse(date).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
