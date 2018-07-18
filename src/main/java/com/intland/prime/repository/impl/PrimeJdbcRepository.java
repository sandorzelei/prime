package com.intland.prime.repository.impl;

import com.intland.prime.repository.IndexPrimeTuple;
import com.intland.prime.repository.PrimeRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrimeJdbcRepository implements PrimeRepository {

    private static final BigInteger FIRST_PRIME = BigInteger.valueOf(2L);

    private static final IndexPrimeTuple FIRST_PRIME_TUPLE = new IndexPrimeTuple(1L, FIRST_PRIME);

    private static final Logger logger = LoggerFactory.getLogger(PrimeJdbcRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public PrimeJdbcRepository(final JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigInteger get(final Long index) {
        Assert.notNull(index, "Index cannot be null");

        try {
            return this.jdbcTemplate.queryForObject("select prime_number from prime where index=?", new Object[] { index }, new BigIntegerRowMapper());
        } catch (final EmptyResultDataAccessException e) {
            logger.trace("There is no stored prime for {} index", index);
            return null;
        }
    }

    @Override
    public void save(final Long index, final BigInteger primeNumber) {
        Assert.notNull(index, "Index cannot be null");
        Assert.isTrue(index >= 0L, "Index must be zero or greater");
        Assert.notNull(primeNumber, "Prime number cannot be null");
        Assert.isTrue(primeNumber.compareTo(BigInteger.valueOf(2)) >= 0, "Prime number must be 2 or greater");

        try {
            this.jdbcTemplate.update("insert into prime (index, prime_number) values(?, ?)", new Object[] { index, primeNumber.toString() });
        } catch (final DuplicateKeyException e) {
            logger.trace("Prime is already known");
        }
    }

    @Override
    public IndexPrimeTuple getLastPrime() {
        try {
            return this.jdbcTemplate.queryForObject("select index, prime_number from prime order by index desc limit 1", new IndexPrimeTupleRowMapper());
        } catch (final EmptyResultDataAccessException e) {
            logger.trace("There is no stored prime yet");
            this.save(1L, FIRST_PRIME);
            return FIRST_PRIME_TUPLE;
        }
    }

    class BigIntegerRowMapper implements RowMapper<BigInteger> {

        @Override
        public BigInteger mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            return new BigInteger(rs.getString("prime_number"));
        }

    }

    class IndexPrimeTupleRowMapper implements RowMapper<IndexPrimeTuple> {

        @Override
        public IndexPrimeTuple mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Long index = rs.getLong("index");
            final BigInteger primeNumber = new BigInteger(rs.getString("prime_number"));

            return new IndexPrimeTuple(index, primeNumber);
        }

    }

}
