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

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry
//                .addResourceHandler("/static/**")
//                .addResourceLocations("/WEB-INF/static/");
//    }

    @Bean
    public CommandLineRunner demo(final ExchangeRateService exchangeRateService){
        String urlPattern = "https://api.privatbank.ua/p24api/exchange_rates?json&date=";
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                ObjectMapper om = new ObjectMapper();
                Date date = new Date(122, Calendar.NOVEMBER, 20);
                SimpleDateFormat sim = new SimpleDateFormat("dd.MM.yyyy");
                for(int i = 0; i < 365; i++){
                    date.setTime(date.getTime() - (60 * 60 * 24 * 1_000));
                    URL url = new URL(urlPattern + sim.format(date));
                    try(InputStream is = url.openStream()){
                        var br = new BufferedReader(new InputStreamReader(is));
                        StringBuilder preJson = new StringBuilder();
                        int c;
                        while ((c = br.read()) != -1){
                            preJson.append((char) c);
                        }
                        System.out.println(preJson);
                        String json = preJson.substring(preJson.indexOf("\"baseCurrency\":\"UAH\",\"currency\":\"USD\""));
                        json = "{\"date\":\"" + sim.format(date) + "\"," + json.substring(0, json.indexOf('}') + 1);
                        ExchangeRate exchangeRate = om.readValue(json, ExchangeRate.class);
                        System.out.println(exchangeRate);
                        exchangeRateService.add(exchangeRate);
                    }
                }
            }
        };
    }
}
