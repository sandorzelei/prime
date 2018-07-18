package com.intland.prime.controller.model;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class PrimeForm implements Serializable {

    private static final long serialVersionUID = 3370750106308019422L;

    @NotNull
    @Min(1)
    private Long number;

    public Long getNumber() {
        return this.number;
    }

    public void setNumber(final Long number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "PrimeForm [number=" + this.number + "]";
    }

}