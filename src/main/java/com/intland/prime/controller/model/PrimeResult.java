package com.intland.prime.controller.model;

import java.io.Serializable;
import java.math.BigInteger;

public class PrimeResult implements Serializable {

    private static final long serialVersionUID = 3370750106308019422L;

    private BigInteger number;

    public PrimeResult() {
        super();
    }

    public PrimeResult(final BigInteger number) {
        super();
        this.number = number;
    }

    public BigInteger getNumber() {
        return this.number;
    }

    public void setNumber(final BigInteger number) {
        this.number = number;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.number == null ? 0 : this.number.hashCode());
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
        final PrimeResult other = (PrimeResult) obj;
        if (this.number == null) {
            if (other.number != null) {
                return false;
            }
        } else if (!this.number.equals(other.number)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PrimeResult [number=" + this.number + "]";
    }

}