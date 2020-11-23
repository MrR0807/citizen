package com.good.citizen.shared.validator;

import com.good.citizen.shared.Result;

public interface Validation<T> {

    boolean supports(T t);

    Result validate(T t);
}