package com.intland.prime.service.computing;

import com.intland.prime.service.computing.impl.PrimeComputingTask;

import org.springframework.lang.NonNull;

public interface PrimeComputingService {

    /**
     * Start computing the next prime
     *
     * @param task
     *            {@link PrimeComputingTask} cannot be null
     *
     */
    void startComputingPrime(@NonNull PrimeComputingTask task);

    /**
     * Return true if there is Available computing capacity, otherwise false
     *
     */
    boolean isAvailable();

}
