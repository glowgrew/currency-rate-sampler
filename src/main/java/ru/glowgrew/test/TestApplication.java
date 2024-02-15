package ru.glowgrew.test;

import ru.glowgrew.test.coindesk.CoindeskApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@EnableScheduling
@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    CoindeskApi coindeskApi(RestClient restClient) {
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build().createClient(CoindeskApi.class);
    }

    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.requestFactory(new JdkClientHttpRequestFactory()).build();
    }

}
