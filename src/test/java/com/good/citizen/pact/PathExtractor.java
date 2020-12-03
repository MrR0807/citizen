package com.good.citizen.pact;

import java.util.regex.Pattern;

public class PathExtractor {

    private static final Pattern pattern = Pattern.compile("/api.*");

    private PathExtractor() {
    }

    public static String extract(String url) {
        var matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            throw new RuntimeException("Could not extract");
        }
    }
}