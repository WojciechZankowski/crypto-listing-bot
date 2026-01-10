package ch.zankowski.crypto.listing.announcement.binance.client;

import ch.zankowski.crypto.listing.announcement.binance.dto.BinanceListingResponse;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.QueryParam;

@RegisterRestClient
@ClientHeaderParam(name = "Cache-Control", value = "no-cache")
@ClientHeaderParam(name = "Pragma", value = "no-cache")
@ClientHeaderParam(name = "Expires", value = "0")
public interface BinanceListingAnnouncementClient {

    @Path("/bapi/apex/v1/public/apex/cms/article/list/query")
    @GET
    BinanceListingResponse getListingAnnouncements(
            @QueryParam("type") final Long type,
            @QueryParam("catalogId") final Long catalogId,
            @QueryParam("pageNo") final Long pageNo,
            @QueryParam("pageSize") final Long pageSize
    );

}
