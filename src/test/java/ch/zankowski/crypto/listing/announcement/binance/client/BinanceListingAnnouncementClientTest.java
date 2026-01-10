package ch.zankowski.crypto.listing.announcement.binance.client;

import ch.zankowski.crypto.WireMockTestingServer;
import ch.zankowski.crypto.listing.announcement.binance.dto.BinanceListingArticle;
import ch.zankowski.crypto.listing.announcement.binance.dto.BinanceListingCatalog;
import ch.zankowski.crypto.listing.announcement.binance.dto.BinanceListingResponse;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(WireMockTestingServer.class)
class BinanceListingAnnouncementClientTest {

    @RestClient
    BinanceListingAnnouncementClient client;

    @BeforeEach
    void setUp() {
        WireMock.reset();
    }

    @Test
    void shouldSuccessfullyFetchListingAnnouncements() {
        WireMock.stubFor(
                get(urlPathEqualTo("/bapi/apex/v1/public/apex/cms/article/list/query"))
                        .withQueryParam("type", equalTo("1"))
                        .withQueryParam("catalogId", equalTo("48"))
                        .withQueryParam("pageNo", equalTo("1"))
                        .withQueryParam("pageSize", equalTo("10"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("ch/zankowski/crypto/listing/announcement/binance/client/200_full_response.json"))
        );

        final BinanceListingResponse response = client.getListingAnnouncements(1L, 48L, 1L, 10L);

        assertThat(response).isNotNull();
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getCatalogs()).hasSize(1);

        final BinanceListingCatalog catalog = response.getData().getCatalogs().getFirst();
        assertThat(catalog.getCatalogId()).isEqualTo(48L);
        assertThat(catalog.getCatalogName()).isEqualTo("New Cryptocurrency Listing");
        assertThat(catalog.getArticles()).hasSize(3);

        assertThat(catalog.getArticles())
                .extracting(BinanceListingArticle::getTitle)
                .contains(
                        "Notice on New Trading Pairs & Trading Bots Services on Binance Spot - 2025-12-24",
                        "Binance Will List KGST & Enable Trading Bots Services on Binance Spot - 2025-12-24",
                        "Binance Futures Will Launch USD?-Margined ZKPUSDT, GUAUSDT and IRUSDT Perpetual Contract (2025-12-21)"
                );
    }

    @Test
    void shouldHandleEmptyArticlesList() {
        final String responseBody = """
                {
                  "data": {
                    "catalogs": [
                      {
                        "catalogId": 48,
                        "catalogName": "Announcements",
                        "articles": []
                      }
                    ]
                  }
                }
                """;

        WireMock.stubFor(
                get(urlPathEqualTo("/bapi/apex/v1/public/apex/cms/article/list/query"))
                        .withQueryParam("type", equalTo("1"))
                        .withQueryParam("catalogId", equalTo("48"))
                        .withQueryParam("pageNo", equalTo("2"))
                        .withQueryParam("pageSize", equalTo("100"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseBody))
        );

        final BinanceListingResponse response = client.getListingAnnouncements(1L, 48L, 2L, 100L);

        assertThat(response).isNotNull();
        assertThat(response.getData().getCatalogs().getFirst().getArticles()).isEmpty();
    }

    @Test
    void shouldReturnResponseWithIgnoredUnknownFields() {
        final String responseBody = """
                {
                  "data": {
                    "catalogs": [
                      {
                        "catalogId": 48,
                        "catalogName": "Announcements",
                        "articles": [
                          {
                            "title": "Test Article",
                            "unknownField": "This should be ignored",
                            "anotherUnknownField": 123
                          }
                        ]
                      }
                    ],
                    "unknownDataField": "Should be ignored"
                  },
                  "unknownRootField": "Should also be ignored"
                }
                """;

        WireMock.stubFor(
                get(urlPathEqualTo("/bapi/apex/v1/public/apex/cms/article/list/query"))
                        .withQueryParam("type", equalTo("1"))
                        .withQueryParam("catalogId", equalTo("48"))
                        .withQueryParam("pageNo", equalTo("3"))
                        .withQueryParam("pageSize", equalTo("100"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseBody))
        );

        final BinanceListingResponse response = client.getListingAnnouncements(1L, 48L, 3L, 100L);

        assertThat(response).isNotNull();
        assertThat(response.getData().getCatalogs()).hasSize(1);
        assertThat(response.getData().getCatalogs().getFirst().getArticles()).hasSize(1);
        assertThat(response.getData().getCatalogs().getFirst().getArticles().getFirst().getTitle())
                .isEqualTo("Test Article");
    }

}

