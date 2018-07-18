package com.intland.prime.service.check;

import org.springframework.lang.NonNull;

import java.math.BigInteger;

public interface PrimeCheckService {

    /**
     * Return the next possible prime, search is started from the given parameter
     *
     * @param startFrom
     *            it cannot be null
     *
     */
    BigInteger getNext(@NonNull BigInteger startFrom);

}
