package org.example.coffeeshop.config;

import java.io.IOException;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class RedisConfig {

	private final Resource redissonConfig;

	public RedisConfig(@Value( "classpath:redisson-config.yml" ) Resource redissonConfig) {
		this.redissonConfig = redissonConfig;
	}

	@Bean
	RedissonClient redissonClient() throws IOException {
		Config config = Config.fromYAML( redissonConfig.getURL() );

		return Redisson.create(config);
	}

}
