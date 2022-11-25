package com.example.exchangerate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

    private final String urlPattern = "https://api.privatbank.ua/p24api/exchange_rates?json&date=";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private final Calendar calendar = Calendar.getInstance();
    {
        calendar.set(Calendar.YEAR, 2022);
        calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 20);
    }
    private final Date date = calendar.getTime();
    private final int day = 24 * 60 * 60 * 1_000;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/static/**")
                .addResourceLocations("/static/");
    }

    @Bean
    public CommandLineRunner demo(final ExchangeRateService exchangeRateService){
        return args -> {
            for(int i = 0; i < 365; i++){
                date.setTime(date.getTime() - day);
                URL url = new URL(urlPattern + simpleDateFormat.format(date));
                try(var br = new BufferedReader(new InputStreamReader(url.openStream()))){
                    StringBuilder preJson = new StringBuilder();
                    int c;
                    while ((c = br.read()) != -1){
                        preJson.append((char) c);
                    }
                    String json = preJson.substring(preJson.indexOf("\"baseCurrency\":\"UAH\",\"currency\":\"USD\""));
                    json = "{\"date\":\"" + simpleDateFormat.format(date) + "\"," + json.substring(0, json.indexOf('}') + 1);
                    ExchangeRate exchangeRate = objectMapper.readValue(json, ExchangeRate.class);
                    System.out.println(exchangeRate);
                    exchangeRateService.add(exchangeRate);
                }
            }
        };
    }
}
