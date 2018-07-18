package com.intland.prime.service.store.impl;

import com.intland.prime.repository.IndexPrimeTuple;
import com.intland.prime.repository.PrimeRepository;
import com.intland.prime.service.store.PrimeNumberStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.util.Optional;

public class CachedPrimeNumberStore implements PrimeNumberStore {

    private static final Logger logger = LoggerFactory.getLogger(CachedPrimeNumberStore.class);

    private final Cache cache;

    private final PrimeRepository primeRepository;

    public CachedPrimeNumberStore(final Cache primeCache, final PrimeRepository primeRepository) {
        this.cache = primeCache;
        this.primeRepository = primeRepository;
    }

    @Override
    public Optional<BigInteger> getPrime(final Long index) {
        Assert.notNull(index, "index must not be null");

        final BigInteger cachedPrime = this.getPrimeFromCache(index);
        if (cachedPrime != null) {
            logger.trace("{}. prime was cached", index);
            return Optional.of(cachedPrime);
        }

        final BigInteger storedPrime = this.primeRepository.get(index);
        if (storedPrime != null) {
            logger.trace("{}. prime was stored, put it into the cache", index);
            this.putPrimeIntoCache(index, storedPrime);
            return Optional.of(storedPrime);
        }

        return Optional.empty();

    }

    @Override
    public void storePrime(final Long index, final BigInteger primeNumber) {
        Assert.notNull(index, "index must not be null");
        Assert.notNull(primeNumber, "primeNumber must not be null");

        if (this.getPrimeFromCache(index) == null) {
            this.primeRepository.save(index, primeNumber);
            this.putPrimeIntoCache(index, primeNumber);
        }

    }

    @Override
    public IndexPrimeTuple getLastPrime() {
        return this.primeRepository.getLastPrime();
    }

    private BigInteger getPrimeFromCache(final Long index) {
        return this.cache.get(index, BigInteger.class);
    }

    private void putPrimeIntoCache(final Long index, final BigInteger primeNumber) {
        this.cache.put(index, primeNumber);
    }

}
