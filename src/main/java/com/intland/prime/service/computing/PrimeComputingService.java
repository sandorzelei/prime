package com.intland.prime.service.computing;

import org.springframework.lang.NonNull;

public interface PrimeComputingService {

    /**
     * Start computing the next prime
     *
     * @param index
     *            Index of the prime, it cannot be null
     *
     */
    void startComputingPrime(@NonNull Long index);

    /**
     * Return true if there is Available computing capacity, otherwise false
     *
     */
    boolean isAvailable();

}
