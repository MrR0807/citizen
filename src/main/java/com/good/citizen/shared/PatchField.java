package com.good.citizen.shared;

import java.util.function.Consumer;

public record PatchField<T>(boolean isSet, T value) {

    public PatchField(T value) {
        this(true, value);
    }

    public static <T> PatchField<T> empty() {
        return new PatchField<>(false, null);
    }

    public void ifSet(Consumer<? super T> action) {
        if (this.isSet) {
            action.accept(this.value);
        }
    }
}