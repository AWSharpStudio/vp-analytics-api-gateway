package com.vp.analytics.api.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureWebTestClient
class GatewayIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void callMsAndReceiveResponse() {
        stubFor(post(urlPathMatching("/api/v1/ingestion/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\": \"ok\"}")
                )
        );
        webTestClient
                .post()
                .uri("/api/v1/ingestion/event")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("ok");
    }

    @Test
    void callUriNotmapped() {
        stubFor(post(urlPathMatching("/api/v1/anyms/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\": \"ok\"}")
                )
        );
        webTestClient
                .post()
                .uri("/api/v1/anyms/event")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo("404");
    }
}