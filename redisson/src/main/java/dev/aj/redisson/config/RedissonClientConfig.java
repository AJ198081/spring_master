package dev.aj.redisson.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonClientConfig {

    private RedissonClient redissonClient;

    @Bean(name = "redissonClient")
    public RedissonClient getRedissonClient() {

        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:16379")
                .setDatabase(0)
                .setPassword("password");

        return Redisson.create(config);
    }

    @Bean(name = "redissonReactiveClient")
    public RedissonReactiveClient getRedissonReactiveClient() {
        return getRedissonClient().reactive();
    }

}
