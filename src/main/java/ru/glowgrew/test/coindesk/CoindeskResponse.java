package ru.glowgrew.test.coindesk;

import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.Map;

public record CoindeskResponse(Time time, Map<String, Currency> bpi) {

    public record Time(@JsonAlias("updatedISO") @DateTimeFormat ZonedDateTime lastUpdated) {

    }

    public record Currency(String code, String symbol, @JsonAlias("rate_float") double currencyRate) {

    }

}
