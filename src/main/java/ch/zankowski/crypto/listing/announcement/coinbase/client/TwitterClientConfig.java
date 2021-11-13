package ch.zankowski.crypto.listing.announcement.coinbase.client;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigMapping(prefix = "crypto.twitter")
public interface TwitterClientConfig {

    @WithName("apiKey")
    String apiKey();

    @WithName("secretApiKey")
    String secretApiKey();

}
