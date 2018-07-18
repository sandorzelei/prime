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

    private final QueueService scheduledPrimeQueueService;

    private final QueueService processingPrimeQueueService;

    public DefaultPrimeComputingScheduler(final PrimeComputingService primeComputingService, final QueueService scheduledPrimeQueueService, final QueueService processingPrimeQueueService) {
        super();
        this.primeComputingService = primeComputingService;
        this.scheduledPrimeQueueService = scheduledPrimeQueueService;
        this.processingPrimeQueueService = processingPrimeQueueService;
    }

    @Override
    @Scheduled(fixedDelay = 1000L)
    public void scheduleNext() {

        if (!this.scheduledPrimeQueueService.hasNext()) {
            return;
        }

        if (!this.primeComputingService.isAvailable()) {
            logger.debug("There is no avaliable computing resource");
            return;
        }

        final Optional<Long> index = this.scheduledPrimeQueueService.pop();
        if (!index.isPresent()) {
            logger.debug("There is nothing to compute");
            return;
        }

        if (this.processingPrimeQueueService.contains(index.get())) {
            logger.debug("Somebody already workin on finding the {}. prime", index.get());
            return;
        }

        this.primeComputingService.startComputingPrime(index.get());
    }

}
