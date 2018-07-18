package com.intland.prime.service.check.impl;

import com.intland.prime.service.check.PrimeCheckService;

import java.math.BigInteger;

public class MillerRabinPrimeCheckService implements PrimeCheckService {

    @Override
    public BigInteger getNext(final BigInteger startFrom) {
        return startFrom.nextProbablePrime();
    }

}
