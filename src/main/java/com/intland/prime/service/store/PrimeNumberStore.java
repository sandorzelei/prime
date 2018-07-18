package com.intland.prime.service.store;

import com.intland.prime.repository.IndexPrimeTuple;

import org.springframework.lang.NonNull;

import java.math.BigInteger;
import java.util.Optional;

public interface PrimeNumberStore {

    /**
     * Return a prime number for the given index
     *
     * @param index
     *            Index of the prime number, it cannot be null
     *
     * @return a {@link Optional} with the found prime
     */
    @NonNull
    Optional<BigInteger> getPrime(@NonNull Long index);

    /**
     * Store a prime in the store
     *
     * @param index
     *            Index of the prime number, it cannot be null
     *
     * @param primeNumber
     *            Prime number, it cannot be null
     */
    void storePrime(@NonNull Long index, @NonNull BigInteger primeNumber);

    /**
     * Return the last known prime
     */
    @NonNull
    IndexPrimeTuple getLastPrime();

}
