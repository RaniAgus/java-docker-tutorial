package io.github.raniagus.example.helpers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum URLUtil {;
  @SafeVarargs
  public static String pathWithParams(String path, Map.Entry<String, String>... queryParams) {
    return path + Stream.of(queryParams)
        .map(e -> "%s=%s".formatted(
            URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8),
            URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8)))
        .collect(Collectors.joining("&", "?", ""));
  }
}
