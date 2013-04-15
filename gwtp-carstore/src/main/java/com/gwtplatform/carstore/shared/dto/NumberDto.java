package com.gwtplatform.carstore.shared.dto;

public class NumberDto<T extends Number> implements Dto {
    private T number;

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
