package com.ccAnalyzer.controller;

import com.ccAnalyzer.rest.CryptoCurrencyRest;
import lombok.NonNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

@Component
public class AlgoController implements ApplicationListener<ContextRefreshedEvent> {

    private static final Object KEY = new Object();
    private static final int INTERVAL = 5000;
    private static List<BigDecimal> prices = new ArrayList<>();

    @NonNull
    private CryptoCurrencyRest service;

    public AlgoController(@NonNull CryptoCurrencyRest service) {
        this.service = service;
    }

    public static BigDecimal randomwalk(BigDecimal currentPrice) {
        Random ran = new Random();
        double u = ran.nextGaussian();
        return currentPrice.add(new BigDecimal(u).setScale(2, RoundingMode.HALF_UP));
    }

    public static BigDecimal randomwalk2(BigDecimal currentPrice) {
        return currentPrice.add(new BigDecimal(1));
    }


    public static void runAlgo(Function<BigDecimal, BigDecimal> func, String name) {
        BigDecimal expectedPrice = BigDecimal.ZERO;
        int index = 0;
        while(true) {
            if(prices.size() > index) {
                BigDecimal newPrice;
                synchronized (KEY) {
                    newPrice = prices.get(index);
                }
                System.out.println(name + "---> New Price:" + newPrice
                                + " Expected:" + expectedPrice);
                System.out.println(name + "---> New Price:" + newPrice
                        + " Expected:" + expectedPrice + " Error: " + (newPrice.subtract(expectedPrice)));
                expectedPrice = func.apply(newPrice);
                index++;
            }
        }
    }

    @Scheduled(fixedRate = INTERVAL)
    public void poll() {
        final BigDecimal bigDecimal = new BigDecimal(service.getCurrentPrice());
        synchronized (KEY) {
            prices.add(bigDecimal);
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        new Thread(() -> AlgoController.runAlgo(AlgoController::randomwalk, "randomwalk")).start();

        new Thread(() -> AlgoController.runAlgo(AlgoController::randomwalk2, "other")).start();
    }
}
