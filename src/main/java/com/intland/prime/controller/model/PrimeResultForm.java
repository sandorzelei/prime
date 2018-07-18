package com.intland.prime.controller.model;

import java.io.Serializable;
import java.math.BigInteger;

public class PrimeResultForm implements Serializable {

    private static final long serialVersionUID = -2103008678244091516L;

    private Long index;

    private BigInteger primeNumber;

    public PrimeResultForm() {
        super();
    }

    public PrimeResultForm(final Long index, final BigInteger primeNumber) {
        this.index = index;
        this.primeNumber = primeNumber;
    }

    public Long getIndex() {
        return this.index;
    }

    public void setIndex(final Long index) {
        this.index = index;
    }

    public BigInteger getPrimeNumber() {
        return this.primeNumber;
    }

    public void setPrimeNumber(final BigInteger primeNumber) {
        this.primeNumber = primeNumber;
    }

    @Override
    public String toString() {
        return "PrimeResultForm [index=" + this.index + ", primeNumber=" + this.primeNumber + "]";
    }

}