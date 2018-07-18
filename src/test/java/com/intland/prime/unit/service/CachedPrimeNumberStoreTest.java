package com.intland.prime.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.intland.prime.repository.IndexPrimeTuple;
import com.intland.prime.repository.PrimeRepository;
import com.intland.prime.service.computing.PrimeComputingScheduler;
import com.intland.prime.service.store.PrimeNumberStore;
import com.intland.prime.service.store.impl.CachedPrimeNumberStore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
@Transactional
public class CachedPrimeNumberStoreTest {

    @MockBean
    private PrimeComputingScheduler primeComputingSchuler;

    @Autowired
    private PrimeRepository primeRepository;

    @Test
    public void primeIsNotCachedIfItIsUnknown() {

        // GIVEN
        final Long index = 1L;

        final PrimeRepository repository = Mockito.spy(this.primeRepository);
        final PrimeNumberStore primeNumberStore = new CachedPrimeNumberStore(new ConcurrentMapCache("testCache"), repository);

        // WHEN
        final Optional<BigInteger> resultOfFirstCall = primeNumberStore.getPrime(index);
        final Optional<BigInteger> resultOfSecondCall = primeNumberStore.getPrime(index);

        // THEN
        verify(repository, times(2)).get(ArgumentMatchers.eq(index));
        verifyNoMoreInteractions(repository);

        assertThat(resultOfFirstCall.isPresent()).isFalse();
        assertThat(resultOfSecondCall.isPresent()).isFalse();
    }

    @Test
    public void primeIsCachedIfItIsKnown() {

        // GIVEN
        final Long index = 1L;

        final BigInteger firstPrime = BigInteger.valueOf(2L);
        this.primeRepository.save(index, firstPrime);

        final PrimeRepository repository = Mockito.spy(this.primeRepository);
        final PrimeNumberStore primeNumberStore = new CachedPrimeNumberStore(new ConcurrentMapCache("testCache"), repository);

        // WHEN
        final Optional<BigInteger> resultOfFirstCall = primeNumberStore.getPrime(index);
        final Optional<BigInteger> resultOfSecondCall = primeNumberStore.getPrime(index);

        // THEN
        verify(repository, times(1)).get(ArgumentMatchers.eq(index));
        verifyNoMoreInteractions(repository);

        assertThat(resultOfFirstCall.get()).isEqualTo(firstPrime);
        assertThat(resultOfSecondCall.get()).isEqualTo(firstPrime);
    }

    @Test
    public void newPrimeIsCachedAndStored() {

        // GIVEN
        final Long index = 1L;
        final BigInteger firstPrime = BigInteger.valueOf(2L);

        final PrimeRepository repository = Mockito.spy(this.primeRepository);
        final PrimeNumberStore primeNumberStore = new CachedPrimeNumberStore(new ConcurrentMapCache("testCache"), repository);

        // WHEN
        primeNumberStore.storePrime(index, firstPrime);
        final Optional<BigInteger> prime = primeNumberStore.getPrime(index);

        // THEN
        verify(repository, times(1)).save(ArgumentMatchers.eq(index), ArgumentMatchers.eq(firstPrime));
        verifyNoMoreInteractions(repository);

        assertThat(prime.get()).isEqualTo(firstPrime);
    }

    @Test
    public void lastKnowPrimeIsChanged() {

        // GIVEN
        final Long firstIndex = 1L;
        final BigInteger firstPrime = BigInteger.valueOf(2L);

        final Long secondIndex = 2L;
        final BigInteger secondPrime = BigInteger.valueOf(3L);

        this.primeRepository.save(firstIndex, firstPrime);

        final PrimeRepository repository = Mockito.spy(this.primeRepository);
        final PrimeNumberStore primeNumberStore = new CachedPrimeNumberStore(new ConcurrentMapCache("testCache"), repository);

        // WHEN
        final IndexPrimeTuple resultOfFirstCall = primeNumberStore.getLastPrime();
        this.primeRepository.save(secondIndex, secondPrime);
        final IndexPrimeTuple resultOfSecondCall = primeNumberStore.getLastPrime();

        // THEN
        verify(repository, times(2)).getLastPrime();
        verifyNoMoreInteractions(repository);

        assertThat(resultOfFirstCall.getIndex()).isEqualTo(firstIndex);
        assertThat(resultOfFirstCall.getPrimeNumber()).isEqualTo(firstPrime);

        assertThat(resultOfSecondCall.getIndex()).isEqualTo(secondIndex);
        assertThat(resultOfSecondCall.getPrimeNumber()).isEqualTo(secondPrime);
    }

}
