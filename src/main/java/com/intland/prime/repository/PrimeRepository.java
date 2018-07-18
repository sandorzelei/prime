package com.intland.prime.repository;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigInteger;

public interface PrimeRepository {

    /**
     * Return the prime number or null if it is not know yet
     *
     * @param index
     *            Index of the prime, it cannot be null
     *
     */
    @Nullable
    BigInteger get(@NonNull Long index);

    /**
     * Save a new prime number
     *
     * @param index
     *            Index of the prime, it cannot be null
     * @param prime
     *            Prime number, it cannot be null
     */
    void save(@NonNull Long index, @NonNull BigInteger prime);

    /**
     * Return the last known prime or first prime
     *
     */
    @NonNull
    IndexPrimeTuple getLastPrime();

}
