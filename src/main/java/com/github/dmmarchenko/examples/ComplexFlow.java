package com.github.dmmarchenko.examples;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComplexFlow {

    public static void main(String[] args) {
        Context context = new Context();
    }

    @SneakyThrows
    private static <T> T longMap(T object, int duration) {
        TimeUnit.SECONDS.sleep(duration);
        return object;
    }

    private static class Context {

        private final Map<String, String> values = new HashMap<>();

        private void put(String key, String value) {
            values.put(key, value);
        }

        private String get(String key) {
            return values.get(key);
        }
    }
}
