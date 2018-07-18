package com.intland.prime.repository;

import java.io.Serializable;
import java.math.BigInteger;

public class IndexPrimeTuple implements Serializable {

    private static final long serialVersionUID = 1370721048298157376L;

    private Long index;

    private BigInteger primeNumber;

    public IndexPrimeTuple() {
        super();
    }

    public IndexPrimeTuple(final Long index, final BigInteger primeNumber) {
        super();
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.index == null ? 0 : this.index.hashCode());
        result = prime * result + (this.primeNumber == null ? 0 : this.primeNumber.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final IndexPrimeTuple other = (IndexPrimeTuple) obj;
        if (this.index == null) {
            if (other.index != null) {
                return false;
            }
        } else if (!this.index.equals(other.index)) {
            return false;
        }
        if (this.primeNumber == null) {
            if (other.primeNumber != null) {
                return false;
            }
        } else if (!this.primeNumber.equals(other.primeNumber)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "IndexPrimeTuple [index=" + this.index + ", primeNumber=" + this.primeNumber + "]";
    }

}