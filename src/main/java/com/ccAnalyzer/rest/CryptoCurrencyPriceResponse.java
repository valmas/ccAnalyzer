package com.ccAnalyzer.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Objects;

@Data
@NoArgsConstructor
public class CryptoCurrencyPriceResponse {

    @JsonProperty("USD")
    private Float usd;
    @JsonProperty("EUR")
    private Float eur;

    @NonNull
    public Float getPrice(){
        return List.of(usd, eur).find(Objects::nonNull).getOrElse(0.0F);
    }
}
