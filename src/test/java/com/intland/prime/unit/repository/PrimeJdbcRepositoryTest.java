package com.intland.prime.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.intland.prime.repository.IndexPrimeTuple;
import com.intland.prime.repository.PrimeRepository;
import com.intland.prime.repository.impl.PrimeJdbcRepository;
import com.intland.prime.service.computing.PrimeComputingScheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
@Transactional
public class PrimeJdbcRepositoryTest {

    @MockBean
    private PrimeComputingScheduler primeComputingSchuler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test(expected = IllegalArgumentException.class)
    public void testIndexParameterIsNull() {
        // GIVEN
        final BigInteger firstPrime = BigInteger.valueOf(2L);
        final PrimeRepository repository = new PrimeJdbcRepository(this.jdbcTemplate);

        // WHEN
        repository.save(null, firstPrime);

        // THEN
        // IllegalArgumentException exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIndexParameterIsNegative() {
        // GIVEN
        final BigInteger firstPrime = BigInteger.valueOf(2L);
        final PrimeRepository repository = new PrimeJdbcRepository(this.jdbcTemplate);

        // WHEN
        repository.save(-2L, firstPrime);

        // THEN
        // IllegalArgumentException exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrimeNumberParameterIsNull() {
        // GIVEN
        final PrimeRepository repository = new PrimeJdbcRepository(this.jdbcTemplate);

        // WHEN
        repository.save(1L, null);

        // THEN
        // IllegalArgumentException exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrimeNumberParameterIsNegative() {
        // GIVEN
        final BigInteger firstPrime = BigInteger.valueOf(-2L);
        final PrimeRepository repository = new PrimeJdbcRepository(this.jdbcTemplate);

        // WHEN
        repository.save(1L, firstPrime);

        // THEN
        // IllegalArgumentException exception
    }

    @Test
    public void testPrimeIsSavedAndFound() {
        // GIVEN
        final BigInteger firstPrime = BigInteger.valueOf(2L);
        final BigInteger secondPrime = BigInteger.valueOf(3L);
        final PrimeRepository repository = new PrimeJdbcRepository(this.jdbcTemplate);

        // WHEN
        repository.save(1L, firstPrime);
        repository.save(2L, secondPrime);

        // THEN
        assertThat(repository.get(1L)).isEqualTo(firstPrime);
        assertThat(repository.get(2L)).isEqualTo(secondPrime);
    }

    @Test
    public void testPrimeIsNotFound() {
        // GIVEN
        final PrimeRepository repository = new PrimeJdbcRepository(this.jdbcTemplate);

        // WHEN
        // Do nothing

        // THEN
        assertThat(repository.get(1L)).isNull();
    }

    @Test
    public void testLastPrimeWhenDatabaseIsEmpty() {
        // GIVEN
        final PrimeRepository repository = new PrimeJdbcRepository(this.jdbcTemplate);

        // WHEN
        final IndexPrimeTuple lastPrime = repository.getLastPrime();

        // THEN
        assertThat(lastPrime).isNotNull();
        assertThat(lastPrime.getIndex()).isEqualTo(1L);
        assertThat(lastPrime.getPrimeNumber()).isEqualTo(BigInteger.valueOf(2L));
    }

    @Test
    public void testLastPrimeWhenDatabaseIsNotEmpty() {
        // GIVEN
        final BigInteger firstPrime = BigInteger.valueOf(2L);
        final BigInteger secondPrime = BigInteger.valueOf(3L);
        final BigInteger thirdPrime = BigInteger.valueOf(5L);
        final PrimeRepository repository = new PrimeJdbcRepository(this.jdbcTemplate);

        // WHEN
        repository.save(1L, firstPrime);
        repository.save(2L, secondPrime);
        repository.save(3L, thirdPrime);

        final IndexPrimeTuple lastPrime = repository.getLastPrime();

        // THEN
        assertThat(lastPrime).isNotNull();
        assertThat(lastPrime.getIndex()).isEqualTo(3L);
        assertThat(lastPrime.getPrimeNumber()).isEqualTo(thirdPrime);
    }

    @Test
    public void testPrimeIsAlreadyKnow() {
        // GIVEN
        final BigInteger firstPrime = BigInteger.valueOf(2L);
        final PrimeRepository repository = new PrimeJdbcRepository(this.jdbcTemplate);
        repository.save(1L, firstPrime);

        // WHEN
        repository.save(1L, firstPrime);

        // THEN
        assertThat(repository.get(1L)).isEqualTo(firstPrime);
    }

}
