package com.ccAnalyzer.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class CryptoCurrencyTimeResponse {

    @JsonProperty("Response")
    private String response;
    @JsonProperty("Data")
    private List<CryptoCurrencyData> data;
    @JsonProperty("TimeFrom")
    private Long timeFrom;
    @JsonProperty("TimeTo")
    private Long timeTo;

    @Data
    @NoArgsConstructor
    public static class CryptoCurrencyData {

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime time;
        private float close;
        private float high;
        private float low;
        private float open;
    }
}
