package dev.aj.ecommerce.auth.config;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class RedissonClientConfiguration {

    @Bean
    public RMapCache<String, String> refreshTokenCache(RedissonClient redissonClient) {
        RMapCache<String, String> refreshTokenCache = redissonClient.getMapCache("refreshTokenCache", StringCodec.INSTANCE);
        refreshTokenCache.setMaxSize(100_000);
        return refreshTokenCache;
    }
}
