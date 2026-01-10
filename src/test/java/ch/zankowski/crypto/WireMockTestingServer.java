package ch.zankowski.crypto;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WireMockTestingServer implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(wireMockConfig()
                .port(8080)
//                .dynamicPort()
        );
        wireMockServer.start();

        return Map.of(
                "quarkus.rest-client.\"ch.zankowski.crypto.listing.announcement.binance.client.BinanceListingAnnouncementClient\".url",
                wireMockServer.baseUrl()
        );
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

}

