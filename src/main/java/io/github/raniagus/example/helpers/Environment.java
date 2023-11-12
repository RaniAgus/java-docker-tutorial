package io.github.raniagus.example.helpers;

import java.util.function.Supplier;

public enum Environment {;
  public static boolean isDevelopment() {
    return System.getenv("PRODUCTION") == null;
  }

  public static String getVariable(String key) {
    return getVariableOrDefault(key, () -> {
      throw new IllegalStateException("Environment variable " + key + " is required");
    });
  }

  public static String getVariableOrDefault(String key, Supplier<String> defaultValue) {
    var value = System.getenv(key);
    return value != null ? value : defaultValue.get();
  }
}
