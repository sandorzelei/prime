package com.intland.prime.service.computing.impl;

import com.intland.prime.service.computing.PrimeComputingService;
import com.intland.prime.service.store.PrimeNumberStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.util.Optional;

public class ThreadPoolPrimeComputingService implements PrimeComputingService {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolPrimeComputingService.class);

    private final PrimeNumberStore primeNumberStore;

    private final ThreadPoolTaskExecutor executor;

    private final PrimeComputingTaskFactory factory;

    public ThreadPoolPrimeComputingService(final PrimeComputingTaskFactory factory, final PrimeNumberStore primeNumberStore, final ThreadPoolTaskExecutor executor) {
        super();
        this.factory = factory;
        this.primeNumberStore = primeNumberStore;
        this.executor = executor;
    }

    @Override
    public void startComputingPrime(final Long index) {
        Assert.notNull(index, "index must not be null");
        Assert.isTrue(index > 0, "index must be greater than or equal to 1");

        try {

            final Optional<BigInteger> primeFromStore = this.primeNumberStore.getPrime(index);
            if (primeFromStore.isPresent()) {
                logger.debug("{}. prime number is already know: {}", index, primeFromStore.get());
                return;
            }

            this.schedule(index);

        } catch (final Exception e) {
            logger.debug(e.getMessage(), e);
        }
    }

    protected void schedule(final Long index) {
        this.executor.submit(this.factory.create(index));
    }

    @Override
    public boolean isAvailable() {
        return this.executor.getActiveCount() < this.executor.getMaxPoolSize();
    }

}
