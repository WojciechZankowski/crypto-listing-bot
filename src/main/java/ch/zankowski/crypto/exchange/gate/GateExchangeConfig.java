package ch.zankowski.crypto.exchange.gate;

import ch.zankowski.crypto.exchange.ExchangeMode;
import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigMapping(prefix = "crypto.exchange.gate")
public interface GateExchangeConfig {

    @WithName("mode")
    ExchangeMode mode();

    @WithName("key")
    String key();

    @WithName("secret")
    String secret();

}
