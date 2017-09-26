package uk.gov.ea.datareturns.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import uk.gov.ea.datareturns.util.Environment;

import java.time.Duration;
import java.util.Objects;

/**
 * Spring cache provider configuration
 *
 * @author Sam Gardner-Dell
 */
@Configuration
@EnableCaching(mode = AdviceMode.PROXY)
public class SpringCacheConfiguration extends CachingConfigurerSupport {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SpringCacheConfiguration.class);

    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
    @Bean public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory cf) {
        LOGGER.info("Initialising redis-template for redis backed spring cache");
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(cf);
        redisTemplate.setEnableDefaultSerializer(true);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
    @Bean CacheManagerCustomizer<RedisCacheManager> redisCacheManagerCacheManagerCustomizer() {
        LOGGER.info("Customising cache manager for redis backed spring cache");
        RedisSerializer<String> serializer = new StringRedisSerializer();
        String cachePrefix = "drapi_" + Environment.getVersion() + ":";
        return (cacheManager) -> {
            cacheManager.setCachePrefix((cacheName) -> serializer.serialize(cachePrefix + cacheName + ":"));
            cacheManager.setUsePrefix(true);
            cacheManager.setLoadRemoteCachesOnStartup(true);
            cacheManager.setDefaultExpiration(Duration.ofDays(1).getSeconds());
            cacheManager.setTransactionAware(true);
        };
    }

    @Bean
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                LOGGER.error("Cache GET error occurred for cache " + cache.getName() + " with key " + Objects.toString(key), exception);
            }

            @Override public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                LOGGER.error("Cache PUT error occurred for cache " + cache.getName() + " with key " + Objects.toString(key), exception);
            }

            @Override public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                LOGGER.error("Cache EVICT error occurred for cache " + cache.getName() + " with key " + Objects.toString(key), exception);
            }

            @Override public void handleCacheClearError(RuntimeException exception, Cache cache) {
                LOGGER.error("Cache CLEAR error occurred for cache " + cache.getName(), exception);
            }
        };
    }
}