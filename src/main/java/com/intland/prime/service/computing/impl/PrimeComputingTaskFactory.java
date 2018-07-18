package com.intland.prime.service.computing.impl;

import com.intland.prime.service.check.PrimeCheckService;
import com.intland.prime.service.queue.QueueService;
import com.intland.prime.service.store.PrimeNumberStore;

public class PrimeComputingTaskFactory {

    private final QueueService processingPrimeQueueService;

    private final PrimeNumberStore primeNumberStore;

    private final PrimeCheckService checkService;

    public PrimeComputingTaskFactory(final PrimeCheckService checkService, final PrimeNumberStore primeNumberStore, final QueueService processingPrimeQueueService) {
        this.checkService = checkService;
        this.primeNumberStore = primeNumberStore;
        this.processingPrimeQueueService = processingPrimeQueueService;
    }

    public PrimeComputingTask create(final Long index) {
        return new PrimeComputingTask(this.checkService, this.primeNumberStore, this.processingPrimeQueueService, index);
    }

}
