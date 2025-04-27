package dev.aj.ecommerce.auth.config;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RedissonClientConfiguration {

    private final Environment environment;

    @Bean(name = "redissonClient")
    public RedissonClient getRedissonClient() {

        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://password@localhost:16379")
                .setDatabase(0)
                .setPassword("password");

        return Redisson.create(config);
    }

    @Bean
    public RMapCache<String, String> refreshTokenCache(RedissonClient redissonClient) {
        RMapCache<String, String> refreshTokenCache = redissonClient.getMapCache("refreshTokenCache", StringCodec.INSTANCE);
        refreshTokenCache.setMaxSize(100_000);
        refreshTokenCache.expireIfSet(Duration.ofMillis(Long.parseLong(environment.getProperty("jwt.refresh.expiration.ms", "10"))));
        return refreshTokenCache;
    }

}
