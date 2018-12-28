package com.ccAnalyzer.rest;

import com.ccAnalyzer.rest.CryptoCurrencyTimeResponse.CryptoCurrencyData;
import io.vavr.Function2;
import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CryptoCurrencyRest {

    private static final String COIN = "BTC";
    private static final String CURRENCY = "USD";
    private RestTemplate restTemplate = new RestTemplate();

    private static final String REST_URI_DATE = "https://min-api.cryptocompare.com/data/";
    private static final String REST_URI_PRICE = "https://min-api.cryptocompare.com/data/price";
    private static final String API_KEY = "f02c13290e01e904fda41fb7e72bb20cd7e7b91fc6f8352299dc4d450e4868ef";
    private static final int MAX_LIMIT = 2000;

    public List<CryptoCurrencyData> getHistory(@NonNull LocalDate datefrom,
                                               @NonNull LocalDate dateTo,
                                               @NonNull Accuracy accuracy){
        List<CryptoCurrencyData> list = new ArrayList<>();
        long measurements = countMeasurements(datefrom, dateTo, accuracy);
        long timeTo = dateTo.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        while(measurements > MAX_LIMIT) {
            List<CryptoCurrencyData> tmpList = getHistory(MAX_LIMIT, timeTo, accuracy);
            measurements -= MAX_LIMIT;
            timeTo = tmpList.get(0).getTime().toEpochSecond(ZoneOffset.UTC);
            Collections.reverse(tmpList);
            list.addAll(tmpList);
        }
        if(measurements > 0) {
            List<CryptoCurrencyData> tmpList = getHistory((int) measurements, timeTo, accuracy);
            Collections.reverse(tmpList);
            list.addAll(tmpList);
        }

        return list;
    }

    private int countMeasurements(@NonNull LocalDate dateFrom,
                                  @NonNull LocalDate dateTo,
                                  @NonNull Accuracy accuracy){
        return  HashMap.ofEntries(
                Tuple.of(Accuracy.DAYS, (Function2<LocalDate, LocalDate, Long>) (d1, d2) -> d2.toEpochDay() - d1.toEpochDay()),
                Tuple.of(Accuracy.HOURS, (d1, d2) ->
                        d2.atStartOfDay(ZoneOffset.UTC).toEpochSecond()/3600 - d1.atStartOfDay(ZoneOffset.UTC).toEpochSecond()/3600),
                Tuple.of(Accuracy.MINUTES, (d1, d2) ->
                        d2.atStartOfDay(ZoneOffset.UTC).toEpochSecond()/60 - d1.atStartOfDay(ZoneOffset.UTC).toEpochSecond()/60)
        ).get(accuracy).map(it -> it.apply(dateFrom, dateTo)).map(Math::toIntExact).getOrElse(0);

    }

    private List<CryptoCurrencyData> getHistory(@NonNull Integer limit, @NonNull Long timeTo, @NonNull Accuracy accuracy){
        return Try.of(() -> restTemplate.getForObject(getURIDate(limit, timeTo, accuracy), CryptoCurrencyTimeResponse.class))
                .map(CryptoCurrencyTimeResponse::getData).getOrElse(Collections.emptyList());
    }

    public Float getCurrentPrice(){
        return Try.of(() -> restTemplate.getForObject(getURIPrice(), CryptoCurrencyPriceResponse.class))
                .map(CryptoCurrencyPriceResponse::getPrice).getOrElse(0.0F);
    }

    private static String getURIPrice() {
        return getURIPrice(COIN, CURRENCY);
    }

    private static String getURIPrice(@NonNull String coin, @NonNull String currency) {
        return REST_URI_PRICE + "?fsym=" + coin + "&tsyms=" + currency + "&api_key=" + API_KEY;
    }

    private static String getURIDate(Integer limit, Long timeTo, @NonNull Accuracy accuracy) {
        return getURIDate(COIN, CURRENCY, limit, timeTo, accuracy);
    }

    private static String getURIDate(@NonNull String coin, @NonNull String currency, Integer limit, Long timeTo, @NonNull Accuracy accuracy) {
        StringBuilder sb = new StringBuilder(REST_URI_DATE);
        sb.append(accuracy.path);
        sb.append("?fsym=").append(coin);
        sb.append("&tsym=").append(currency);
        Option.of(limit).forEach(it -> sb.append("&limit=").append(it));
        Option.of(timeTo).forEach(it -> sb.append("&toTs=").append(it));
        sb.append("&api_key=").append(API_KEY);
        return sb.toString();
    }
}
