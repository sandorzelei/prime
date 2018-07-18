package com.intland.prime.service.computing.impl;

import com.intland.prime.repository.IndexPrimeTuple;
import com.intland.prime.service.check.PrimeCheckService;
import com.intland.prime.service.queue.QueueService;
import com.intland.prime.service.store.PrimeNumberStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

public class PrimeComputingTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(PrimeComputingTask.class);

    private final Long index;

    private final PrimeCheckService checkService;

    private final PrimeNumberStore primeNumberStore;

    private final QueueService primeQueueService;

    public PrimeComputingTask(final PrimeCheckService checkService, final PrimeNumberStore primeNumberStore, final QueueService primeQueueService, final Long index) {
        this.checkService = checkService;
        this.primeNumberStore = primeNumberStore;
        this.primeQueueService = primeQueueService;
        this.index = index;
    }

    @Override
    public void run() {
        try {

            final IndexPrimeTuple closesKnowPrime = this.primeNumberStore.getLastPrime();
            logger.debug("Start computing from {}. prime, Index of prime is {}", closesKnowPrime.getIndex(), this.index);

            BigInteger prime = closesKnowPrime.getPrimeNumber();
            for (long i = closesKnowPrime.getIndex() + 1; i <= this.index; i++) {

                // There is a chance that prime was found by an other instance
                if (this.primeNumberStore.getPrime(i).isPresent()) {
                    logger.trace("{}. prime was found by an other instance", i);
                    continue;
                }

                prime = this.checkService.getNext(prime);
                this.primeNumberStore.storePrime(i, prime);

                if (i % 1000 == 0) {
                    logger.debug("{}. prime found from {}", i, this.index);
                }

            }

            this.primeQueueService.processed(this.index);
            logger.debug("{}. prime is found and stored, prime is {}", this.index, prime);

        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
