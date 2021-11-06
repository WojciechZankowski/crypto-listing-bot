package ch.zankowski.crypto.listing.binance.client;

import ch.zankowski.crypto.listing.binance.dto.BinanceListingResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;

@RegisterRestClient
public interface BinanceListingAnnouncementClient {

    @GET
    BinanceListingResponse getListingAnnouncements(
            @QueryParam("catalogId") final Long catalogId,
            @QueryParam("pageNo") final Long pageNo,
            @QueryParam("pageSize") final Long pageSize,
            @QueryParam("rnd") final Long rnd
    );

}
