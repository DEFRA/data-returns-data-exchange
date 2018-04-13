package uk.gov.defra.datareturns.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Spring cache support
 *
 * @author Sam Gardner-Dell
 */
@Configuration
@EnableCaching
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class SpringCacheConfiguration {
    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
    public RedisTemplate<Object, Object> redisTemplate(final RedisConnectionFactory connectionFactory) {
        final RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setDefaultSerializer(new JdkSerializationRedisSerializer());
        return template;
    }
}
