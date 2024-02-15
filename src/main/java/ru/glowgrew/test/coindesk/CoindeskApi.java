package ru.glowgrew.test.coindesk;

import org.springframework.web.service.annotation.GetExchange;

public interface CoindeskApi {

    @GetExchange("https://api.coindesk.com/v1/bpi/currentprice.json")
    CoindeskResponse getCurrentPrice();

}
