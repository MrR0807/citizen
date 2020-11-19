package com.good.citizen.shared;

import java.util.Objects;
import java.util.function.Consumer;

public class PatchField<T> {

    private final boolean isSet;
    private final T value;

    public PatchField(T value) {
        this.isSet = true;
        this.value = value;
    }

    private PatchField(boolean isSet, T value) {
        this.isSet = isSet;
        this.value = value;
    }

    public static <T> PatchField<T> empty() {
        return new PatchField<>(false, null);
    }

    public boolean isSet() {
        return this.isSet;
    }

    public T getValue() {
        return this.value;
    }

    public void ifSet(Consumer<? super T> action) {
        if (this.isSet) {
            action.accept(this.value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        var that = (PatchField<?>) o;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    @Override
    public String toString() {
        return "PatchField{" +
                "isSet=" + this.isSet +
                ", value=" + this.value +
                '}';
    }
}