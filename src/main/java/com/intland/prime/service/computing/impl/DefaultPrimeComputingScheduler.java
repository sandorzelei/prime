package com.intland.prime.service.computing.impl;

import com.intland.prime.service.computing.PrimeComputingScheduler;
import com.intland.prime.service.computing.PrimeComputingService;
import com.intland.prime.service.queue.QueueService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Optional;

public class DefaultPrimeComputingScheduler implements PrimeComputingScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultPrimeComputingScheduler.class);

    private final PrimeComputingService primeComputingService;

    private final QueueService primeQueueService;

    private final PrimeComputingTaskFactory factory;

    public DefaultPrimeComputingScheduler(final PrimeComputingTaskFactory factory, final PrimeComputingService primeComputingService, final QueueService primeQueueService) {
        super();
        this.factory = factory;
        this.primeComputingService = primeComputingService;
        this.primeQueueService = primeQueueService;
    }

    @Override
    @Scheduled(fixedDelay = 1000L)
    public void scheduleNext() {

        if (!this.primeQueueService.hasNextScheduled()) {
            return;
        }

        if (!this.primeComputingService.isAvailable()) {
            logger.debug("There is no avaliable computing resource");
            return;
        }

        final Optional<Long> index = this.primeQueueService.getNextIndex();
        if (!index.isPresent()) {
            logger.debug("There is nothing to compute");
            return;
        }

        this.primeComputingService.startComputingPrime(this.factory.create(index.get()));
    }

}
