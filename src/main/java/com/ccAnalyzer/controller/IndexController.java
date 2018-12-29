package com.ccAnalyzer.controller;

import com.ccAnalyzer.rest.Accuracy;
import com.ccAnalyzer.rest.CryptoCurrencyTimeResponse.CryptoCurrencyData;
import com.ccAnalyzer.rest.CryptoCurrencyRest;
import lombok.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Controller
public class IndexController {

    @NonNull
    private CryptoCurrencyRest service;

    public IndexController(@NonNull CryptoCurrencyRest service) {
        this.service = service;
    }

    @GetMapping("/historyByDate")
    public String history(@NonNull @RequestParam LocalDate dateFrom,
                           @NonNull @RequestParam LocalDate dateTo,
                           @NonNull @RequestParam Accuracy accuracy,
                           Model model) {
        List<CryptoCurrencyData> data = service.getHistory(dateFrom, dateTo, accuracy);
        model.addAttribute("data", data);
        return "index";
    }

    @GetMapping(value = "/currentPrice")
    @ResponseBody
    public String currentPrice() {
        return service.getCurrentPrice();
    }


}
