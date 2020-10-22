package com.good.citizen.shared;

import java.util.List;
import java.util.Optional;

public class RepoUtils {

    private RepoUtils() {
    }

    public static <T> Optional<T> fromResultListToOptional(List<T> result) {
        return Optional.ofNullable(result.isEmpty() ? null : result.get(0));
    }
}