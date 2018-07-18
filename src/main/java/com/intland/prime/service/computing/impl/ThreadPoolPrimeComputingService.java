package com.intland.prime.service.computing.impl;

import com.intland.prime.service.computing.PrimeComputingService;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class ThreadPoolPrimeComputingService implements PrimeComputingService {

    private final ThreadPoolTaskExecutor executor;

    public ThreadPoolPrimeComputingService(final ThreadPoolTaskExecutor executor) {
        super();
        this.executor = executor;
    }

    @Override
    public void startComputingPrime(final PrimeComputingTask task) {
        this.executor.submit(task);
    }

    @Override
    public boolean isAvailable() {
        return this.executor.getActiveCount() < this.executor.getMaxPoolSize();
    }

}
