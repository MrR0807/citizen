package com.good.citizen.config;

import com.good.citizen.shared.PatchField;

import javax.validation.valueextraction.ExtractedValue;
import javax.validation.valueextraction.ValueExtractor;

public class PatchFieldValueExtractor implements ValueExtractor<PatchField<@ExtractedValue ?>> {

    @Override
    public void extractValues(PatchField<?> originalValue, ValueReceiver receiver) {
        receiver.value(null, originalValue.value());
    }
}