package com.arcbees.carsample.shared.dto;

public class NumberDto<T extends Number> implements Dto {
    private static final long serialVersionUID = 1819716677848054360L;

    private T number;

    @SuppressWarnings("unused")
    protected NumberDto() {
        // Needed for serialization
    }

    public NumberDto(T number) {
        this.number = number;
    }

    public T getNumber() {
        return number;
    }

    public void setNumber(T number) {
        this.number = number;
    }
}
