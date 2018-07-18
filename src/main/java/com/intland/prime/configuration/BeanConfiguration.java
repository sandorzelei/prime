package com.intland.prime.configuration;

import com.intland.prime.repository.PrimeRepository;
import com.intland.prime.repository.impl.PrimeJdbcRepository;
import com.intland.prime.service.check.PrimeCheckService;
import com.intland.prime.service.check.impl.MillerRabinPrimeCheckService;
import com.intland.prime.service.computing.PrimeComputingScheduler;
import com.intland.prime.service.computing.PrimeComputingService;
import com.intland.prime.service.computing.impl.DefaultPrimeComputingScheduler;
import com.intland.prime.service.computing.impl.PrimeComputingTaskFactory;
import com.intland.prime.service.computing.impl.ThreadPoolPrimeComputingService;
import com.intland.prime.service.queue.QueueService;
import com.intland.prime.service.queue.impl.RedisQueueService;
import com.intland.prime.service.store.PrimeNumberStore;
import com.intland.prime.service.store.impl.CachedPrimeNumberStore;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BeanConfiguration {

    @Bean
    public PrimeCheckService primeCheckService() {
        return new MillerRabinPrimeCheckService();
    }

    @Bean(name = "singleThreadExecutor", destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor singleThreadExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("singleThreadExecutor");
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(1);

        return executor;
    }

    @Bean
    public RedisTemplate<String, Long> stringLongTemplate(final RedisConnectionFactory redisConnectionFactory) {

        final RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new JdkSerializationRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());

        return template;
    }

    @Bean
    public QueueService primeQueueService(final RedisTemplate<String, Long> template) {
        return new RedisQueueService("prime", template);
    }

    @Bean
    public PrimeComputingTaskFactory primeComputingTaskFactory(final PrimeCheckService primeCheckService, final PrimeNumberStore primeNumberStore, final QueueService processingPrimeQueueService) {
        return new PrimeComputingTaskFactory(primeCheckService, primeNumberStore, processingPrimeQueueService);
    }

    @Bean
    public PrimeComputingScheduler primeComputingSchuler(final PrimeComputingTaskFactory primeComputingTaskFactory, final PrimeComputingService primeComputingService,
            final QueueService primeQueueService) {
        return new DefaultPrimeComputingScheduler(primeComputingTaskFactory, primeComputingService, primeQueueService);
    }

    @Bean
    public PrimeRepository primeRepository(final JdbcTemplate jdbcTemplate) {
        return new PrimeJdbcRepository(jdbcTemplate);
    }

    @Bean
    public PrimeNumberStore primeNumberStore(final CacheManager manager, final PrimeRepository primeRepository) {
        return new CachedPrimeNumberStore(manager.getCache("prime"), primeRepository);
    }

    @Bean
    public PrimeComputingService primeComputingService(@Qualifier("singleThreadExecutor") final ThreadPoolTaskExecutor singleThreadExecutor) {
        return new ThreadPoolPrimeComputingService(singleThreadExecutor);
    }

}
