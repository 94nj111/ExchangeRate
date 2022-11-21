package com.example.exchangerate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MyController {

    private final ExchangeRateService exchangeRateService;

    public MyController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/")
    public String index(@RequestParam(value = "a", required = false) String a,
                        @RequestParam(value = "b", required = false) String b,
                        Model model){
        ExchangeRate ex  = exchangeRateService.findByDate(a, b);
        model.addAttribute("ex", ex);
        return "index";
    }
}
